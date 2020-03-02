package com.fyss.controller;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.fyss.R;
import com.fyss.model.GroupMeeting;
import com.fyss.network.JsonPlaceHolderApi;
import com.fyss.network.RetrofitClientInstance;
import com.fyss.session.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SyEditMeetingActivity extends AppCompatActivity {


    private EditText room;
    private NumberPicker weekNo;
    private TextView dateText, timeText, infoText, descText, buildText, weekText, roomText;
    private DatePicker date;
    private TimePicker time;
    private Spinner building;
    private FloatingActionButton addBtn;
    private GroupMeeting meeting;

    private SessionManager sm;

    private int year, month, day, hour, minute;

    private Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
    private final JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meeting);
        sm = new SessionManager(getApplicationContext());
        meeting = (GroupMeeting) getIntent().getSerializableExtra("meeting");

        date = findViewById(R.id.dateTxt);
        time = findViewById(R.id.time_picker);
        room = findViewById(R.id.roomNo);
        weekNo = findViewById(R.id.weekNo);
        building = findViewById(R.id.buildingsId);
        addBtn = findViewById(R.id.actionBtn);
        dateText = findViewById(R.id.dateId);
        timeText = findViewById(R.id.timeId);
        infoText = findViewById(R.id.infoId);
        descText = findViewById(R.id.descId);
        buildText = findViewById(R.id.bt);
        weekText = findViewById(R.id.wt);
        roomText = findViewById(R.id.rt);

        weekNo.setMinValue(1);
        weekNo.setMaxValue(12);

        setInformation(meeting);


        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (date.getVisibility() == View.GONE) {
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
                if (time.getVisibility() == View.GONE) {
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

                descText.setBackgroundColor(getResources().getColor(R.color.colorPurple));
                hideOthers(4);
                addBtn.show();

            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                year = date.getYear();
                month = date.getMonth();
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

                meeting.setGMDate(strDate);
                meeting.setBuilding(building.getSelectedItem().toString());
                meeting.setRoom(room.getText().toString());
                meeting.setWeekNum(weekNum);
                updateMeeting(meeting);
            }
        });
    }

    public void updateMeeting(GroupMeeting meeting){
        Call<Void> call = jsonPlaceHolderApi.editGroupMeeting(meeting.getGmid(), meeting);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    String result = "Cde: " + response.code();
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(getApplicationContext(), "Meeting Information Updated.", Toast.LENGTH_LONG).show();

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

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setInformation(GroupMeeting meeting) {
        String dateTime [] = meeting.getGMDate().split("T");
        String dateArray [] = dateTime[0].split("-");
        String timeArray [] = dateTime[1].split(":");
        int yyyy = Integer.parseInt(dateArray[0]);
        int MM = Integer.parseInt(dateArray[1]);
        int dd = Integer.parseInt(dateArray[2]);

        int HH = Integer.parseInt(timeArray[0]);
        int mm = Integer.parseInt(timeArray[1]);

        room.setText(meeting.getRoom());
        date.updateDate(yyyy, MM, dd);
        time.setHour(HH);
        time.setMinute(mm);
        weekNo.setValue(meeting.getWeekNum());


        int i = building.getCount();
        for(int j = 0; j < i; j++){
            if(meeting.getBuilding().matches(building.getItemAtPosition(j).toString())){
                building.setSelection(j);
            }
        }
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
                dateText.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                infoText.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                descText.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                break;
            case 3:
                date.setVisibility(View.GONE);
                time.setVisibility(View.GONE);
                dateText.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                timeText.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                descText.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                break;
            case 4:
                date.setVisibility(View.GONE);
                time.setVisibility(View.GONE);
                building.setVisibility(View.GONE);
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
