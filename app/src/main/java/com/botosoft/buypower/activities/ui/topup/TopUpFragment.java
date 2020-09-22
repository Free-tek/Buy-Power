package com.botosoft.buypower.activities.ui.topup;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.botosoft.buypower.R;
import com.botosoft.buypower.activities.ui.controls.ControlsFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;

public class TopUpFragment extends Fragment {

    private TopUpViewModel dashboardViewModel;
    EditText cardNumber, cardMonth, cardYear, cardCVV, cardPin;
    TextView cardNumber_, cardMonth_, cardYear_, cardCVV_, cardPin_;
    Button topUp;
    ImageView success, loading;
    TextView successText, amount;
    ProgressDialog progressDialog;


    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    static FirebaseUser User = mAuth.getCurrentUser();
    static final String userID = User.getUid();

    FirebaseDatabase database;
    DatabaseReference users, port;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(TopUpViewModel.class);
        View root = inflater.inflate(R.layout.fragment_topup, container, false);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        users = database.getReference().child("users");
        port = database.getReference().child("product_serial_no");

        PaystackSdk.initialize(getActivity());

        initUi(root);

        return root;
    }

    public void initUi(View view){
        cardNumber = (EditText) view.findViewById(R.id.card_number);
        cardMonth = (EditText) view.findViewById(R.id.card_month);
        cardYear = (EditText) view.findViewById(R.id.card_year);
        cardCVV = (EditText) view.findViewById(R.id.card_cvv);

        
        cardNumber_ = (TextView) view.findViewById(R.id.card_number_);
        cardMonth_ = (TextView) view.findViewById(R.id.card_month_);
        cardYear_ = (TextView) view.findViewById(R.id.card_year_);
        cardCVV_ = (TextView) view.findViewById(R.id.card_cvv_);


        topUp = (Button) view.findViewById(R.id.top_up);

        loading = (ImageView) view.findViewById(R.id.loading);
        success = (ImageView) view.findViewById(R.id.success);
        successText = (TextView) view.findViewById(R.id.successText);
        amount = (TextView) view.findViewById(R.id.amount);


        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String _customerId = String.valueOf(dataSnapshot.child("customerId").getValue());

                port.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String _port = String.valueOf(dataSnapshot.child(_customerId).getValue());
                        if(_port.equals("4")){
                            amount.setText("N2000");
                        }else{
                            amount.setText("N4000");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        topUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String _cardNumber = String.valueOf(cardNumber.getText());
                int expiryMonth = Integer.parseInt(String.valueOf(cardMonth.getText())); //any month in the future
                int expiryYear = Integer.parseInt(String.valueOf(cardYear.getText())); // any year in the future. '2018' would work also!
                String cvv = String.valueOf(cardCVV.getText());  // cvv of the test card

                //TODO: VALIDATE

                if(isValid()){
                    final Card card = new Card(_cardNumber, expiryMonth, expiryYear, cvv);
                    if (card.isValid()) {
                        // charge card

                        users.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final String _customerId = String.valueOf(dataSnapshot.child("customerId").getValue());

                                port.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        final String _port = String.valueOf(dataSnapshot.child(_customerId).getValue());

                                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                                        alertDialog.setTitle("Proceed");
                                        alertDialog.setMessage("Do you want to proceed to pay?");
                                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        progressDialog = new ProgressDialog(getActivity());
                                                        progressDialog.setMessage("Making Transfer...");
                                                        progressDialog.setCanceledOnTouchOutside(true);
                                                        progressDialog.show();

                                                        performCharge(card, _port);

                                                    }

                                                });

                                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }

                                                });


                                        alertDialog.show();



                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }



                        });



                    } else {
                        //do something
                        Toast.makeText(getActivity(), "Invalid card details", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });




    }


    public void performCharge(Card card, String _port){
        //create a Charge object
        Charge charge = new Charge();
        if(_port.equals("4")){
            charge.setAmount(2000);
        }else{
            charge.setAmount(4000);
        }
        charge.setCard(card);
        charge.setEmail("adewole63@gmail.com");

        PaystackSdk.chargeCard(getActivity(), charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction) {
                // This is called only after transaction is deemed successful.
                // Retrieve the transaction, and send its reference to your server
                // for verification.

                progressDialog.dismiss();
                //record subscription date start
                Date currentDate = (Date) java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("Africa/Lagos")).getTime();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                final String date = dateFormat.format(currentDate);


                //Specifying date format that matches the given date
                Calendar c = Calendar.getInstance();
                try{
                    //Setting the date to the given date
                    c.setTime(dateFormat.parse(date));
                }catch(ParseException e){
                    e.printStackTrace();
                }

                //Number of Days to add
                c.add(Calendar.DAY_OF_MONTH, 30);
                //Date after adding the days to the given date
                String newDate = dateFormat.format(c.getTime());

                final String uid = mAuth.getCurrentUser().getUid();

                DatabaseReference userId = database.getReference().child("users").child(uid);

                userId.child("subscription_expiry_date").setValue(newDate);

                Toast.makeText(getActivity(), "Payment Successful", Toast.LENGTH_SHORT).show();
                success.setVisibility(View.VISIBLE);
                successText.setVisibility(View.VISIBLE);



                cardNumber.setVisibility(View.INVISIBLE);
                cardMonth.setVisibility(View.INVISIBLE);
                cardYear.setVisibility(View.INVISIBLE);
                cardCVV.setVisibility(View.INVISIBLE);
                cardNumber_.setVisibility(View.INVISIBLE);
                cardMonth_.setVisibility(View.INVISIBLE);
                cardYear_.setVisibility(View.INVISIBLE);
                cardCVV_.setVisibility(View.INVISIBLE);
                topUp.setVisibility(View.INVISIBLE);
                loading.setVisibility(View.INVISIBLE);

            }

            @Override
            public void beforeValidate(Transaction transaction) {
                // This is called only before requesting OTP.
                // Save reference so you may send to server. If
                // error occurs with OTP, you should still verify on server.

                progressDialog.dismiss();


                success.setVisibility(View.INVISIBLE);
                successText.setVisibility(View.INVISIBLE);

                cardNumber.setVisibility(View.VISIBLE);
                cardMonth.setVisibility(View.VISIBLE);
                cardYear.setVisibility(View.VISIBLE);
                cardCVV.setVisibility(View.VISIBLE);


                cardNumber_.setVisibility(View.VISIBLE);
                cardMonth_.setVisibility(View.VISIBLE);
                cardYear_.setVisibility(View.VISIBLE);
                cardCVV_.setVisibility(View.VISIBLE);

                topUp.setVisibility(View.VISIBLE);

                cardNumber.setAlpha((float) 0.4);
                cardMonth.setAlpha((float) 0.4);
                cardYear.setAlpha((float) 0.4);
                cardCVV.setAlpha((float) 0.4);

                cardNumber_.setAlpha((float) 0.4);
                cardMonth_.setAlpha((float) 0.4);
                cardYear_.setAlpha((float) 0.4);
                cardCVV_.setAlpha((float) 0.4);

                topUp.setAlpha((float) 0.4);


                loading.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                //handle error here

                progressDialog.dismiss();


                success.setVisibility(View.INVISIBLE);
                successText.setVisibility(View.VISIBLE);

                cardNumber.setVisibility(View.VISIBLE);
                cardMonth.setVisibility(View.VISIBLE);
                cardYear.setVisibility(View.VISIBLE);
                cardCVV.setVisibility(View.VISIBLE);
                cardNumber_.setVisibility(View.VISIBLE);
                cardMonth_.setVisibility(View.VISIBLE);
                cardYear_.setVisibility(View.VISIBLE);
                cardCVV_.setVisibility(View.VISIBLE);
                topUp.setVisibility(View.VISIBLE);

                //loading.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "Error occurred while making the transaction", Toast.LENGTH_SHORT).show();
            }

        });
    }


    public boolean isValid(){

        boolean valid = true;

        String $cardNumber = cardNumber.getText().toString();
        if (TextUtils.isEmpty($cardNumber)) {
            cardNumber.setError("Required.");
            valid = false;
        } else {
            cardNumber.setError(null);
        }


        if ($cardNumber.length() != 16) {
            cardNumber.setError("Incorrect.");
            valid = false;
            Toast.makeText(getActivity(), "Card number must be 16 digits", Toast.LENGTH_SHORT).show();
        } else {
            cardNumber.setError(null);
        }


        String $expiryMonth = cardMonth.getText().toString();
        if (TextUtils.isEmpty($expiryMonth)) {
            cardMonth.setError("Required.");
            valid = false;
        } else {
            cardMonth.setError(null);
        }

        if ($expiryMonth.length() != 2) {
            cardMonth.setError("Incorrect.");
            valid = false;
            Toast.makeText(getActivity(), "Card month must be 2 digits", Toast.LENGTH_SHORT).show();
        } else {
            cardMonth.setError(null);
        }

        String $cardYear = cardYear.getText().toString();
        if (TextUtils.isEmpty($cardYear)) {
            cardYear.setError("Required.");
            valid = false;
        } else {
            cardYear.setError(null);
        }

        if ($cardYear.length() != 2) {
            cardYear.setError("Incorrect.");
            valid = false;
            Toast.makeText(getActivity(), "Card year must be 2 digits", Toast.LENGTH_SHORT).show();
        } else {
            cardYear.setError(null);
        }

        String $cvv = cardCVV.getText().toString();
        if (TextUtils.isEmpty($cvv)) {
            cardCVV.setError("Required.");
            valid = false;
        } else {
            cardCVV.setError(null);
        }

        if ($cvv.length() != 3) {
            cardCVV.setError("Incorrect.");
            valid = false;
            Toast.makeText(getActivity(), "Card CVV must be 3 digits", Toast.LENGTH_SHORT).show();
        } else {
            cardCVV.setError(null);
        }

        return valid;
    }
}
