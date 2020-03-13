package com.fyss.controller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.fyss.R;
import com.fyss.model.GroupMeeting;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class SyMeetingPageActivity extends AppCompatActivity{

    private Button attendBtn, pvaBtn, qualBtn;
    private ImageButton backBtn, editBtn;
    private SharedPreferences sharedPreferences;
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
        qualBtn = findViewById(R.id.qualBtn);


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
                    Intent intent = new Intent(getApplicationContext(), SyAttendance.class);
                //intent.putExtra("meeting", meeting);
                saveMeeting(meeting);
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

        qualBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = getPackageManager().getLaunchIntentForPackage("com.qualtrics.xm");
                if (i != null) {
                    startActivity(i);
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
                //Intent intent = new Intent(getApplicationContext(), SyDashboardActivity.class);
                //startActivity(intent);
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

    private void saveMeeting(GroupMeeting meeting) {
        Gson gson = new Gson();
        String jsonText1 = gson.toJson(meeting);
        sharedPreferences = getSharedPreferences("Meeting", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("meeting", jsonText1);
        editor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences = getSharedPreferences("Meeting", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("meeting").commit();
    }
}


