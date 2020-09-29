package com.botosoft.buypower.activities.ui.support;

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
import com.botosoft.buypower.activities.PowerConsumedActivity;
import com.botosoft.buypower.activities.RenamePortsActivity;

public class SupportFragment extends Fragment {

    private SupportViewModel notificationsViewModel;
    Button rename, support, subscriptions, powerConsumption;

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
