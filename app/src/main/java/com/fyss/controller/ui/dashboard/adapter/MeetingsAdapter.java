package com.fyss.controller.ui.dashboard.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fyss.R;
import com.fyss.model.GroupMeeting;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MeetingsAdapter extends RecyclerView.Adapter<MeetingsAdapter.MyViewHolder> {

    private ArrayList<GroupMeeting> meetingsList;
    private RecyclerViewClickListener mListener;


    public MeetingsAdapter(ArrayList<GroupMeeting> meetingsList, RecyclerViewClickListener listener) {
        this.meetingsList = meetingsList;
        this.mListener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView weekNo, date, time, building, room;
        private LinearLayout when, where;

        private RecyclerViewClickListener mListener;

        public MyViewHolder(View view, RecyclerViewClickListener listener) {
            super(view);
            mListener = listener;
            view.setOnClickListener(this);
            weekNo = view.findViewById(R.id.meetingNum);
            date = view.findViewById(R.id.date);
            time = view.findViewById(R.id.time);
            building = view.findViewById(R.id.building);
            room = view.findViewById(R.id.room);
            when = view.findViewById(R.id.whenLayout);
            where = view.findViewById(R.id.whereLayout);
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

        String format1 = "yyyy-MM-dd";
        String format2 = "dd-MM-yyyy";
        String time = "";
        Date d = null;
        String date = meeting.getGMDate();
        if (date != null) {
            String[] parts = date.split("T");
            date = parts[0];
            time = parts[1];

            time = time.substring(0, time.length() - 3);

            SimpleDateFormat formatter = new SimpleDateFormat(format1);
            try {
                d = formatter.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            formatter.applyPattern(format2);
            date = formatter.format(d);

        }

        boolean flag = checkIfMeetingHasHappened(meeting, d);

        holder.date.setText(date);
        holder.time.setText(time);
        holder.building.setText(meeting.getBuilding());
        holder.weekNo.setText("Meeting - Week " + meeting.getWeekNum().toString());
        holder.room.setText(meeting.getRoom());

        if (position % 2 == 1) {
            holder.where.setBackgroundColor(Color.parseColor("#D1C4E9"));
            holder.when.setBackgroundColor(Color.parseColor("#EDE7F6"));
        }

    }

    private boolean checkIfMeetingHasHappened(GroupMeeting meeting, Date meetTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR_OF_DAY, 1);
        Date current = cal.getTime();
        if(current.after(meetTime)){
            return true;
        }
        else
            return false;
    }

    @Override
    public int getItemCount() {
        return meetingsList.size();
    }


    public interface RecyclerViewClickListener {

        void onClick(View view, int position);
    }
}
