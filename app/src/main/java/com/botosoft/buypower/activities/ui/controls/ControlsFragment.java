package com.botosoft.buypower.activities.ui.controls;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.botosoft.buypower.R;
import com.botosoft.buypower.Utilities.ConnectionClass;
import com.botosoft.buypower.activities.SetUpAccountActivity;
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
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ControlsFragment extends Fragment {

    private ControlsViewModel homeViewModel;
    RelativeLayout masterPort, port2, port3, port4, port5, port6;
    TextView masterPort1Name, port2Name, port3Name, port4Name, port5Name, port6Name, powerText;
    TextView masterPort1Consumption, port2Consumption, port3Consumption, port4Consumption, port5Consumption, port6Consumption;
    ImageView onlineStats1, onlineStats2, onlineStats3, onlineStats4, onlineStats5, onlineStats6, power;
    Button port1Switch, port2Switch, port3Switch, port4Switch, port5Switch, port6Switch;
    ProgressBar progressBar;
    ConnectionClass connectionClass;
    Button refresh;

    String _customerId;
    int _port, _portNo;

    String $power_availability, $port_size, $port1_power, $port2_power, $port3_power, $port4_power, $port5_power,
            $port6_power, $port1_online, $port2_online, $port3_online, $port4_online, $port5_online, $port6_online,
            $port1_powerConsumption, $port2_powerConsumption, $port3_powerConsumption,
            $port4_powerConsumption, $port5_powerConsumption, $port6_powerConsumption, $port1_name, $port2_name, $port3_name, $port4_name, $port5_name, $port6_name, $last_report_timestamp;


    //Firebase
    FirebaseDatabase database;
    DatabaseReference users;


    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    static FirebaseUser User = mAuth.getCurrentUser();
    static final String userID = User.getUid();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(ControlsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_controls, container, false);

        connectionClass = new ConnectionClass();
        //Initialise Firebase
        database = FirebaseDatabase.getInstance();
        users = database.getReference("users");

        initUi(root);

        users.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                _customerId = String.valueOf(dataSnapshot.child("customerId").getValue());
                _portNo = Integer.parseInt(String.valueOf(dataSnapshot.child("portNo").getValue()));
                //LoadData loadData = new LoadData();
                //loadData.execute("");
                fetchStatus();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });


        return root;
    }

    public void initUi(View view) {

        progressBar = (ProgressBar) view.findViewById(R.id.spin_kit);
        Sprite doubleBounce = new DoubleBounce();
        progressBar.setIndeterminateDrawable(doubleBounce);
        progressBar.setVisibility(View.VISIBLE);

        masterPort = (RelativeLayout) view.findViewById(R.id.master_port);
        port2 = (RelativeLayout) view.findViewById(R.id.port2);
        port3 = (RelativeLayout) view.findViewById(R.id.port3);
        port4 = (RelativeLayout) view.findViewById(R.id.port4);
        port5 = (RelativeLayout) view.findViewById(R.id.port5);
        port6 = (RelativeLayout) view.findViewById(R.id.port6);

        masterPort.setVisibility(View.INVISIBLE);
        port2.setVisibility(View.INVISIBLE);
        port3.setVisibility(View.INVISIBLE);
        port4.setVisibility(View.INVISIBLE);
        port5.setVisibility(View.INVISIBLE);
        port6.setVisibility(View.INVISIBLE);

        masterPort1Name = (TextView) view.findViewById(R.id.master_port1_name);
        port2Name = (TextView) view.findViewById(R.id.port2_name);
        port3Name = (TextView) view.findViewById(R.id.port3_name);
        port4Name = (TextView) view.findViewById(R.id.port4_name);
        port5Name = (TextView) view.findViewById(R.id.port5_name);
        port6Name = (TextView) view.findViewById(R.id.port6_name);

        masterPort1Consumption = (TextView) view.findViewById(R.id.master_power_consum);
        port2Consumption = (TextView) view.findViewById(R.id.port2_consum);
        port3Consumption = (TextView) view.findViewById(R.id.port3_consum);
        port4Consumption = (TextView) view.findViewById(R.id.port4_consum);
        port5Consumption = (TextView) view.findViewById(R.id.port5_consum);
        port6Consumption = (TextView) view.findViewById(R.id.port6_consum);

        onlineStats1 = (ImageView) view.findViewById(R.id.online_stats1);
        onlineStats2 = (ImageView) view.findViewById(R.id.online_stats2);
        onlineStats3 = (ImageView) view.findViewById(R.id.online_stats3);
        onlineStats4 = (ImageView) view.findViewById(R.id.online_stats4);
        onlineStats5 = (ImageView) view.findViewById(R.id.online_stats5);
        onlineStats6 = (ImageView) view.findViewById(R.id.online_stats6);

        power = (ImageView) view.findViewById(R.id.power);
        powerText = (TextView) view.findViewById(R.id.power_text);


        port1Switch = (Button) view.findViewById(R.id.port1_switch);
        port2Switch = (Button) view.findViewById(R.id.port2_switch);
        port3Switch = (Button) view.findViewById(R.id.port3_switch);
        port4Switch = (Button) view.findViewById(R.id.port4_switch);
        port5Switch = (Button) view.findViewById(R.id.port5_switch);
        port6Switch = (Button) view.findViewById(R.id.port6_switch);

        refresh = (Button) view.findViewById(R.id.refresh);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Reload current fragment

                Log.e("info", "clicked");

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (Build.VERSION.SDK_INT >= 26) {
                    ft.setReorderingAllowed(false);
                }
                ft.detach(ControlsFragment.this).attach(ControlsFragment.this).commit();
            }
        });


        port1Switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                powerOnOff($port1_power, $port1_online, 1);
            }
        });

        port2Switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                powerOnOff($port2_power, $port2_online, 2);
            }
        });

        port3Switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                powerOnOff($port3_power, $port3_online, 3);
            }
        });

        port4Switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                powerOnOff($port4_power, $port4_online, 4);
            }
        });

        port5Switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                powerOnOff($port5_power, $port5_online, 5);
            }
        });

        port6Switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                powerOnOff($port6_power, $port6_online, 6);
            }
        });

    }


    private void powerOnOff(String power, String onlineStats, final int port) {

        if(!isNetworkAvailable()) {
            Toast.makeText(getActivity(), "Couldn't switch port on, please check your internet connection", Toast.LENGTH_SHORT).show();
        }if (Integer.parseInt(power) == 0) {
            //port is off


            //----------

            if (Integer.parseInt($power_availability) == 0) {
                //power not available at the apartment

                AlertDialog alertDialog1 = new AlertDialog.Builder(getActivity()).create();
                alertDialog1.setTitle("Ooops...");
                alertDialog1.setMessage("Power is currently unavailable at your apartment, but we will switch the port on once they restore power");
                alertDialog1.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog1.setButton(AlertDialog.BUTTON_POSITIVE, "Proceed",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                switchOn(port);

                            }
                        });

                alertDialog1.show();

            } else if (Integer.parseInt(onlineStats) == 0) {

                //device is offline and power is available
                AlertDialog alertDialog1 = new AlertDialog.Builder(getActivity()).create();
                alertDialog1.setTitle("Ooops...");
                alertDialog1.setMessage("This port is currently offline, we lost connection with it but once it is back on, we will switch it on");
                alertDialog1.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog1.setButton(AlertDialog.BUTTON_POSITIVE, "Proceed",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                switchOn(port);

                            }
                        });

                alertDialog1.show();


            } else {
                //device is online and power is available

                AlertDialog alertDialog1 = new AlertDialog.Builder(getActivity()).create();
                alertDialog1.setTitle("Proceed?");
                alertDialog1.setMessage("Do you want to switch on this port?");
                alertDialog1.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog1.setButton(AlertDialog.BUTTON_POSITIVE, "Proceed",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                switchOn(port);

                            }
                        });

                alertDialog1.show();
            }

        } else {

            if (Integer.parseInt($power_availability) == 0) {
                //power not available at the apartment

                final AlertDialog alertDialog1 = new AlertDialog.Builder(getActivity()).create();
                alertDialog1.setTitle("Ooops...");
                alertDialog1.setMessage("Power is currently unavailable at your apartment, but we will switch off the port once they restore power");
                alertDialog1.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog1.setButton(AlertDialog.BUTTON_POSITIVE, "Proceed",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {

                                switchOff(port);

                            }
                        });
                alertDialog1.show();

            } else if (Integer.parseInt(onlineStats) == 0) {
                //port is offline

                final AlertDialog alertDialog1 = new AlertDialog.Builder(getActivity()).create();
                alertDialog1.setTitle("Ooops...");
                alertDialog1.setMessage("This port is currently offline, we lost connection with it but once it is back on, we will switch it off");
                alertDialog1.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog1.setButton(AlertDialog.BUTTON_POSITIVE, "Proceed",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {

                                switchOff(port);

                            }
                        });
                alertDialog1.show();

            } else {
                //power is available and port is online

                final AlertDialog alertDialog1 = new AlertDialog.Builder(getActivity()).create();
                alertDialog1.setTitle("Proceed?");
                alertDialog1.setMessage("Power off this port");
                alertDialog1.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog1.setButton(AlertDialog.BUTTON_POSITIVE, "Proceed",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {

                                switchOff(port);
                            }
                        });
                alertDialog1.show();

            }


        }


    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void switchOn(final int port) {

        DatabaseReference command = database.getReference().child("commands").child(_customerId);
        command.child("port"+port).child("power_status").setValue(1);
        Toast.makeText(getActivity(), "The request has been sent.", Toast.LENGTH_SHORT).show();




        /*String z;
        try {
            final Connection con = connectionClass.CONN();

            if (con == null) {
                Log.e("check", "got here 1");
                z = "Please check your internet connection";

            } else {

                Log.e("check", "got here 2");

                Date currentDate = (Date) java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("Africa/Lagos")).getTime();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                final String date = dateFormat.format(currentDate);


                if (port == 1) {

                    *//*String query= "UPDATE power_availability SET port1_power = '1' WHERE customer_id = '"+_customerId+"'";
                    Statement stmt = con.createStatement();
                    stmt.executeUpdate(query);

                    z = "Registeration successfull";
                    $port1_power = "1";

                    port1Switch.setBackground(getResources().getDrawable(R.drawable.on));
                    port1Switch.setVisibility(View.VISIBLE);*//*


                    String requestBody = "#0#on#";

                    String sql = "insert into iot_requests " + " (device_id, request_body, status, time)"
                            + " values ('" + _customerId + "','" + requestBody + "','0','" + date + "')";
                    Statement stmt2 = con.createStatement();
                    stmt2.executeUpdate(sql);


                } else if (port == 2) {
                    *//*
                    String query= "UPDATE power_availability SET port2_power = '1' WHERE customer_id = '"+_customerId+"'";
                    Statement stmt = con.createStatement();
                    stmt.executeUpdate(query);

                    z = "Registeration successfull";
                    $port2_power = "1";


                    port2Switch.setBackground(getResources().getDrawable(R.drawable.on));
                    port2Switch.setVisibility(View.VISIBLE);
                     *//*

                    String requestBody = "#2#on#";

                    String sql = "insert into iot_requests " + " (device_id, request_body, status, time)"
                            + " values ('" + _customerId + "','" + requestBody + "','0','" + date + "')";
                    Statement stmt2 = con.createStatement();
                    stmt2.executeUpdate(sql);

                } else if (port == 3) {
                    *//*String query= "UPDATE power_availability SET port3_power = '1' WHERE customer_id = '"+_customerId+"'";
                    Statement stmt = con.createStatement();
                    stmt.executeUpdate(query);

                    z = "Registeration successfull";

                    $port3_power = "1";

                    port3Switch.setBackground(getResources().getDrawable(R.drawable.on));
                    port3Switch.setVisibility(View.VISIBLE);*//*

                    String requestBody = "#3#on#";
                    String sql = "insert into iot_requests " + " (device_id, request_body, status, time)"
                            + " values ('" + _customerId + "','" + requestBody + "','0','" + date + "')";
                    Statement stmt2 = con.createStatement();
                    stmt2.executeUpdate(sql);

                } else if (port == 4) {
                    *//*String query= "UPDATE power_availability SET port4_power = '1' WHERE customer_id = '"+_customerId+"'";
                    Statement stmt = con.createStatement();
                    stmt.executeUpdate(query);

                    z = "Registeration successfull";

                    $port4_power = "1";

                    port4Switch.setBackground(getResources().getDrawable(R.drawable.on));
                    port4Switch.setVisibility(View.VISIBLE);*//*

                    String requestBody = "#4#on#";
                    String sql = "insert into iot_requests " + " (device_id, request_body, status, time)"
                            + " values ('" + _customerId + "','" + requestBody + "','0','" + date + "')";
                    Statement stmt2 = con.createStatement();
                    stmt2.executeUpdate(sql);

                } else if (port == 5) {
                    *//*String query= "UPDATE power_availability SET port5_power = '1' WHERE customer_id = '"+_customerId+"'";
                    Statement stmt = con.createStatement();
                    stmt.executeUpdate(query);

                    z = "Registeration successfull";

                    $port5_power = "1";

                    port5Switch.setBackground(getResources().getDrawable(R.drawable.on));
                    port5Switch.setVisibility(View.VISIBLE);*//*

                    String requestBody = "#5#on#";
                    String sql = "insert into iot_requests " + " (device_id, request_body, status, time)"
                            + " values ('" + _customerId + "','" + requestBody + "','0','" + date + "')";
                    Statement stmt2 = con.createStatement();
                    stmt2.executeUpdate(sql);

                } else if (port == 6) {
                    *//*String query= "UPDATE power_availability SET port6_power = '1' WHERE customer_id = '"+_customerId+"'";
                    Statement stmt = con.createStatement();
                    stmt.executeUpdate(query);

                    z = "Registeration successfull";

                    $port6_power = "1";

                    port6Switch.setBackground(getResources().getDrawable(R.drawable.on));
                    port6Switch.setVisibility(View.VISIBLE);*//*

                    String requestBody = "#6#on#";
                    String sql = "insert into iot_requests " + " (device_id, request_body, status, time)"
                            + " values ('" + _customerId + "','" + requestBody + "','0','" + date + "')";
                    Statement stmt2 = con.createStatement();
                    stmt2.executeUpdate(sql);
                }


            }
        } catch (Exception ex) {
            Log.e("check", "got here 3");
            z = "Exceptions" + ex;
        }*/

    }


    private void switchOff(int port) {

        DatabaseReference command = database.getReference().child("commands").child(_customerId);
        command.child("port"+port).child("power_status").setValue(0);
        Toast.makeText(getActivity(), "The request has been sent.", Toast.LENGTH_SHORT).show();



        /*String z;
        try {
            Connection con = connectionClass.CONN();

            if (con == null) {
                Log.e("check", "got here 1");
                z = "Please check your internet connection";

            } else {

                Log.e("check", "got here 2");

                Date currentDate = (Date) java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("Africa/Lagos")).getTime();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                final String date = dateFormat.format(currentDate);


                if (port == 1) {
                    *//*String query= "UPDATE power_availability SET port1_power = '0' WHERE customer_id = '"+_customerId+"'";
                    Statement stmt = con.createStatement();
                    stmt.executeUpdate(query);



                    z = "Registeration successfull";
                    $port1_power = "1";

                    port1Switch.setBackground(getResources().getDrawable(R.drawable.off));
                    port1Switch.setVisibility(View.VISIBLE);*//*

                    String requestBody = "#0#off#";
                    String sql = "insert into iot_requests " + " (device_id, request_body, status, time)"
                            + " values ('" + _customerId + "','" + requestBody + "','0','" + date + "')";
                    Statement stmt2 = con.createStatement();
                    stmt2.executeUpdate(sql);
                } else if (port == 2) {
                    *//*String query= "UPDATE power_availability SET port2_power = '0' WHERE customer_id = '"+_customerId+"'";
                    Statement stmt = con.createStatement();
                    stmt.executeUpdate(query);

                    z = "Registeration successfull";
                    $port2_power = "1";


                    port2Switch.setBackground(getResources().getDrawable(R.drawable.off));
                    port2Switch.setVisibility(View.VISIBLE);*//*

                    String requestBody = "#2#off#";
                    String sql = "insert into iot_requests " + " (device_id, request_body, status, time)"
                            + " values ('" + _customerId + "','" + requestBody + "','0','" + date + "')";
                    Statement stmt2 = con.createStatement();
                    stmt2.executeUpdate(sql);

                } else if (port == 3) {
                    *//*String query= "UPDATE power_availability SET port3_power = '0' WHERE customer_id = '"+_customerId+"'";
                    Statement stmt = con.createStatement();
                    stmt.executeUpdate(query);

                    z = "Registeration successfull";

                    $port3_power = "1";

                    port3Switch.setBackground(getResources().getDrawable(R.drawable.off));
                    port3Switch.setVisibility(View.VISIBLE);*//*

                    String requestBody = "#3#off#";
                    String sql = "insert into iot_requests " + " (device_id, request_body, status, time)"
                            + " values ('" + _customerId + "','" + requestBody + "','0','" + date + "')";
                    Statement stmt2 = con.createStatement();
                    stmt2.executeUpdate(sql);

                } else if (port == 4) {
                    *//*String query= "UPDATE power_availability SET port4_power = '1' WHERE customer_id = '"+_customerId+"'";
                    Statement stmt = con.createStatement();
                    stmt.executeUpdate(query);

                    z = "Registeration successfull";

                    $port4_power = "1";

                    port4Switch.setBackground(getResources().getDrawable(R.drawable.off));
                    port4Switch.setVisibility(View.VISIBLE);*//*

                    String requestBody = "#4#off#";
                    String sql = "insert into iot_requests " + " (device_id, request_body, status, time)"
                            + " values ('" + _customerId + "','" + requestBody + "','0','" + date + "')";
                    Statement stmt2 = con.createStatement();
                    stmt2.executeUpdate(sql);

                } else if (port == 5) {
                    *//*String query= "UPDATE power_availability SET port5_power = '0' WHERE customer_id = '"+_customerId+"'";
                    Statement stmt = con.createStatement();
                    stmt.executeUpdate(query);

                    z = "Registeration successfull";

                    $port5_power = "1";

                    port5Switch.setBackground(getResources().getDrawable(R.drawable.off));
                    port5Switch.setVisibility(View.VISIBLE);*//*

                    String requestBody = "#5#off#";
                    String sql = "insert into iot_requests " + " (device_id, request_body, status, time)"
                            + " values ('" + _customerId + "','" + requestBody + "','0','" + date + "')";
                    Statement stmt2 = con.createStatement();
                    stmt2.executeUpdate(sql);

                } else if (port == 6) {

                   *//* String query= "UPDATE power_availability SET port6_power = '0' WHERE customer_id = '"+_customerId+"'";
                    Statement stmt = con.createStatement();
                    stmt.executeUpdate(query);

                    z = "Registeration successfull";

                    $port6_power = "1";

                    port6Switch.setBackground(getResources().getDrawable(R.drawable.off));
                    port6Switch.setVisibility(View.VISIBLE);*//*

                    String requestBody = "#6#off#";
                    String sql = "insert into iot_requests " + " (device_id, request_body, status, time)"
                            + " values ('" + _customerId + "','" + requestBody + "','0','" + date + "')";
                    Statement stmt2 = con.createStatement();
                    stmt2.executeUpdate(sql);

                }


            }
        } catch (Exception ex) {
            Log.e("check", "got here 3");
            z = "Exceptions" + ex;
        }
*/

    }


    Runnable runnable = new Runnable() {
        public void run() {
            //some code here

            Date currentDate = (Date) java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("Africa/Lagos")).getTime();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            final String date = dateFormat.format(currentDate);


            try {
                final Connection con = connectionClass.CONN();

                if (con == null) {
                    Log.e("check", "got here 1");
                } else {

                    String requestBody = "#" + _port + "#on#";

                    String sql = "insert into iot_requests " + " (device_id, request_body, status, time)"
                            + " values ('" + _customerId + "','" + requestBody + "','0','" + date + "')";
                    Statement stmt2 = con.createStatement();
                    stmt2.executeUpdate(sql);

                }

            } catch (Exception ex) {
                Log.e("check", "got here 3");

            }


        }
    };

    public void fetchStatus() {

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                $port1_name = String.valueOf(dataSnapshot.child(userID).child("devices").child(_customerId).child("port1").child("name").getValue());
                $port2_name = String.valueOf(dataSnapshot.child(userID).child("devices").child(_customerId).child("port2").child("name").getValue());
                $port3_name = String.valueOf(dataSnapshot.child(userID).child("devices").child(_customerId).child("port3").child("name").getValue());
                $port4_name = String.valueOf(dataSnapshot.child(userID).child("devices").child(_customerId).child("port4").child("name").getValue());
                $last_report_timestamp = String.valueOf(dataSnapshot.child(userID).child("devices").child(_customerId).child("port1").child("last_report_timestamp").getValue());

                if(_portNo == 6){
                    $port5_name = String.valueOf(dataSnapshot.child(userID).child("devices").child(_customerId).child("port5").child("name").getValue());
                    $port6_name = String.valueOf(dataSnapshot.child(userID).child("devices").child(_customerId).child("port6").child("name").getValue());
                }


                masterPort1Name.setText($port1_name);
                port2Name.setText($port2_name);
                port3Name.setText($port3_name);
                port4Name.setText($port4_name);
                port5Name.setText($port5_name);
                port6Name.setText($port6_name);


                $power_availability = String.valueOf(dataSnapshot.child(userID).child("devices").child(_customerId).child("power_source").getValue());
                if (Integer.parseInt($power_availability) == 0) {
                    power.setImageDrawable(getResources().getDrawable(R.drawable.not_available));
                    powerText.setText("Power Unavailable");
                } else {
                    power.setImageDrawable(getResources().getDrawable(R.drawable.available));
                    powerText.setText("Power available");
                }

                $port1_online = String.valueOf(dataSnapshot.child(userID).child("devices").child(_customerId).child("port1").child("online_status").getValue());
                $port1_power = String.valueOf(dataSnapshot.child(userID).child("devices").child(_customerId).child("port1").child("power_status").getValue());

                $port1_powerConsumption = String.valueOf(dataSnapshot.child(userID).child("devices").child(_customerId).child("daily_power_logs").child($last_report_timestamp).child("port1").getValue());
                $port2_powerConsumption = String.valueOf(dataSnapshot.child(userID).child("devices").child(_customerId).child("daily_power_logs").child($last_report_timestamp).child("port2").getValue());
                $port3_powerConsumption = String.valueOf(dataSnapshot.child(userID).child("devices").child(_customerId).child("daily_power_logs").child($last_report_timestamp).child("port3").getValue());
                $port4_powerConsumption = String.valueOf(dataSnapshot.child(userID).child("devices").child(_customerId).child("daily_power_logs").child($last_report_timestamp).child("port4").getValue());


                /* $port1_powerConsumption = String.valueOf(dataSnapshot.child(userID).child("devices").child(_customerId).child("port1").child("port_power_consumed").getValue());
                $port2_powerConsumption = String.valueOf(dataSnapshot.child(userID).child("devices").child(_customerId).child("port2").child("port_power_consumed").getValue());
                $port3_powerConsumption = String.valueOf(dataSnapshot.child(userID).child("devices").child(_customerId).child("port3").child("port_power_consumed").getValue());
                $port4_powerConsumption = String.valueOf(dataSnapshot.child(userID).child("devices").child(_customerId).child("port4").child("port_power_consumed").getValue());*/

                if(_portNo == 6){
                    /*$port5_powerConsumption = String.valueOf(dataSnapshot.child(userID).child("devices").child(_customerId).child("port5").child("port_power_consumed").getValue());
                    $port6_powerConsumption = String.valueOf(dataSnapshot.child(userID).child("devices").child(_customerId).child("port6").child("port_power_consumed").getValue());
                   */

                    $port5_powerConsumption = String.valueOf(dataSnapshot.child(userID).child("devices").child(_customerId).child("daily_power_logs").child($last_report_timestamp).child("port5").getValue());
                    $port6_powerConsumption = String.valueOf(dataSnapshot.child(userID).child("devices").child(_customerId).child("daily_power_logs").child($last_report_timestamp).child("port6").getValue());
                }


                if (Integer.parseInt($port1_online) == 0) {
                    onlineStats1.setImageDrawable(getResources().getDrawable(R.drawable.circle_unavailable));
                    onlineStats2.setImageDrawable(getResources().getDrawable(R.drawable.circle_unavailable));
                    onlineStats3.setImageDrawable(getResources().getDrawable(R.drawable.circle_unavailable));
                    onlineStats4.setImageDrawable(getResources().getDrawable(R.drawable.circle_unavailable));
                    onlineStats5.setImageDrawable(getResources().getDrawable(R.drawable.circle_unavailable));
                    onlineStats6.setImageDrawable(getResources().getDrawable(R.drawable.circle_unavailable));
                    port1Switch.setBackground(getResources().getDrawable(R.drawable.off));
                    port1Switch.setVisibility(View.VISIBLE);
                    masterPort1Consumption.setText("offline");
                    port2Consumption.setText("offline");
                    port3Consumption.setText("offline");
                    port4Consumption.setText("offline");

                    if(_portNo == 6){
                        port5Consumption.setText("offline");
                        port6Consumption.setText("offline");
                    }



                } else {
                    onlineStats1.setImageDrawable(getResources().getDrawable(R.drawable.circle_available));
                    onlineStats2.setImageDrawable(getResources().getDrawable(R.drawable.circle_available));
                    onlineStats3.setImageDrawable(getResources().getDrawable(R.drawable.circle_available));
                    onlineStats4.setImageDrawable(getResources().getDrawable(R.drawable.circle_available));
                    onlineStats5.setImageDrawable(getResources().getDrawable(R.drawable.circle_available));
                    onlineStats6.setImageDrawable(getResources().getDrawable(R.drawable.circle_available));
                    masterPort1Consumption.setVisibility(View.VISIBLE);
                    masterPort1Consumption.setText($port1_powerConsumption + "KW");
                    port2Consumption.setText($port2_powerConsumption + "KW");
                    port3Consumption.setText($port3_powerConsumption + "KW");
                    port4Consumption.setText($port4_powerConsumption + "KW");

                    if(_portNo == 6){
                        port5Consumption.setText($port5_powerConsumption + "KW");
                        port6Consumption.setText($port6_powerConsumption + "KW");
                    }


                }

                if (Integer.parseInt($port1_power) == 0) {
                    port1Switch.setBackground(getResources().getDrawable(R.drawable.off));
                    port1Switch.setVisibility(View.VISIBLE);

                } else {

                    port1Switch.setBackground(getResources().getDrawable(R.drawable.on));
                    port1Switch.setVisibility(View.VISIBLE);

                }

                $port2_online = String.valueOf(dataSnapshot.child(userID).child("devices").child(_customerId).child("port2").child("online_status").getValue());
                $port2_power = String.valueOf(dataSnapshot.child(userID).child("devices").child(_customerId).child("port2").child("power_status").getValue());


                if (Integer.parseInt($port2_power) == 0) {
                    port2Switch.setBackground(getResources().getDrawable(R.drawable.off));
                    port2Switch.setVisibility(View.VISIBLE);
                } else {
                    port2Switch.setBackground(getResources().getDrawable(R.drawable.on));
                    port2Switch.setVisibility(View.VISIBLE);
                }

                if(Integer.parseInt($port2_online) == 0){
                    onlineStats2.setImageDrawable(getResources().getDrawable(R.drawable.circle_unavailable));
                    port2Consumption.setText("offline");

                }else{
                    onlineStats2.setImageDrawable(getResources().getDrawable(R.drawable.circle_available));
                    port2Consumption.setVisibility(View.VISIBLE);
                    port2Consumption.setText($port2_powerConsumption  + "KW");


                }


                $port3_online = String.valueOf(dataSnapshot.child(userID).child("devices").child(_customerId).child("port3").child("online_status").getValue());
                $port3_power = String.valueOf(dataSnapshot.child(userID).child("devices").child(_customerId).child("port3").child("power_status").getValue());


                if (Integer.parseInt($port3_power) == 0) {
                    port3Switch.setBackground(getResources().getDrawable(R.drawable.off));
                    port3Switch.setVisibility(View.VISIBLE);
                } else {
                    port3Switch.setBackground(getResources().getDrawable(R.drawable.on));
                    port3Switch.setVisibility(View.VISIBLE);
                }

                if(Integer.parseInt($port3_online) == 0){
                    onlineStats3.setImageDrawable(getResources().getDrawable(R.drawable.circle_unavailable));
                    port3Switch.setBackground(getResources().getDrawable(R.drawable.off));
                    port3Switch.setVisibility(View.VISIBLE);
                    port3Consumption.setText("offline");
                }else{
                    onlineStats3.setImageDrawable(getResources().getDrawable(R.drawable.circle_available));
                    port3Consumption.setVisibility(View.VISIBLE);
                    port3Consumption.setText($port3_powerConsumption  + "KW");


                }



                $port4_online = String.valueOf(dataSnapshot.child(userID).child("devices").child(_customerId).child("port4").child("online_status").getValue());
                $port4_power = String.valueOf(dataSnapshot.child(userID).child("devices").child(_customerId).child("port4").child("power_status").getValue());


                if (Integer.parseInt($port4_power) == 0) {
                    port4Switch.setBackground(getResources().getDrawable(R.drawable.off));
                    port4Switch.setVisibility(View.VISIBLE);
                } else {
                    port4Switch.setBackground(getResources().getDrawable(R.drawable.on));
                    port4Switch.setVisibility(View.VISIBLE);
                }

                if(Integer.parseInt($port4_online) == 0){
                    onlineStats4.setImageDrawable(getResources().getDrawable(R.drawable.circle_unavailable));
                    port4Switch.setBackground(getResources().getDrawable(R.drawable.off));
                    port4Switch.setVisibility(View.VISIBLE);
                    port4Consumption.setText("offline");
                }else{
                    onlineStats4.setImageDrawable(getResources().getDrawable(R.drawable.circle_available));
                    port4Consumption.setVisibility(View.VISIBLE);
                    port4Consumption.setText($port4_powerConsumption  + "KW");


                }

                $port5_online = String.valueOf(dataSnapshot.child(userID).child("devices").child(_customerId).child("port5").child("online_status").getValue());
                $port5_power = String.valueOf(dataSnapshot.child(userID).child("devices").child(_customerId).child("port5").child("power_status").getValue());

                $port6_online = String.valueOf(dataSnapshot.child(userID).child("devices").child(_customerId).child("port6").child("online_status").getValue());
                $port6_power = String.valueOf(dataSnapshot.child(userID).child("devices").child(_customerId).child("port6").child("power_status").getValue());


                if (_portNo == 4) {

                    masterPort.setVisibility(View.VISIBLE);
                    port2.setVisibility(View.VISIBLE);
                    port3.setVisibility(View.VISIBLE);
                    port4.setVisibility(View.VISIBLE);
                    port5.setVisibility(View.INVISIBLE);
                    port6.setVisibility(View.INVISIBLE);

                }else if (_portNo == 6) {

                    masterPort.setVisibility(View.VISIBLE);
                    port2.setVisibility(View.VISIBLE);
                    port3.setVisibility(View.VISIBLE);
                    port4.setVisibility(View.VISIBLE);
                    port5.setVisibility(View.VISIBLE);
                    port6.setVisibility(View.VISIBLE);



                    if (Integer.parseInt($port5_power) == 0) {
                        port5Switch.setBackground(getResources().getDrawable(R.drawable.off));
                        port5Switch.setVisibility(View.VISIBLE);
                    } else {
                        port5Switch.setBackground(getResources().getDrawable(R.drawable.on));
                        port5Switch.setVisibility(View.VISIBLE);
                    }


                    if(Integer.parseInt($port5_online) == 0){
                        onlineStats5.setImageDrawable(getResources().getDrawable(R.drawable.circle_unavailable));
                        port5Switch.setBackground(getResources().getDrawable(R.drawable.off));
                        port5Switch.setVisibility(View.VISIBLE);
                        port5Consumption.setText("offline");
                    }else{
                        onlineStats5.setImageDrawable(getResources().getDrawable(R.drawable.circle_available));
                        port5Consumption.setVisibility(View.VISIBLE);
                        port5Consumption.setText($port5_powerConsumption  + "KW");


                    }



                    if (Integer.parseInt($port6_power) == 0) {
                        port6Switch.setBackground(getResources().getDrawable(R.drawable.off));
                        port6Switch.setVisibility(View.VISIBLE);

                    } else {
                        port6Switch.setBackground(getResources().getDrawable(R.drawable.on));
                        port6Switch.setVisibility(View.VISIBLE);
                    }


                     if(Integer.parseInt($port6_online) == 0){
                        onlineStats6.setImageDrawable(getResources().getDrawable(R.drawable.circle_unavailable));
                        port6Switch.setBackground(getResources().getDrawable(R.drawable.off));
                        port6Switch.setVisibility(View.VISIBLE);
                        port6Consumption.setText("offline");
                    }else{
                        onlineStats6.setImageDrawable(getResources().getDrawable(R.drawable.circle_available));
                        port6Consumption.setVisibility(View.VISIBLE);
                        port6Consumption.setText($port6_powerConsumption  + "KW");


                    }
                }

                progressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    /*private class LoadData extends AsyncTask<String, String, String> {

        String z = "";
        boolean isSuccess = false;


        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            //get customer_id
            try {
                Connection con = connectionClass.CONN();
                Log.e("check", "got here0");
                if (con == null) {
                    z = "Please check your internet connection";
                } else {

                    Log.e("check", "got here2");


                    String query = "select * from power_availability where customer_id='" + _customerId + "'";


                    Statement stmt = con.createStatement();
                    // stmt.executeUpdate(query);

                    ResultSet rs = stmt.executeQuery(query);

                    while (rs.next()) {
                        $power_availability = rs.getString(3);
                        $port_size = rs.getString(4);
                        //total power consumption == 5
                        $port1_power = rs.getString(6);
                        $port2_power = rs.getString(7);
                        $port3_power = rs.getString(8);
                        $port4_power = rs.getString(9);
                        $port5_power = rs.getString(10);
                        $port6_power = rs.getString(11);
                        $port1_powerConsumption = rs.getString(12);
                        $port2_powerConsumption = rs.getString(13);
                        $port3_powerConsumption = rs.getString(14);
                        $port4_powerConsumption = rs.getString(15);
                        $port5_powerConsumption = rs.getString(16);
                        $port6_powerConsumption = rs.getString(17);
                        $port1_online = rs.getString(18);
                        $port2_online = rs.getString(19);
                        $port3_online = rs.getString(20);
                        $port4_online = rs.getString(21);
                        $port5_online = rs.getString(22);
                        $port6_online = rs.getString(23);
                        //update_time == 24

                        Log.e("check", "got here2-" + $power_availability + $port6_online);

                        isSuccess = true;

                    }


                    String query2 = "select * from port_names where customer_id='" + _customerId + "'";


                    Statement stmt2 = con.createStatement();
                    // stmt.executeUpdate(query);

                    ResultSet rs2 = stmt2.executeQuery(query2);

                    while (rs2.next()) {
                        $port1_name = rs2.getString(2);
                        $port2_name = rs2.getString(3);
                        $port3_name = rs2.getString(4);
                        $port4_name = rs2.getString(5);
                        $port5_name = rs2.getString(6);
                        $port6_name = rs2.getString(7);

                        Log.e("check", "got here3-" + $port1_name + $port6_name);

                        isSuccess = true;

                    }


                }
            } catch (Exception ex) {
                isSuccess = false;
                z = "Exceptions" + ex;
                Log.e("check2", z);
            }


            return z;
        }

        @Override
        protected void onPostExecute(String s) {

            if (isSuccess) {

                Log.e("check", "successful");


                $power_availability = rs.getString(3);
                $port_size = rs.getString(4);
                //total power consumption == 5
                $port1_power = rs.getString(6);
                $port2_power = rs.getString(7);
                $port3_power = rs.getString(8);
                $port4_power = rs.getString(9);
                $port5_power = rs.getString(10);
                $port6_power = rs.getString(11);
                $port1_powerConsumption = rs.getString(12);
                $port2_powerConsumption = rs.getString(13);
                $port3_powerConsumption = rs.getString(14);
                $port4_powerConsumption = rs.getString(15);
                $port5_powerConsumption = rs.getString(16);
                $port6_powerConsumption = rs.getString(17);
                $port1_online = rs.getString(18);
                $port2_online = rs.getString(19);
                $port3_online = rs.getString(20);
                $port4_online = rs.getString(21);
                $port5_online = rs.getString(22);
                $port6_online = rs.getString(23);


                $port1_name = rs2.getString(2);
                $port2_name = rs2.getString(3);
                $port3_name = rs2.getString(4);
                $port4_name = rs2.getString(5);
                $port5_name = rs2.getString(6);
                $port6_name = rs2.getString(7);


                masterPort1Name.setText($port1_name);
                port2Name.setText($port2_name);
                port3Name.setText($port3_name);
                port4Name.setText($port4_name);
                port5Name.setText($port5_name);
                port6Name.setText($port6_name);


                if (Integer.parseInt($power_availability) == 0) {
                    power.setImageDrawable(getResources().getDrawable(R.drawable.not_available));
                    powerText.setText("Power Unavailable");
                } else {
                    power.setImageDrawable(getResources().getDrawable(R.drawable.available));
                    powerText.setText("Power available");
                }


                if (Integer.parseInt($port1_online) == 0) {
                    onlineStats1.setImageDrawable(getResources().getDrawable(R.drawable.circle_unavailable));
                    onlineStats2.setImageDrawable(getResources().getDrawable(R.drawable.circle_unavailable));
                    onlineStats3.setImageDrawable(getResources().getDrawable(R.drawable.circle_unavailable));
                    onlineStats4.setImageDrawable(getResources().getDrawable(R.drawable.circle_unavailable));
                    onlineStats5.setImageDrawable(getResources().getDrawable(R.drawable.circle_unavailable));
                    onlineStats6.setImageDrawable(getResources().getDrawable(R.drawable.circle_unavailable));
                    port1Switch.setBackground(getResources().getDrawable(R.drawable.off));
                    port1Switch.setVisibility(View.VISIBLE);
                    masterPort1Consumption.setText("offline");
                    port2Consumption.setText("offline");
                    port3Consumption.setText("offline");
                    port4Consumption.setText("offline");
                    port5Consumption.setText("offline");
                    port6Consumption.setText("offline");

                } else {
                    onlineStats1.setImageDrawable(getResources().getDrawable(R.drawable.circle_available));
                    onlineStats2.setImageDrawable(getResources().getDrawable(R.drawable.circle_available));
                    onlineStats3.setImageDrawable(getResources().getDrawable(R.drawable.circle_available));
                    onlineStats4.setImageDrawable(getResources().getDrawable(R.drawable.circle_available));
                    onlineStats5.setImageDrawable(getResources().getDrawable(R.drawable.circle_available));
                    onlineStats6.setImageDrawable(getResources().getDrawable(R.drawable.circle_available));
                    masterPort1Consumption.setVisibility(View.VISIBLE);
                    masterPort1Consumption.setText($port1_powerConsumption + "KW");
                    port2Consumption.setText($port2_powerConsumption + "KW");
                    port3Consumption.setText($port3_powerConsumption + "KW");
                    port4Consumption.setText($port4_powerConsumption + "KW");
                    port5Consumption.setText($port5_powerConsumption + "KW");
                    port6Consumption.setText($port6_powerConsumption + "KW");

                }

                if (Integer.parseInt($port1_power) == 0) {
                    port1Switch.setBackground(getResources().getDrawable(R.drawable.off));
                    port1Switch.setVisibility(View.VISIBLE);

                } else {

                    port1Switch.setBackground(getResources().getDrawable(R.drawable.on));
                    port1Switch.setVisibility(View.VISIBLE);

                }

                *//*if(Integer.parseInt($port2_online) == 0){
                    onlineStats2.setImageDrawable(getResources().getDrawable(R.drawable.circle_unavailable));
                    port2Consumption.setText("offline");

                }else{
                    onlineStats2.setImageDrawable(getResources().getDrawable(R.drawable.circle_available));
                    port2Consumption.setVisibility(View.VISIBLE);
                    port2Consumption.setText($port2_powerConsumption  + "KW");


                }*//*

                if (Integer.parseInt($port2_power) == 0) {
                    port2Switch.setBackground(getResources().getDrawable(R.drawable.off));
                    port2Switch.setVisibility(View.VISIBLE);
                } else {
                    port2Switch.setBackground(getResources().getDrawable(R.drawable.on));
                    port2Switch.setVisibility(View.VISIBLE);
                }




*//*
                if(Integer.parseInt($port3_online) == 0){
                    onlineStats3.setImageDrawable(getResources().getDrawable(R.drawable.circle_unavailable));
                    port3Switch.setBackground(getResources().getDrawable(R.drawable.off));
                    port3Switch.setVisibility(View.VISIBLE);
                    port3Consumption.setText("offline");
                }else{
                    onlineStats3.setImageDrawable(getResources().getDrawable(R.drawable.circle_available));
                    port3Consumption.setVisibility(View.VISIBLE);
                    port3Consumption.setText($port3_powerConsumption  + "KW");


                }*//*

                if (Integer.parseInt($port3_power) == 0) {
                    port3Switch.setBackground(getResources().getDrawable(R.drawable.off));
                    port3Switch.setVisibility(View.VISIBLE);
                } else {
                    port3Switch.setBackground(getResources().getDrawable(R.drawable.on));
                    port3Switch.setVisibility(View.VISIBLE);
                }


                *//*if(Integer.parseInt($port4_online) == 0){
                    onlineStats4.setImageDrawable(getResources().getDrawable(R.drawable.circle_unavailable));
                    port4Switch.setBackground(getResources().getDrawable(R.drawable.off));
                    port4Switch.setVisibility(View.VISIBLE);
                    port4Consumption.setText("offline");
                }else{
                    onlineStats4.setImageDrawable(getResources().getDrawable(R.drawable.circle_available));
                    port4Consumption.setVisibility(View.VISIBLE);
                    port4Consumption.setText($port4_powerConsumption  + "KW");


                }*//*

                if (Integer.parseInt($port4_power) == 0) {
                    port4Switch.setBackground(getResources().getDrawable(R.drawable.off));
                    port4Switch.setVisibility(View.VISIBLE);
                } else {
                    port4Switch.setBackground(getResources().getDrawable(R.drawable.on));
                    port4Switch.setVisibility(View.VISIBLE);
                }


                if (Integer.parseInt($port_size) == 4) {

                    masterPort.setVisibility(View.VISIBLE);
                    port2.setVisibility(View.VISIBLE);
                    port3.setVisibility(View.VISIBLE);
                    port4.setVisibility(View.VISIBLE);
                    port5.setVisibility(View.INVISIBLE);
                    port6.setVisibility(View.INVISIBLE);

                } else if (Integer.parseInt($port_size) == 6) {

                    masterPort.setVisibility(View.VISIBLE);
                    port2.setVisibility(View.VISIBLE);
                    port3.setVisibility(View.VISIBLE);
                    port4.setVisibility(View.VISIBLE);
                    port5.setVisibility(View.VISIBLE);
                    port6.setVisibility(View.VISIBLE);


                    *//*if(Integer.parseInt($port5_online) == 0){
                        onlineStats5.setImageDrawable(getResources().getDrawable(R.drawable.circle_unavailable));
                        port5Switch.setBackground(getResources().getDrawable(R.drawable.off));
                        port5Switch.setVisibility(View.VISIBLE);
                        port5Consumption.setText("offline");
                    }else{
                        onlineStats5.setImageDrawable(getResources().getDrawable(R.drawable.circle_available));
                        port5Consumption.setVisibility(View.VISIBLE);
                        port5Consumption.setText($port5_powerConsumption  + "KW");


                    }*//*

                    if (Integer.parseInt($port5_power) == 0) {
                        port5Switch.setBackground(getResources().getDrawable(R.drawable.off));
                        port5Switch.setVisibility(View.VISIBLE);
                    } else {
                        port5Switch.setBackground(getResources().getDrawable(R.drawable.on));
                        port5Switch.setVisibility(View.VISIBLE);
                    }


                    *//*if(Integer.parseInt($port6_online) == 0){
                        onlineStats6.setImageDrawable(getResources().getDrawable(R.drawable.circle_unavailable));
                        port6Switch.setBackground(getResources().getDrawable(R.drawable.off));
                        port6Switch.setVisibility(View.VISIBLE);
                        port6Consumption.setText("offline");
                    }else{
                        onlineStats6.setImageDrawable(getResources().getDrawable(R.drawable.circle_available));
                        port6Consumption.setVisibility(View.VISIBLE);
                        port6Consumption.setText($port6_powerConsumption  + "KW");


                    }*//*

                    if (Integer.parseInt($port6_power) == 0) {
                        port6Switch.setBackground(getResources().getDrawable(R.drawable.off));
                        port6Switch.setVisibility(View.VISIBLE);

                    } else {
                        port6Switch.setBackground(getResources().getDrawable(R.drawable.on));
                        port6Switch.setVisibility(View.VISIBLE);
                    }

                }


            } else {
                Toast.makeText(getActivity(), "Unsuccessful", Toast.LENGTH_SHORT).show();
                Log.e("check", s);
            }

            progressBar.setVisibility(View.INVISIBLE);

        }
    }*/


}





