package com.fyss.controller.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fyss.R;
import com.fyss.model.GroupMeeting;

import java.util.List;

public class MeetingsAdapter extends RecyclerView.Adapter<MeetingsAdapter.MyViewHolder> {

    private List<GroupMeeting> meetingsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView weekNo, date, time, building, room;

        public MyViewHolder(View view) {
            super(view);
            weekNo =  view.findViewById(R.id.meetingNum);
            date =  view.findViewById(R.id.date);
            time =  view.findViewById(R.id.time);
            building = view.findViewById(R.id.building);
            room =  view.findViewById(R.id.room);
        }
    }

    public MeetingsAdapter(List<GroupMeeting> meetingsList) {
        this.meetingsList = meetingsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.meeting_items, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        GroupMeeting meeting = meetingsList.get(position);

        holder.date.setText(meeting.getGMDate());
        holder.building.setText(meeting.getBuilding());
        holder.weekNo.setText(meeting.getWeekNum().toString());
        holder.room.setText(meeting.getRoom());
    }

    @Override
    public int getItemCount() {
        return meetingsList.size();
    }
}
