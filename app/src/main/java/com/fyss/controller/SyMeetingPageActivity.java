package com.fyss.controller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fyss.R;
import com.fyss.controller.ui.dashboard.sy.fragment.FragDashSy2;
import com.fyss.model.GroupMeeting;
import com.fyss.service.MyFirebaseMessagingService;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;

public class SyMeetingPageActivity extends AppCompatActivity {

    private Button attendBtn;
    private Toolbar toolbar;
    private TextView meeting_num, topic, desc;

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_page_sy);
        final GroupMeeting meeting = (GroupMeeting) getIntent().getSerializableExtra("meeting");


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        meeting_num = findViewById(R.id.meeting_no);
        topic = findViewById(R.id.topic);
        desc = findViewById(R.id.description);

        topic.setText(meeting.getTopic());
        desc.setText(meeting.getDescription());
        meeting_num.setText("Group Meeting - Week " + meeting.getWeekNum());



        attendBtn = findViewById(R.id.attendBtn);

        attendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SyAttendanceActivity.class);
                intent.putExtra("meeting", meeting);
                startActivity(intent);
            }
        });
    }
}
