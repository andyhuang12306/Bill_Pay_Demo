package com.example.myapplication;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

public class MyPaymentAdapter extends RecyclerView.Adapter<MyPaymentAdapter.MyViewHolder> {
    private ArrayList<Payment> mData;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView no;
        public TextView name;
        public TextView amount;
        public RadioButton button;
        public MyViewHolder(View v) {
            super(v);
            no = v.findViewById(R.id.no);
            name = v.findViewById(R.id.name);
            amount = v.findViewById(R.id.amount);
            button = v.findViewById(R.id.button);
        }
    }

    public MyPaymentAdapter(ArrayList<Payment> myData) {
        mData = myData;
    }

    @Override
    public MyPaymentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bill_item_view, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.no.setText(mData.get(position).getNo());
        holder.name.setText(mData.get(position).getName());
        holder.amount.setText(mData.get(position).getAmount());
        holder.button.setTag(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}