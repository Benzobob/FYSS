package com.fyss.controller.ui.dashboard.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.fyss.R;
import com.fyss.model.FyUser;

import java.util.ArrayList;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MyViewHolder>{

    private ArrayList<FyUser> membersList;
    private RecyclerViewClickListener mListener;


    public MembersAdapter(ArrayList<FyUser> membersList, RecyclerViewClickListener listener) {
        this.membersList = membersList;
        this.mListener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView nameTxt, emailTxt;
        private RecyclerViewClickListener mListener;
        private ConstraintLayout constInner;

        public MyViewHolder(View view, RecyclerViewClickListener listener) {
            super(view);
            mListener = listener;
            view.setOnClickListener(this);
            nameTxt =  view.findViewById(R.id.nameItem);
            constInner = view.findViewById(R.id.innerConst);
            emailTxt = view.findViewById(R.id.emailTextView);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.members_items, parent, false);

        return new MyViewHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        FyUser user = membersList.get(position);

        String name = user.getFirstname() + " " + user.getSurname();

        holder.nameTxt.setText(name);
        holder.emailTxt.setText(user.getEmail());


        if(position %2 == 1)
        {
            holder.constInner.setBackgroundColor(Color.parseColor("#EDE7F6"));
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
