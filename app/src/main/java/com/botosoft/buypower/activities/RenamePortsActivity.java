package com.botosoft.buypower.activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.botosoft.buypower.R;
import com.botosoft.buypower.Utilities.ConnectionClass;
import com.botosoft.buypower.activities.ui.controls.ControlsFragment;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Connection;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RenamePortsActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference product_serial_no, users;

    ConnectionClass connectionClass;
    String portSize;

    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    static FirebaseUser User = mAuth.getCurrentUser();
    static final String userID = User.getUid();

    EditText masterPort, port2, port3, port4, port5, port6;
    Button rename;
    String _customerId;
    ProgressDialog progressDialog;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rename_ports);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        product_serial_no = database.getReference().child("product_serial_no");
        users = database.getReference("users");

        connectionClass = new ConnectionClass();

        initUi();
    }


    public void initUi(){

        progressBar = (ProgressBar) findViewById(R.id.spin_kit);
        Sprite doubleBounce = new DoubleBounce();
        progressBar.setIndeterminateDrawable(doubleBounce);
        progressBar.setVisibility(View.VISIBLE);


        masterPort = (EditText) findViewById(R.id.master_port);
        port2 = (EditText) findViewById(R.id.port_2);
        port3 = (EditText) findViewById(R.id.port_3);
        port4 = (EditText) findViewById(R.id.port_4);
        port5 = (EditText) findViewById(R.id.port_5);
        port6 = (EditText) findViewById(R.id.port_6);
        rename = (Button) findViewById(R.id._rename);

        masterPort.setVisibility(View.INVISIBLE);
        port2.setVisibility(View.INVISIBLE);
        port3.setVisibility(View.INVISIBLE);
        port4.setVisibility(View.INVISIBLE);
        port5.setVisibility(View.INVISIBLE);
        port6.setVisibility(View.INVISIBLE);

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int portNo = Integer.parseInt(String.valueOf(dataSnapshot.child(userID).child("portNo").getValue()));
                String customerId = String.valueOf(dataSnapshot.child(userID).child("customerId").getValue());


                String port1Name = String.valueOf(dataSnapshot.child(userID).child("devices").child(customerId).child("port1").child("name").getValue());
                String port2Name = String.valueOf(dataSnapshot.child(userID).child("devices").child(customerId).child("port2").child("name").getValue());
                String port3Name = String.valueOf(dataSnapshot.child(userID).child("devices").child(customerId).child("port3").child("name").getValue());
                String port4Name = String.valueOf(dataSnapshot.child(userID).child("devices").child(customerId).child("port4").child("name").getValue());

                masterPort.setHint(port1Name);
                port2.setHint(port2Name);
                port3.setHint(port3Name);
                port4.setHint(port4Name);

                //Toast.makeText(RenamePortsActivity.this, "port1Name: " + port1Name, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);

                masterPort.setVisibility(View.VISIBLE);
                port2.setVisibility(View.VISIBLE);
                port3.setVisibility(View.VISIBLE);
                port4.setVisibility(View.VISIBLE);

                if(portNo ==  6){
                    String port5Name = String.valueOf(dataSnapshot.child(userID).child("devices").child(customerId).child("port5").child("name").getValue());
                    String port6Name = String.valueOf(dataSnapshot.child(userID).child("devices").child(customerId).child("port6").child("name").getValue());

                    port5.setHint(port5Name);
                    port6.setHint(port6Name);

                    port5.setVisibility(View.VISIBLE);
                    port6.setVisibility(View.VISIBLE);
                }






            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(RenamePortsActivity.this);
                progressDialog.setMessage("Renaming Ports...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                renamePort();
                Toast.makeText(getApplication(), "Rename was successful...", Toast.LENGTH_SHORT).show();
                //Dorename dorename = new Dorename();
                //dorename.execute("");

            }
        });

    }

    public void renamePort(){

        users.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                _customerId = String.valueOf(dataSnapshot.child("customerId").getValue());

                String _masterPort = String.valueOf(masterPort.getText());
                String _port2 = String.valueOf(port2.getText());
                String _port3 = String.valueOf(port3.getText());
                String _port4 = String.valueOf(port4.getText());
                String _port5 = String.valueOf(port5.getText());
                String _port6 = String.valueOf(port6.getText());

                if(!_masterPort.equals("")){
                    users.child(userID).child("devices").child(_customerId).child("port1").child("name").setValue(_masterPort);
                }

                if(!_port2.equals("")){
                    users.child(userID).child("devices").child(_customerId).child("port2").child("name").setValue(_port2);
                }

                if(!_port3.equals("")){
                    users.child(userID).child("devices").child(_customerId).child("port3").child("name").setValue(_port3);
                }

                if(!_port4.equals("")){
                    users.child(userID).child("devices").child(_customerId).child("port4").child("name").setValue(_port4);
                }

                if(!_port5.equals("")){
                    users.child(userID).child("devices").child(_customerId).child("port5").child("name").setValue(_port5);
                }

                if(!_port6.equals("")){
                    users.child(userID).child("devices").child(_customerId).child("port6").child("name").setValue(_port6);
                }

                progressDialog.dismiss();
                //Toast.makeText(getApplication(), "Rename was successful...", Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                progressDialog.dismiss();
                //Toast.makeText(getApplication(), "Ooops... rename failed please try again", Toast.LENGTH_SHORT).show();

            }



        });

    }

    public class Dorename extends AsyncTask<String,String,String>
    {
        boolean isSuccess=false;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {


            final String[] z = new String[1];
            users.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    _customerId = String.valueOf(dataSnapshot.child("customerId").getValue());


                    String _masterPort = String.valueOf(masterPort.getText());
                    String _port2 = String.valueOf(port2.getText());
                    String _port3 = String.valueOf(port3.getText());
                    String _port4 = String.valueOf(port4.getText());
                    String _port5 = String.valueOf(port5.getText());
                    String _port6 = String.valueOf(port6.getText());

                    try {
                        Connection con = connectionClass.CONN();

                        Log.e("check2", String.valueOf(con));
                        if (con == null) {
                            Log.e("check", "got here 1" );
                            z[0] = "Please check your internet connection";

                        } else {

                            Log.e("check", "got here 2" );

                            if(_masterPort.equals("")){
                                _masterPort = "Master Port";
                            }else if(_port2.equals("")){
                                _port2 = "Port 2";
                            }else if(_port3.equals("")){
                                _port3 = "Port 3";
                            }else if(_port4.equals("")){
                                _port4 = "Port 4";
                            }else if(_port5.equals("")){
                                _port5 = "Port 5";
                            }else if(_port6.equals("")){
                                _port6 = "Port 6";
                            }

                            String query= "UPDATE port_names SET port1_name = '"+_masterPort+"' WHERE customer_id = '"+_customerId+"'";
                            Statement stmt = con.createStatement();
                            stmt.executeUpdate(query);

                            String query2= "UPDATE port_names SET port2_name = '"+_port2+"' WHERE customer_id = '"+_customerId+"'";
                            Statement stmt2 = con.createStatement();
                            stmt2.executeUpdate(query2);

                            String query3= "UPDATE port_names SET port3_name = '"+_port3+"' WHERE customer_id = '"+_customerId+"'";
                            Statement stmt3 = con.createStatement();
                            stmt3.executeUpdate(query3);

                            String query4= "UPDATE port_names SET port4_name = '"+_port4+"' WHERE customer_id = '"+_customerId+"'";
                            Statement stmt4 = con.createStatement();
                            stmt4.executeUpdate(query4);

                            String query5= "UPDATE port_names SET port5_name = '"+_port5+"' WHERE customer_id = '"+_customerId+"'";
                            Statement stmt5 = con.createStatement();
                            stmt5.executeUpdate(query5);

                            String query6= "UPDATE port_names SET port6_name = '"+_port6+"' WHERE customer_id = '"+_customerId+"'";
                            Statement stmt6 = con.createStatement();
                            stmt6.executeUpdate(query6);

                            

                            Log.e("check", "got here 4" );


                            z[0] = "Registeration successfull";
                            isSuccess=true;


                        }
                    }
                    catch (Exception ex)
                    {
                        Log.e("check", "got here 3" );
                        isSuccess = false;
                        z[0] = "Exceptions"+ex;
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }



            });



            return z[0];
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            if(isSuccess) {
                Log.e("check", "Launched do in bg2");
                Toast.makeText(RenamePortsActivity.this, "Ports Successfully Renamed", Toast.LENGTH_SHORT).show();

            }else{
                Toast.makeText(RenamePortsActivity.this, "Rename Unsuccessful", Toast.LENGTH_SHORT).show();

            }


        }
    }


}
