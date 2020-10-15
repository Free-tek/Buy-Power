package com.botosoft.buypower.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.botosoft.buypower.Interface.ItemClickListener;
import com.botosoft.buypower.R;

public class PowerConsumedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView port1Name, port2Name, port3Name, port4Name, port5Name, port6Name ;
    public TextView port1, port2, port3, port4, port5, port6, time;
    private ItemClickListener itemClickListener;

    public PowerConsumedViewHolder(View itemView) {
        super(itemView);
        this.port1Name = (TextView) itemView.findViewById(R.id.port1_name);
        this.port2Name = (TextView) itemView.findViewById(R.id.port2_name);
        this.port3Name = (TextView) itemView.findViewById(R.id.port3_name);
        this.port4Name = (TextView) itemView.findViewById(R.id.port4_name);
        this.port5Name = (TextView) itemView.findViewById(R.id.port5_name);
        this.port6Name = (TextView) itemView.findViewById(R.id.port6_name);

        this.time = (TextView) itemView.findViewById(R.id.time);

        this.port1 = (TextView) itemView.findViewById(R.id.port1);
        this.port2 = (TextView) itemView.findViewById(R.id.port2);
        this.port3 = (TextView) itemView.findViewById(R.id.port3);
        this.port4 = (TextView) itemView.findViewById(R.id.port4);
        this.port5 = (TextView) itemView.findViewById(R.id.port5);
        this.port6 = (TextView) itemView.findViewById(R.id.port6);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void onClick(View view) {
        this.itemClickListener.onClick(view, getAdapterPosition(), false);
    }



}
