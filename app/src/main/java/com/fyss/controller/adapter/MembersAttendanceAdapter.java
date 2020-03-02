package com.fyss.controller.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.fyss.R;
import com.fyss.model.FyUser;

import java.util.ArrayList;

public class MembersAttendanceAdapter extends RecyclerView.Adapter<MembersAttendanceAdapter.MyViewHolder>{

    private ArrayList<FyUser> membersList;
    private RecyclerViewClickListener mListener;


    public MembersAttendanceAdapter(ArrayList<FyUser> membersList, RecyclerViewClickListener listener) {
        this.membersList = membersList;
        this.mListener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView nameTxt;
        private RecyclerViewClickListener mListener;
        private CheckBox checkBox;


        public MyViewHolder(View view, RecyclerViewClickListener listener) {
            super(view);
            mListener = listener;
            nameTxt = view.findViewById(R.id.nameItem);
            checkBox = view.findViewById(R.id.checkBox2);
        }
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.members_attend_items, parent, false);

        return new MyViewHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final FyUser user = membersList.get(position);
        final int pos = position;
        String name = user.getFirstname() + " " + user.getSurname();


        if(position %2 == 1)
        {
            holder.itemView.setBackgroundColor(Color.parseColor("#c0d6e4"));
        }

        holder.nameTxt.setText(name);

        holder.checkBox.setOnCheckedChangeListener(null);

        //if true, your checkbox will be selected, else unselected
        holder.checkBox.setChecked(user.isSelected());

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //set your object's last status
                user.setSelected(isChecked);
            }
        });

    }

    @Override
    public int getItemCount() {
        return membersList.size();
    }


    public interface RecyclerViewClickListener {

        void onClick(View view, int position);
    }

}
