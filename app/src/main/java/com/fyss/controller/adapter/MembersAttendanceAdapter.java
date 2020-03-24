package com.fyss.controller.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.fyss.R;
import com.fyss.model.FyUser;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.fyss.service.Const.PREFS_NAME;
import static com.fyss.service.Const.PREF_DARK_THEME;

public class MembersAttendanceAdapter extends RecyclerView.Adapter<MembersAttendanceAdapter.MyViewHolder>{

    private ArrayList<FyUser> membersList;
    private RecyclerViewClickListener mListener;


    public MembersAttendanceAdapter(ArrayList<FyUser> membersList, RecyclerViewClickListener listener) {
        this.membersList = membersList;
        this.mListener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView nameTxt;
        private RecyclerViewClickListener mListener;
        private Switch switchBtn;


        public MyViewHolder(final View view, RecyclerViewClickListener listener) {
            super(view);
            mListener = listener;
            nameTxt = view.findViewById(R.id.nameItem);
            switchBtn = view.findViewById(R.id.switch1);
            switchBtn.setOnClickListener(this);
            switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b){
                        mListener.onClick(view,getAdapterPosition());
                    }
                }
            });
        }


        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);

        if(useDarkTheme) {
            parent.getContext().setTheme(R.style.AppTheme_DarkTheme_NoActionBar);
        }else{
            parent.getContext().setTheme(R.style.AppTheme_LightTheme_NoActionBar);
        }
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.members_attend_items, parent, false);

        return new MyViewHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder( MyViewHolder holder, int position) {
        FyUser user = membersList.get(position);
        int pos = position;
        String name = user.getFirstname() + " " + user.getSurname();


        if(position %2 == 1)
        {
            holder.itemView.setBackgroundColor(Color.parseColor("#c0d6e4"));
        }

        holder.nameTxt.setText(name);



    }

    @Override
    public int getItemCount() {
        return membersList.size();
    }




    public interface RecyclerViewClickListener {

        void onClick(View v, int position);
    }

}
