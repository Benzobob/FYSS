package com.fyss.controller;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.fyss.R;
import com.fyss.model.Group;
import com.fyss.model.GroupMeeting;
import com.fyss.network.JsonPlaceHolderApi;
import com.fyss.network.RetrofitClientInstance;
import com.fyss.session.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SyAddMeeting extends AppCompatActivity {

    private EditText room, desc, topic;
    private NumberPicker weekNo;
    private TextView dateText, timeText, infoText, descText, buildText, weekText, roomText, dText, topicText;
    private DatePicker date;
    private TimePicker time;
    private Spinner building;
    private ImageButton backBtn;
    private FloatingActionButton addBtn;
    private GroupMeeting meeting;
    private long mLastClickTime = 0;
    private SessionManager sm;

    private int year, month, day, hour, minute;

    private Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
    private final JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meeting);
        sm = new SessionManager(getApplicationContext());

        date        = findViewById(R.id.dateTxt);
        time        = findViewById(R.id.time_picker);
        room        = findViewById(R.id.roomNo);
        weekNo      = findViewById(R.id.weekNo);
        building    = findViewById(R.id.buildingsId);
        addBtn      = findViewById(R.id.actionBtn);
        dateText    = findViewById(R.id.dateId);
        timeText    = findViewById(R.id.timeId);
        infoText    = findViewById(R.id.infoId);
        descText    = findViewById(R.id.descId);
        buildText   = findViewById(R.id.bt);
        weekText    = findViewById(R.id.wt);
        roomText    = findViewById(R.id.rt);
        desc        = findViewById(R.id.descTxt);
        topic       = findViewById(R.id.topicTxt);
        dText       = findViewById(R.id.Description);
        topicText   = findViewById(R.id.Topic);
        backBtn     = findViewById(R.id.backBtn);

        weekNo.setMinValue(1);
        weekNo.setMaxValue(12);


        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(date.getVisibility() == View.GONE) {
                    date.setVisibility(View.VISIBLE);
                    dateText.setBackgroundColor(getResources().getColor(R.color.colorPurple));
                    hideOthers(1);
                    addBtn.hide();
                }
            }
        });

        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(time.getVisibility() == View.GONE) {
                    time.setVisibility(View.VISIBLE);
                    timeText.setBackgroundColor(getResources().getColor(R.color.colorPurple));
                    hideOthers(2);
                    addBtn.hide();
                }
            }
        });

        infoText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildText.setVisibility(View.VISIBLE);
                roomText.setVisibility(View.VISIBLE);
                weekText.setVisibility(View.VISIBLE);
                weekNo.setVisibility(View.VISIBLE);
                room.setVisibility(View.VISIBLE);
                building.setVisibility(View.VISIBLE);
                infoText.setBackgroundColor(getResources().getColor(R.color.colorPurple));
                hideOthers(3);
                addBtn.hide();

            }
        });

        descText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                desc.setVisibility(View.VISIBLE);
                topic.setVisibility(View.VISIBLE);
                dText.setVisibility(View.VISIBLE);
                topicText.setVisibility(View.VISIBLE);
                descText.setBackgroundColor(getResources().getColor(R.color.colorPurple));
                hideOthers(4);
                addBtn.show();

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


        addBtn.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View arg0) {

                //Prevents double clicks
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                addBtn.setEnabled(false);

                boolean flag;

                year = date.getYear();
                month = date.getMonth() - 1;
                day = date.getDayOfMonth();
                hour = time.getHour();
                minute = time.getMinute();
                int weekNum = weekNo.getValue();

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String strDate = format.format(calendar.getTime());
                String strTime = "" + hour + ":" + minute + ":00";
                if(hour < 10){
                    strTime = "0" + hour + ":" + minute + ":00";
                }
                strDate = strDate + "T" +strTime;

                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                try {
                    Date tester = format1.parse(strDate);
                    strDate = format1.format(tester);
                    flag = true;
                } catch (ParseException e) {
                    e.printStackTrace();
                    flag = false;
                }

                if(flag) {
                    meeting = new GroupMeeting();
                    meeting.setTopic(topic.getText().toString());
                    meeting.setDescription(desc.getText().toString());
                    meeting.setGMDate(strDate);
                    meeting.setBuilding(building.getSelectedItem().toString());
                    meeting.setRoom(room.getText().toString());
                    meeting.setWeekNum(weekNum);
                    getGroupId();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Please re-enter date/time.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void getGroupId() {
        int id;
        HashMap<String, String> user = sm.getUserDetails();
        if (user.get(SessionManager.KEY_USER_ID) != null) {
            id = Integer.parseInt(user.get(SessionManager.KEY_USER_ID));
            Call<ResponseBody> call = jsonPlaceHolderApi.getGroupId(id);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (!response.isSuccessful()) {
                        String result = "Cod: " + response.code();
                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                    }
                    else{
                        String gid = null;
                        try {
                            gid = response.body().string();
                            setGroupForMeeting(Integer.parseInt(gid));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void setGroupForMeeting(int gid) {
        Call<Group> call = jsonPlaceHolderApi.findGroupById(gid);

        call.enqueue(new Callback<Group>() {
            @Override
            public void onResponse(Call<Group> call, Response<Group> response) {
                if (!response.isSuccessful()) {
                    String result = "Code " + response.code();
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                    return;
                }

                meeting.setGid(response.body());
                addMeeting();
            }

            @Override
            public void onFailure(Call<Group> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addMeeting() {
        Call<Void> call = jsonPlaceHolderApi.create(meeting);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    String result = "Cde: " + response.code();
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(getApplicationContext(), "Meeting Added.\nGroup will be notified.", Toast.LENGTH_LONG).show();

                Intent i = new Intent(getApplicationContext(), SyDashboardActivity.class);
                startActivity(i);
                finish();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void hideOthers(int i){
        switch (i) {
            case 1:
                time.setVisibility(View.GONE);
                building.setVisibility(View.GONE);
                room.setVisibility(View.GONE);
                weekNo.setVisibility(View.GONE);
                buildText.setVisibility(View.GONE);
                roomText.setVisibility(View.GONE);
                weekText.setVisibility(View.GONE);
                desc.setVisibility(View.GONE);
                topic.setVisibility(View.GONE);
                dText.setVisibility(View.GONE);
                topicText.setVisibility(View.GONE);
                timeText.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                infoText.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                descText.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                break;
            case 2:
                date.setVisibility(View.GONE);
                building.setVisibility(View.GONE);
                room.setVisibility(View.GONE);
                weekNo.setVisibility(View.GONE);
                buildText.setVisibility(View.GONE);
                roomText.setVisibility(View.GONE);
                weekText.setVisibility(View.GONE);
                desc.setVisibility(View.GONE);
                topic.setVisibility(View.GONE);
                dText.setVisibility(View.GONE);
                topicText.setVisibility(View.GONE);
                dateText.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                infoText.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                descText.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                break;
            case 3:
                date.setVisibility(View.GONE);
                time.setVisibility(View.GONE);
                desc.setVisibility(View.GONE);
                topic.setVisibility(View.GONE);
                dText.setVisibility(View.GONE);
                topicText.setVisibility(View.GONE);
                dateText.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                timeText.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                descText.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                break;
            case 4:
                date.setVisibility(View.GONE);
                time.setVisibility(View.GONE);
                building.setVisibility(View.GONE);
                room.setVisibility(View.GONE);
                weekNo.setVisibility(View.GONE);
                buildText.setVisibility(View.GONE);
                roomText.setVisibility(View.GONE);
                weekText.setVisibility(View.GONE);
                dateText.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                timeText.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                infoText.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                break;
        }
    }

}
