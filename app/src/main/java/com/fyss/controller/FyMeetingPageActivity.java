package com.fyss.controller;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fyss.R;
import com.fyss.model.GroupMeeting;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FyMeetingPageActivity extends AppCompatActivity {


    private Button attendBtn, pvaBtn, qualBtn;
    private ImageButton backBtn, editBtn;
    private SharedPreferences sharedPreferences;
    private Toolbar toolbar;
    private TextView meeting_num, topic, desc;
    private TextView weekNo, date, time, building, room;
    private LinearLayout when, where;
    private GroupMeeting meeting;

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_page_fy);
        meeting = (GroupMeeting) getIntent().getSerializableExtra("meeting");

        weekNo = findViewById(R.id.meetingNum);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        building = findViewById(R.id.building);
        room = findViewById(R.id.room);
        when = findViewById(R.id.whenLayout);
        where = findViewById(R.id.whereLayout);
        meeting_num = findViewById(R.id.meeting_no);
        topic = findViewById(R.id.topic);
        desc = findViewById(R.id.description);
        toolbar = findViewById(R.id.toolbar);
        backBtn = findViewById(R.id.backBtn);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //When and where
        setMeetingDetails();

        //Topic and Description
        setMeetingInfo();

        setOnClickListeners();
    }

    private void setOnClickListeners() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setMeetingInfo() {
        topic.setText(meeting.getTopic());
        desc.setText(meeting.getDescription());
        meeting_num.setText("Group Meeting - Week " + meeting.getWeekNum());
    }

    private void setMeetingDetails() {
        String format1 = "yyyy-MM-dd";
        String format2 = "dd-MM-yyyy";
        String ti = "";
        Date d = null;
        String da = meeting.getGMDate();

        if (da != null) {
            String[] parts = da.split("T");
            da = parts[0];
            ti = parts[1];

            ti = ti.substring(0, ti.length() - 3);

            SimpleDateFormat formatter = new SimpleDateFormat(format1);
            try {
                d = formatter.parse(da);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            formatter.applyPattern(format2);
            da = formatter.format(d);

        }

        date.setText(da);
        time.setText(ti);
        building.setText(meeting.getBuilding());
        room.setText(meeting.getRoom());
    }
}