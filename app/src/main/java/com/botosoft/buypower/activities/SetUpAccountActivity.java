package com.botosoft.buypower.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.botosoft.buypower.R;
import com.botosoft.buypower.Utilities.ConnectionClass;
import com.botosoft.buypower.Utilities.Constants;
import com.botosoft.buypower.Utilities.FcmVolley;
import com.botosoft.buypower.Utilities.SharedPreference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


import java.util.HashMap;
import java.util.Map;



/**
 * Created by Adewole Babatunde on 3/04/2020.
 */

public class SetUpAccountActivity extends AppCompatActivity {

    EditText customerId, email, password, phoneNo, confirmPassword, name;
    Button setUp;
    TextView signUp, login;
    ProgressDialog progressDialog;
    ConnectionClass connectionClass;
    FirebaseDatabase database;
    DatabaseReference product_serial_no, users;

    String portSize;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setupaccount);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        product_serial_no = database.getReference().child("product_serial_no");
        users = database.getReference().child("users");


        connectionClass = new ConnectionClass();

        initUi();
    }

    public void initUi(){
        signUp = (TextView) findViewById(R.id.signUp);
        login = (TextView) findViewById(R.id.login);
        customerId = (EditText) findViewById(R.id.serial_no);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirm_password);
        phoneNo = (EditText) findViewById(R.id.phone_no);
        name = (EditText) findViewById(R.id.name);

        setUp = (Button) findViewById(R.id.set_up);

        setUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!validateForm()){

                    Toast.makeText(SetUpAccountActivity.this, "Check the form for errors", Toast.LENGTH_SHORT).show();

                }else if(!isNetworkAvailable()){

                    Toast.makeText(SetUpAccountActivity.this, "Please check your internet connection and try again", Toast.LENGTH_SHORT).show();

                }else{

                    final Intent intent = new Intent(SetUpAccountActivity.this, LoginActivity.class);

                    progressDialog = new ProgressDialog(SetUpAccountActivity.this);
                    progressDialog.setMessage("Creating Account...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();


                    final String $email = String.valueOf(email.getText());
                    final String $password = String.valueOf(password.getText());
                    final String $serialNo = String.valueOf(customerId.getText());

                    Log.e("info", "Got here" + $serialNo);


                    product_serial_no.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean exist = false;
                            int iter = 1;
                            for (DataSnapshot Snapshot: dataSnapshot.getChildren()) {
                                String custID = String.valueOf(Snapshot.getKey());
                                portSize = String.valueOf(Snapshot.child("ports").getValue());

                                Log.e("id", $serialNo + " : " + custID);
                                if (custID.equals($serialNo)){
                                    exist = true;
                                    Log.e("info", "Got here" );
                                    mAuth.createUserWithEmailAndPassword($email, $password)
                                            .addOnCompleteListener(SetUpAccountActivity.this, new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {

                                                            if (task.isSuccessful()) {
                                                                // Sign in success, update UI with the signed-in user's information

                                                                saveData();
                                                                sendEmailVerification();
                                                                progressDialog.dismiss();
                                                                Toast.makeText(SetUpAccountActivity.this, "Sign up successful, We sent you a verification email, visit your mail now to complete the sign up process", Toast.LENGTH_SHORT).show();

                                                                startActivity(intent);
                                                                finish();



                                                                //Doregister doregister = new Doregister();
                                                                //doregister.execute("");

                                                            }else {
                                                                // If sign in fails, display a message to the user.
                                                                progressDialog.dismiss();
                                                                if (checkEmailExistsOrNot($email)){
                                                                    Toast.makeText(SetUpAccountActivity.this, "Ooops.. email already exists",
                                                                            Toast.LENGTH_SHORT).show();
                                                                    progressDialog.dismiss();
                                                                }else{
                                                                    progressDialog.dismiss();
                                                                    Toast.makeText(SetUpAccountActivity.this, "Sign up failed, please try again",
                                                                            Toast.LENGTH_SHORT).show();
                                                                }



                                                            }


                                                        }

                                                    }
                                            );

                                    break;


                                }
                                iter ++;
                            }


                            if(!exist) {
                                progressDialog.dismiss();
                                Toast.makeText(SetUpAccountActivity.this, "This product serial number does not exist, please confirm this.",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


            }

            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SetUpAccountActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void sendEmailVerification() {

        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(SetUpAccountActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Toast.makeText(SetUpAccountActivity.this, "We sent you a verification email, visit your mail now to complete the sign up process", Toast.LENGTH_SHORT).show();
                        } else {
                            //Toast.makeText(SetUpAccountActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private boolean validateForm() {
        boolean valid = true;

        String $name = name.getText().toString();
        if (TextUtils.isEmpty($name)) {
            name.setError("Required.");
            valid = false;
        } else {
            name.setError(null);
        }


        String $customerId = customerId.getText().toString();
        if (TextUtils.isEmpty($customerId)) {
            customerId.setError("Required.");
            valid = false;
        } else {
            customerId.setError(null);
        }

        String $phone = phoneNo.getText().toString();
        if (TextUtils.isEmpty($phone)) {
            phoneNo.setError("Required.");
            valid = false;
        } else {
            phoneNo.setError(null);
        }

        String $email = email.getText().toString();
        if (TextUtils.isEmpty($email)) {
            email.setError("Required.");
            valid = false;
        } else {
            email.setError(null);
        }


        String $password = password.getText().toString();
        if (TextUtils.isEmpty($password)) {
            password.setError("Required.");
            valid = false;
        } else {
            password.setError(null);
        }

        String $cpassword = confirmPassword.getText().toString();
        if (TextUtils.isEmpty($cpassword)) {
            confirmPassword.setError("Required.");
            valid = false;
        } else {
            confirmPassword.setError(null);
        }

        if(!($password.equals($cpassword))){
            confirmPassword.setError("Does not match password.");
        }

        return valid;
    }

    private void saveData() {
        final String uid = mAuth.getCurrentUser().getUid();

        DatabaseReference users = database.getReference().child("users").child(uid);

        String $customerId = String.valueOf(customerId.getText());
        String $phoneNo = String.valueOf(phoneNo.getText());
        String $email = String.valueOf(email.getText());
        String $password = String.valueOf(password.getText());

        int _portSize =  Integer.parseInt(portSize);

        users.child("customerId").setValue($customerId);
        users.child("phoneNo").setValue($phoneNo);
        users.child("email").setValue($email);
        users.child("password").setValue($password);
        users.child("portNo").setValue(_portSize);

        users.child("devices").child($customerId).child("power_source").setValue(0);



        product_serial_no.child($customerId).child("userId").setValue(uid);

        users.child("devices").child($customerId).child("port1").child("power_status").setValue(0);
        users.child("devices").child($customerId).child("port2").child("power_status").setValue(0);
        users.child("devices").child($customerId).child("port3").child("power_status").setValue(0);
        users.child("devices").child($customerId).child("port4").child("power_status").setValue(0);

        users.child("devices").child($customerId).child("port1").child("name").setValue("port1");
        users.child("devices").child($customerId).child("port2").child("name").setValue("port2");
        users.child("devices").child($customerId).child("port3").child("name").setValue("port3");
        users.child("devices").child($customerId).child("port4").child("name").setValue("port4");


        users.child("devices").child($customerId).child("port1").child("port_power_consumed").setValue(0);
        users.child("devices").child($customerId).child("port2").child("port_power_consumed").setValue(0);
        users.child("devices").child($customerId).child("port3").child("port_power_consumed").setValue(0);
        users.child("devices").child($customerId).child("port4").child("port_power_consumed").setValue(0);

        users.child("devices").child($customerId).child("port1").child("online_status").setValue(0);
        users.child("devices").child($customerId).child("port2").child("online_status").setValue(0);
        users.child("devices").child($customerId).child("port3").child("online_status").setValue(0);
        users.child("devices").child($customerId).child("port4").child("online_status").setValue(0);



        if(_portSize == 6){
            users.child("devices").child($customerId).child("port5").child("power_status").setValue(0);
            users.child("devices").child($customerId).child("port6").child("power_status").setValue(0);

            users.child("devices").child($customerId).child("port5").child("name").setValue("port5");
            users.child("devices").child($customerId).child("port6").child("name").setValue("port6");

            users.child("devices").child($customerId).child("port5").child("port_power_consumed").setValue(0);
            users.child("devices").child($customerId).child("port6").child("port_power_consumed").setValue(0);

            users.child("devices").child($customerId).child("port5").child("online_status").setValue(0);
            users.child("devices").child($customerId).child("port6").child("online_status").setValue(0);
        }

    }


    public boolean checkEmailExistsOrNot(String email){

        final boolean[] result = {true};
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                if (task.getResult().getSignInMethods().size() == 0){
                    result[0] =  false;
                    //email doesnt exist
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });

        return result[0];

    }

    public class Doregister extends AsyncTask<String,String,String>
    {


        String _customerId = customerId.getText().toString();
        String _password= password.getText().toString();
        String _email= email.getText().toString();
        String _phoneNo= phoneNo.getText().toString();
        String _name= name.getText().toString();
        String z="";
        boolean isSuccess=false;

        @Override
        protected void onPreExecute() {
            /*progressDialog.setMessage("Loading...");
            progressDialog.show();*/
        }

        @Override
        protected String doInBackground(String... params) {


            if(_customerId.trim().equals("")|| _password.trim().equals(""))
                z = "Please enter all fields....";

            else
            {
                Log.e("check", "Launched do in bg1");
                try {
                    Connection con = connectionClass.CONN();

                    Log.e("check2", String.valueOf(con));
                    if (con == null) {
                        Log.e("check", "got here 1" );
                        z = "Please check your internet connection";

                    } else {

                        Log.e("check", "got here 2" );


                        String query="insert into authentication values('"+_customerId+"','"+_password+"','"+_email+"','"+_phoneNo+"','"+_name+"')";
                        Statement stmt = con.createStatement();
                        stmt.executeUpdate(query);


                        String query3="insert into port_names values('"+_customerId+"','Master port','Port 2','Port 3','Port 4','Port 5', 'Port 6')";
                        Statement stmt3 = con.createStatement();
                        stmt3.executeUpdate(query3);

                        //record subscription date start
                        Date currentDate = (Date) java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("Africa/Lagos")).getTime();
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        final String date = dateFormat.format(currentDate);

                        String query2="insert into power_availability " + "  (Customer_id, Power_avail, Port_size, Total_powerConsumption, Port1_power, Port2_power, Port3_power, Port4_power, Port5_power, Port6_power, Port1_powerConsumption, Port2_powerConsumption, Port3_powerConsumption, Port4_powerConsumption, Port5_powerConsumption, Port6_powerConsumption, port1_online, port2_online, port3_online, port4_online, port5_online, port6_online, update_time)"
                                + " values ('"+_customerId+"','0','"+portSize+"','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','"+date+"')";
                        Statement stmt2 = con.createStatement();
                        stmt2.executeUpdate(query2);

                        Log.e("check", "got here 4" );


                        z = "Registeration successfull";
                        isSuccess=true;


                    }
                }
                catch (Exception ex)
                {
                    Log.e("check", "got here 3" );
                    isSuccess = false;
                    z = "Exceptions"+ex;
                }
            }
            return z;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.e("error", ""+z);
            Toast.makeText(getBaseContext(),""+z,Toast.LENGTH_LONG).show();

            if(isSuccess) {
                Log.e("check", "Launched do in bg2");
                sendToken();
                //saveData();

            }else{
                Toast.makeText(SetUpAccountActivity.this, "Registration Unsuccessful", Toast.LENGTH_SHORT).show();
                progressDialog.hide();
            }


        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                Intent intent = new Intent(SetUpAccountActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }




    private class Dologin extends AsyncTask<String,String,String>{


        String _customerId = customerId.getText().toString();
        String _password= password.getText().toString();

        String z="";
        boolean isSuccess=false;

        String $customerId,$password;


        @Override
        protected void onPreExecute() {


            progressDialog = new ProgressDialog(SetUpAccountActivity.this);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setMessage("Loading...");
            progressDialog.show();


            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            if(_customerId.trim().equals("")|| _password.trim().equals(""))
                z = "Please enter all fields....";
            else
            {
                try {

                    Connection con = connectionClass.CONN();
                    if (con == null) {
                        z = "Please check your internet connection";
                    } else {

                        String query=" select * from authentication where customer_id='"+_customerId+"' and password = '"+_password+"'";


                        Statement stmt = con.createStatement();
                        stmt.executeUpdate(query);


                        ResultSet rs=stmt.executeQuery(query);

                        while (rs.next())

                        {
                            $customerId = rs.getString(1);
                            $password=rs.getString(2);

                            if($customerId.equals(customerId)&& $password.equals(password))
                            {

                                isSuccess=true;
                                z = "Login successfull";

                            }

                            else
                                isSuccess=false;
                        }





                    }
                }
                catch (Exception ex)
                {
                    isSuccess = false;
                    z = "Exceptions"+ex;
                }
            }
            return z;        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getBaseContext(),""+z,Toast.LENGTH_LONG).show();

            if(isSuccess) {
                Toast.makeText(SetUpAccountActivity.this, "Successful Check", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(SetUpAccountActivity.this, "Unsuccessful", Toast.LENGTH_SHORT).show();
            }

            progressDialog.hide();

        }
    }


    public void sendToken() {

        final String token = SharedPreference.getInstance(this).getDeviceToken();
        final String _email = String.valueOf(email.getText());

        if (token == null) {
            progressDialog.dismiss();
            Toast.makeText(this, "Token not generated", Toast.LENGTH_LONG).show();
            return;
        }else{

        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_REGISTER_DEVICE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            Toast.makeText(SetUpAccountActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SetUpAccountActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", _email);
                params.put("token", token);

                return params;
            }
        };

        String z;

        try {
            Connection con = connectionClass.CONN();

            Log.e("check2", String.valueOf(con));
            if (con == null) {
                Log.e("check", "got here 1" );
                z = "Please check your internet connection";

            } else {

                Log.e("check", "got here 2" );

                //record subscription date start
                Date currentDate = (Date) java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("Africa/Lagos")).getTime();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                final String date = dateFormat.format(currentDate);


                String query2="insert into devices " + "  (email, token)"
                        + " values ('"+_email+"','"+token+"')";
                Statement stmt2 = con.createStatement();
                stmt2.executeUpdate(query2);

                Log.e("check", "got here 4" );

            }
        }
        catch (Exception ex)
        {
            Log.e("check", "got here 3" );
            z = "Exceptions"+ex;
            Toast.makeText(this, z, Toast.LENGTH_SHORT).show();
            return;
        }



        progressDialog.dismiss();
        FcmVolley.getInstance(this).addToRequestQueue(stringRequest);
        saveData();
    }


}
