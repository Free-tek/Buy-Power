package com.botosoft.buypower.activities;

import android.app.ProgressDialog;
import android.content.Intent;
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

import com.botosoft.buypower.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Adewole Babatunde on 3/04/2020.
 */

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText email;
    Button setRecoveryMail;
    TextView signUp;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        initUi();
    }

    public void initUi(){
        signUp = (TextView) findViewById(R.id.signUp);
        email = (EditText) findViewById(R.id.email);
        setRecoveryMail = (Button) findViewById(R.id.set_recovery_mail);

        setRecoveryMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String $email = String.valueOf(email.getText());
                if(TextUtils.isEmpty($email)){
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter your email!", Toast.LENGTH_SHORT);
                }else{
                    progressDialog = new ProgressDialog(ForgotPasswordActivity.this);
                    progressDialog.setMessage("Creating Account");
                    progressDialog.setCanceledOnTouchOutside(true);
                    progressDialog.show();

                    FirebaseAuth.getInstance().sendPasswordResetEmail($email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("Email Status", "Email sent.");
                                        progressDialog.dismiss();
                                        Toast.makeText(ForgotPasswordActivity.this, "Email sent successfully!", Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(ForgotPasswordActivity.this, "Invalid email!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
