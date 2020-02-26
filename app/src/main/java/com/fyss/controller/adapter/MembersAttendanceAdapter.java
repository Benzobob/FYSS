package com.fyss.controller.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

        public MyViewHolder(View view, RecyclerViewClickListener listener) {
            super(view);
            mListener = listener;
            nameTxt =  view.findViewById(R.id.nameItem);
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
        FyUser user = membersList.get(position);

        String name = user.getFirstname() + " " + user.getSurname();

        holder.nameTxt.setText(name);


        if(position %2 == 1)
        {
            holder.itemView.setBackgroundColor(Color.parseColor("#c0d6e4"));
        }
        // else {
        //      holder.itemView.setBackgroundColor(Color.parseColor("#86959f"));
        //  }

    }

    @Override
    public int getItemCount() {
        return membersList.size();
    }


    public interface RecyclerViewClickListener {

        void onClick(View view, int position);
    }
}
