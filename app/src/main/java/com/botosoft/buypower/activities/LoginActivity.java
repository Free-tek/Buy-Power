
package com.botosoft.buypower.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.botosoft.buypower.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;


/**
 * Created by Adewole Babatunde on 3/04/2020.
 */

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button login;
    TextView forgotPassword, setUp;
    ProgressDialog progressDialog;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();
        initUi();
    }

    public void initUi(){
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        forgotPassword = (TextView) findViewById(R.id.forgot_password);
        setUp = (TextView) findViewById(R.id.signUp);
        login = (Button) findViewById(R.id.login);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validateForm()){
                    Toast.makeText(LoginActivity.this, "Make sure to fill all the required fields",
                            Toast.LENGTH_SHORT).show();
                }else if(!isNetworkAvailable()){
                    //progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Please check your internet connection",
                            Toast.LENGTH_SHORT).show();
                }else{

                    final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setMessage("Logging you in...");
                    progressDialog.setCanceledOnTouchOutside(true);
                    progressDialog.show();


                    String $email = String.valueOf(email.getText());
                    String $password = String.valueOf(password.getText());

                    mAuth.signInWithEmailAndPassword($email, $password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information

                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (user.isEmailVerified()){
                                            startActivity(intent);

                                        }else{

                                            AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                                            alertDialog.setTitle("OOps... 😭");
                                            alertDialog.setMessage("You have not verified your email yet, please check your mail for the verification mail we sent you" );
                                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Okay",
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();

                                                        }
                                                    });
                                            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Resend Mail",
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            sendEmailVerification();
                                                            dialog.dismiss();

                                                        }
                                                    });
                                            alertDialog.show();
                                        }




                                    }else if(!isNetworkAvailable()){
                                        progressDialog.dismiss();
                                        Toast.makeText(LoginActivity.this, "Please check your internet connection",
                                                Toast.LENGTH_SHORT).show();
                                    }else {
                                        progressDialog.dismiss();
                                        Toast.makeText(LoginActivity.this, "Wrong login details, please try again",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    progressDialog.dismiss();
                                }
                            });
                }
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        setUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SetUpAccountActivity.class);
                startActivity(intent);
            }
        });
    }

    private void sendEmailVerification() {

        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,
                                    "We sent you a verification email, visit your mail now to complete the sign up process",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private boolean validateForm() {
        boolean valid = true;

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

        return valid;
    }


}
