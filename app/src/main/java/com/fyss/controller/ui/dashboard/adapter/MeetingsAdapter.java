package com.fyss.controller.ui.dashboard.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fyss.R;
import com.fyss.model.GroupMeeting;

import java.util.ArrayList;

public class MeetingsAdapter extends RecyclerView.Adapter<MeetingsAdapter.MyViewHolder>{

    private ArrayList<GroupMeeting> meetingsList;
    private RecyclerViewClickListener mListener;


    public MeetingsAdapter(ArrayList<GroupMeeting> meetingsList, RecyclerViewClickListener listener) {
        this.meetingsList = meetingsList;
        this.mListener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView weekNo, date, time, building, room;
        private ImageButton delBtn, editBtn;

        private RecyclerViewClickListener mListener;

        public MyViewHolder(View view, RecyclerViewClickListener listener) {
            super(view);
            mListener = listener;
            view.setOnClickListener(this);
            weekNo =  view.findViewById(R.id.meetingNum);
            date =  view.findViewById(R.id.date);
            time =  view.findViewById(R.id.time);
            building = view.findViewById(R.id.building);
            room =  view.findViewById(R.id.room);
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
                .inflate(R.layout.meeting_items, parent, false);

        return new MyViewHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        GroupMeeting meeting = meetingsList.get(position);

        String date = meeting.getGMDate();
        String[] parts = date.split("T");
        date = parts[0];
        String time = parts[1];

        holder.date.setText(date);
        holder.time.setText(time);
        holder.building.setText(meeting.getBuilding());
        holder.weekNo.setText("Meeting " + meeting.getWeekNum().toString());
        holder.room.setText(meeting.getRoom());

        if(position %2 == 1)
        {
            holder.itemView.setBackgroundColor(Color.parseColor("#D8BFD8"));
        }
        else {
            holder.itemView.setBackgroundColor(Color.parseColor("#CCCCFF"));
        }

    }

    @Override
    public int getItemCount() {
        return meetingsList.size();
    }


    public interface RecyclerViewClickListener {

        void onClick(View view, int position);
    }
}
