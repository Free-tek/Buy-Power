package com.botosoft.buypower.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.botosoft.buypower.Interface.ItemClickListener;
import com.botosoft.buypower.Model.PowerConsumedModel;
import com.botosoft.buypower.R;
import com.botosoft.buypower.ViewHolder.PowerConsumedViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PowerConsumedActivity  extends AppCompatActivity {

    //Fitrebase
    FirebaseDatabase database;
    DatabaseReference users, powerConsumedList;
    FirebaseRecyclerAdapter<PowerConsumedModel, PowerConsumedViewHolder> adapter ;

    //View
    RecyclerView recycler_menu;
    LinearLayoutManager layoutManager;

    String customerId, port1Name, port2Name, port3Name, port4Name, port5Name, port6Name;

    ProgressBar progressBar;

    int portNo;

    static private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    static FirebaseUser user = mAuth.getCurrentUser();
    static final String userID = user.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_consumed);

        progressBar = (ProgressBar)findViewById(R.id.spin_kit);
        Sprite doubleBounce = new DoubleBounce();
        progressBar.setIndeterminateDrawable(doubleBounce);
        progressBar.setVisibility(View.VISIBLE);

        //Initialise Firebase
        database = FirebaseDatabase.getInstance();
        users = database.getReference("users").child(userID);

        //Initialise View
        recycler_menu = (RecyclerView)findViewById(R.id.recycler_power_consumed);
        recycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                customerId = String.valueOf(dataSnapshot.child("customerId").getValue());
                portNo = Integer.parseInt(String.valueOf(dataSnapshot.child("portNo").getValue()));

                port1Name = String.valueOf(dataSnapshot.child("devices").child(customerId).child("port1").child("name").getValue());
                port2Name = String.valueOf(dataSnapshot.child("devices").child(customerId).child("port2").child("name").getValue());
                port3Name = String.valueOf(dataSnapshot.child("devices").child(customerId).child("port3").child("name").getValue());
                port4Name = String.valueOf(dataSnapshot.child("devices").child(customerId).child("port4").child("name").getValue());

                powerConsumedList = database.getReference("users").child(userID).child("devices").child(customerId).child("daily_power_logs");


                if(portNo == 6){
                    port5Name = String.valueOf(dataSnapshot.child("devices").child(customerId).child("port5").child("name").getValue());
                    port6Name = String.valueOf(dataSnapshot.child("devices").child(customerId).child("port6").child("name").getValue());
                    loadMenuSixPorts();
                }else{
                    loadMenuFourPorts();
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void loadMenuFourPorts() {


        Query query = FirebaseDatabase.getInstance()
                .getReference().child("users").child(userID).child("devices").child(customerId).child("daily_power_logs");

        FirebaseRecyclerOptions<PowerConsumedModel> options =
                new FirebaseRecyclerOptions.Builder<PowerConsumedModel>()
                        .setQuery(query, new SnapshotParser<PowerConsumedModel>() {
                            @NonNull
                            @Override
                            public PowerConsumedModel parseSnapshot(@NonNull DataSnapshot snapshot) {
                                String key =  snapshot.getKey();
                                String snapshot1 = "";
                                String snapshot2 = "";
                                String snapshot3 = "";
                                String snapshot4 = "";


                                if (snapshot.child("port1").exists()){
                                    snapshot1 = snapshot.child("port1").getValue().toString();
                                }

                                if (snapshot.child("port2").exists()){
                                    snapshot2 = snapshot.child("port2").getValue().toString();
                                }

                                if (snapshot.child("port3").exists()){
                                    snapshot3= snapshot.child("port3").getValue().toString();
                                }
                                if (snapshot.child("port4").exists()){
                                    snapshot4 = snapshot.child("port4").getValue().toString();
                                }





                                return new PowerConsumedModel(
                                        key,
                                        snapshot1,
                                        snapshot2,
                                        snapshot3,
                                        snapshot4,
                                        "",
                                        "",
                                        port1Name,
                                        port2Name,
                                        port3Name,
                                        port4Name,
                                        "",
                                        "",
                                        ""
                                );
                            }
                        })
                        .build();



        adapter = new FirebaseRecyclerAdapter<PowerConsumedModel, PowerConsumedViewHolder>(options) {
            @NonNull
            @Override
            public PowerConsumedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.power_consumed_item, parent, false);
                return new PowerConsumedViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull PowerConsumedViewHolder viewHolder, int i, @NonNull PowerConsumedModel model) {
                viewHolder.port1Name.setText(model.getPort1Name());
                viewHolder.port2Name.setText(model.getPort2Name());
                viewHolder.port3Name.setText(model.getPort3Name());
                viewHolder.port4Name.setText(model.getPort4Name());
                viewHolder.port5Name.setVisibility(View.INVISIBLE);
                viewHolder.port6Name.setVisibility(View.INVISIBLE);
                viewHolder.port1.setText(model.getPort1());
                viewHolder.port2.setText(model.getPort2());
                viewHolder.port3.setText(model.getPort3());
                viewHolder.port4.setText(model.getPort4());
                viewHolder.port5.setVisibility(View.INVISIBLE);
                viewHolder.port6.setVisibility(View.INVISIBLE);




                progressBar.setVisibility(View.INVISIBLE);


                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {




                    }
                });
            }



        };

        adapter.startListening();
        adapter.notifyDataSetChanged(); //Refresh data if changed
        recycler_menu.setAdapter(adapter);


    }


    //Create Load Menu method below.
    private void loadMenuSixPorts() {


        Query query = FirebaseDatabase.getInstance()
                .getReference().child("users").child(userID).child("devices").child(customerId).child("daily_power_logs");

        //Log.e("check", "this is your customer id" + customerId + ":::" + query );
        FirebaseRecyclerOptions<PowerConsumedModel> options =
                new FirebaseRecyclerOptions.Builder<PowerConsumedModel>()
                        .setQuery(query, new SnapshotParser<PowerConsumedModel>() {
                            @NonNull
                            @Override
                            public PowerConsumedModel parseSnapshot(@NonNull DataSnapshot snapshot) {
                                Log.e("check", "this is your customer id" + customerId + ":::" + snapshot.getKey() );

                                String key =  snapshot.getKey();
                                String snapshot1 = "";
                                String snapshot2 = "";
                                String snapshot3 = "";
                                String snapshot4 = "";
                                String snapshot5 = "";
                                String snapshot6 = "";

                                if (snapshot.child("port1").exists()){
                                    snapshot1 = snapshot.child("port1").getValue().toString();
                                }

                                if (snapshot.child("port2").exists()){
                                    snapshot2 = snapshot.child("port2").getValue().toString();
                                }

                                if (snapshot.child("port3").exists()){
                                    snapshot3= snapshot.child("port3").getValue().toString();
                                }
                                if (snapshot.child("port4").exists()){
                                    snapshot4 = snapshot.child("port4").getValue().toString();
                                }
                                if (snapshot.child("port5").exists()){
                                    snapshot5 = snapshot.child("port5").getValue().toString();
                                }
                                if (snapshot.child("port6").exists()){
                                    snapshot6 = snapshot.child("port6").getValue().toString();
                                }

                                return new PowerConsumedModel(
                                        key,
                                        snapshot1,
                                        snapshot2,
                                        snapshot3,
                                        snapshot4,
                                        snapshot5,
                                        snapshot6,
                                        port1Name,
                                        port2Name,
                                        port3Name,
                                        port4Name,
                                        port5Name,
                                        port6Name,
                                        ""
                                );

                            }
                        })
                        .build();



        adapter = new FirebaseRecyclerAdapter<PowerConsumedModel, PowerConsumedViewHolder>(options) {
            @NonNull
            @Override
            public PowerConsumedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.power_consumed_item, parent, false);
                return new PowerConsumedViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull PowerConsumedViewHolder viewHolder, int i, @NonNull PowerConsumedModel model) {
                viewHolder.port1Name.setText(model.getPort1Name());
                viewHolder.port2Name.setText(model.getPort2Name());
                viewHolder.port3Name.setText(model.getPort3Name());
                viewHolder.port4Name.setText(model.getPort4Name());
                viewHolder.port5Name.setText(model.getPort5Name());
                viewHolder.port6Name.setText(model.getPort6Name());
                viewHolder.port1.setText(model.getPort1());
                viewHolder.port2.setText(model.getPort2());
                viewHolder.port3.setText(model.getPort3());
                viewHolder.port4.setText(model.getPort4());
                viewHolder.port5.setText(model.getPort5());
                viewHolder.port6.setText(model.getPort6());

                Date date = new Date(Integer.parseInt(model.getTimeStamp()) *1000L); // convert seconds to milliseconds
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // the format of your date
                String formattedDate = dateFormat.format(date);
                System.out.println(formattedDate);

                viewHolder.time.setText(formattedDate);


                //TODO: New text code for recycler item click
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });
            }



        };

        adapter.startListening();
        adapter.notifyDataSetChanged(); //Refresh data if changed
        recycler_menu.setAdapter(adapter);


    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

    }

    @Override
    protected void onStart(){
        super.onStart();


    }

    @Override
    protected void onStop(){
        super.onStop();
        adapter.stopListening();

    }




}
