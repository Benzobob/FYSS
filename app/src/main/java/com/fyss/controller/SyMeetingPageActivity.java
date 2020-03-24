package com.fyss.controller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import static com.fyss.service.Const.PREFS_NAME;
import static com.fyss.service.Const.PREF_DARK_THEME;
import com.fyss.R;
import com.fyss.model.GroupMeeting;
import com.fyss.service.PushReminder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SyMeetingPageActivity extends AppCompatActivity{

    private Button attendBtn, pvaBtn, qualBtn;
    private FloatingActionButton remidnerBtn;
    private ImageButton backBtn, editBtn;
    private SharedPreferences sharedPreferences;
    private Toolbar toolbar;
    private TextView meeting_num, topic, desc;
    private WebView pvaWeb;
    private String dateTime, building, room, gmid;
    private boolean showReminderBtn = true;

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);

        if(useDarkTheme) {
            setTheme(R.style.AppTheme_DarkTheme_NoActionBar);
        }else{
            setTheme(R.style.AppTheme_LightTheme_NoActionBar);
        }
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
        remidnerBtn = findViewById(R.id.floatingActionButton2);


        WebSettings webSettings = pvaWeb.getSettings();
        webSettings.setJavaScriptEnabled(true);

        WebViewClientImpl webViewClient = new WebViewClientImpl(this);
        pvaWeb.setWebViewClient(webViewClient);

        dateTime = meeting.getDateStr();
        building = meeting.getBuilding();
        gmid = meeting.getGmid().toString();
        room = meeting.getRoom();
        topic.setText(meeting.getTopic());
        desc.setText(meeting.getDescription());
        meeting_num.setText("Group Meeting - Week " + meeting.getWeekNum());
        attendBtn = findViewById(R.id.attendBtn);

        checkIfReminderHasBeenSet();

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
                    remidnerBtn.setVisibility(View.GONE);
                }
                else{
                    pvaBtn.setTextColor(getResources().getColor(R.color.colorWhite));
                    pvaWeb.setVisibility(View.GONE);
                    if(showReminderBtn) remidnerBtn.setVisibility(View.VISIBLE);
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
                else {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "com.qualtrics.xm")));

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
                finish();

            }
        });

        remidnerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpCalendar();
                remidnerBtn.setVisibility(View.GONE);
            }
        });


    }

    private void checkIfReminderHasBeenSet() {
        SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String[] projection = new String[] { CalendarContract.Events.CALENDAR_ID, CalendarContract.Events.TITLE, CalendarContract.Events.DESCRIPTION, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND, CalendarContract.Events.ALL_DAY, CalendarContract.Events.EVENT_LOCATION };
        Date d = null;
        try {
            d = output.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long HOUR = 3600*1000;
        Date endTime = new Date(d.getTime() + HOUR);

        String selection = "(( " + CalendarContract.Events.DTSTART + " >= " + d.getTime() + " ) AND ( " + CalendarContract.Events.DTSTART + " <= " + endTime.getTime() + " ))";

        Cursor cursor = this.getBaseContext().getContentResolver().query( CalendarContract.Events.CONTENT_URI, projection, selection, null, null );

        if (cursor.moveToFirst()) {
            do {
                showReminderBtn = false;
                remidnerBtn.setVisibility(View.GONE);
            } while ( cursor.moveToNext());
        }
    }

    private void setUpCalendar() {
        SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date d = null;
        try {
            d = output.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long HOUR = 3600*1000;
        Date endTime = new Date(d.getTime() + HOUR);
        String loc =  building + " Building. " + room;
        PushReminder reminder = new PushReminder( loc, d, endTime,  SyMeetingPageActivity.this);
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


