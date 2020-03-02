package com.fyss.controller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

    private Button attendBtn, editBtn, pvaBtn;
    private ImageButton backBtn;
    private Toolbar toolbar;
    private TextView meeting_num, topic, desc;
    private WebView pvaWeb;

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
        editBtn = findViewById(R.id.editBtn);
        pvaBtn = findViewById(R.id.pvaBtn);
        pvaWeb = findViewById(R.id.pvaLink);
        backBtn = findViewById(R.id.backBtn);


        WebSettings webSettings = pvaWeb.getSettings();
        webSettings.setJavaScriptEnabled(true);

        WebViewClientImpl webViewClient = new WebViewClientImpl(this);
        pvaWeb.setWebViewClient(webViewClient);

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

        pvaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pvaWeb.getSettings().setJavaScriptEnabled(true);
                pvaWeb.loadUrl("https://www.studentvolunteer.ie/user-login/");
                if(pvaWeb.getVisibility() == View.GONE){
                    pvaWeb.setVisibility(View.VISIBLE);
                    pvaBtn.setTextColor(getResources().getColor(R.color.colorPurple));
                }
                else{
                    pvaBtn.setTextColor(getResources().getColor(R.color.colorWhite));
                    pvaWeb.setVisibility(View.GONE);
                }

            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SyEditMeetingActivity.class);
                intent.putExtra("meeting", meeting);
                startActivity(intent);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SyDashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if((keyCode == KeyEvent.KEYCODE_BACK) && this.pvaWeb.canGoBack()){
            this.pvaWeb.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}


