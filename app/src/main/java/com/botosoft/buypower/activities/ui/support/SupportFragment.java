package com.botosoft.buypower.activities.ui.support;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.botosoft.buypower.R;
import com.botosoft.buypower.activities.LoginActivity;
import com.botosoft.buypower.activities.PowerConsumedActivity;
import com.botosoft.buypower.activities.RenamePortsActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SupportFragment extends Fragment {

    private SupportViewModel notificationsViewModel;
    Button rename, support, subscriptions, powerConsumption, signOut;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(SupportViewModel.class);
        View root = inflater.inflate(R.layout.fragment_support, container, false);

        initUi(root);
        return root;
    }


    public void initUi(View view){

        rename = (Button) view.findViewById(R.id.rename);
        support = (Button) view.findViewById(R.id.support);
        subscriptions = (Button) view.findViewById(R.id.subscriptions);
        subscriptions = (Button) view.findViewById(R.id.subscriptions);
        powerConsumption = (Button) view.findViewById(R.id.power_consumed);
        signOut = (Button) view.findViewById(R.id.signOut);

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Proceed?");
                alertDialog.setMessage("Do you want to sign out" );
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent signOut = new Intent (getActivity(), LoginActivity.class);
                                signOut.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                //sign out
                                FirebaseAuth.getInstance().signOut();
                                startActivity(signOut);
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alertDialog.show();

            }
        });
        rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RenamePortsActivity.class);
                startActivity(intent);
            }
        });

        support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        subscriptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        powerConsumption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PowerConsumedActivity.class);
                startActivity(intent);
            }
        });

    }
}
