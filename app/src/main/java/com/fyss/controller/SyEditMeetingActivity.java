package com.fyss.controller;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import static com.fyss.service.Const.PREFS_NAME;
import static com.fyss.service.Const.PREF_DARK_THEME;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
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


    private EditText room, desc, topic;;
    private NumberPicker weekNo;
    private TextView dateText, timeText, infoText, descText, buildText, weekText, roomText, dText, topicText;;
    private DatePicker date;
    private TimePicker time;
    private Spinner building;
    private FloatingActionButton addBtn;
    private GroupMeeting meeting;
    private ImageButton backBtn, delBtn;
    private Double lon, lat;

    private SessionManager sm;

    private int year, month, day, hour, minute;

    private Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
    private final JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);

        if(useDarkTheme) {
            setTheme(R.style.AppTheme_DarkTheme_NoActionBar);
        }else{
            setTheme(R.style.AppTheme_LightTheme_NoActionBar);
        }
        setContentView(R.layout.activity_edit_meeting);
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
        desc        = findViewById(R.id.descTxt);
        topic       = findViewById(R.id.topicTxt);
        dText       = findViewById(R.id.Description);
        topicText   = findViewById(R.id.Topic);
        delBtn      = findViewById(R.id.delBtn);
        backBtn     = findViewById(R.id.backBtn);

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

        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyDelete();
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

                setLocation(building.getSelectedItem().toString());

                //meeting.setGMDate(strDate);
                meeting.setDateStr(strDate);
                meeting.setLon(lon);
                meeting.setLat(lat);
                meeting.setTopic(topic.getText().toString());
                meeting.setDescription(desc.getText().toString());
                meeting.setBuilding(building.getSelectedItem().toString());
                meeting.setRoom(room.getText().toString());
                meeting.setWeekNum(weekNum);
                updateMeeting(meeting);
            }
        });
    }

    private void verifyDelete() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == DialogInterface.BUTTON_POSITIVE) {
                    deleteAttendance();
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme));
        builder.setMessage("Are you sure you want \n to delete this meeting?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void deleteAttendance() {
        Call<Void> call = jsonPlaceHolderApi.removeAttendance(meeting.getGmid());

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    String result = "Cde: " + response.code();
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                    return;
                }
                deleteMeeting();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteMeeting() {
        Call<Void> call = jsonPlaceHolderApi.removeMeeting(meeting.getGmid().toString());

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    String result = "Cde: " + response.code();
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(getApplicationContext(), "Meeting Deleted.", Toast.LENGTH_LONG).show();

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
        //String dateTime [] = meeting.getGMDate().split("T");
        String dateTime [] = meeting.getDateStr().split("T");
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
        desc.setText(meeting.getDescription());
        topic.setText(meeting.getTopic());


        int i = building.getCount();
        for(int j = 0; j < i; j++){
            if(meeting.getBuilding().matches(building.getItemAtPosition(j).toString())){
                building.setSelection(j);
            }
        }
    }

    private void setLocation(String building) {
        switch (building) {
            case ("Computer Science"):
                lon = 52.673961;
                lat = -8.575635;
                break;
            case ("Foundation"):
                lon = 52.674531;
                lat = -8.573753;
                break;
            case ("Main"):
                lon = 52.674030;
                lat = -8.571921;
                break;
            case ("Schumann"):
                lon = 52.673248;
                lat = -8.577930;
                break;
            case ("Kemmy Business School"):
                lon = 52.672649;
                lat = -8.577061;
                break;
            case ("Tierney"):
                lon = 52.674502;
                lat = -8.577161;
                break;
            case ("Glucksman Library"):
                lon = 52.673341;
                lat = -8.573506;
                break;
            case ("Health Sciences"):
                lon = 52.677759;
                lat = -8.568910;
                break;
            case ("Schrödinger"):
                lon = 52.673864;
                lat =-8.567458;
                break;
        }
    }

    public void hideOthers(int i){
        int primaryColor = getColor(getApplicationContext(), R.attr.primary);
        switch (i) {
            case 1:
                time.setVisibility(View.GONE);
                building.setVisibility(View.GONE);
                room.setVisibility(View.GONE);
                weekNo.setVisibility(View.GONE);
                desc.setVisibility(View.GONE);
                topic.setVisibility(View.GONE);
                dText.setVisibility(View.GONE);
                topicText.setVisibility(View.GONE);
                buildText.setVisibility(View.GONE);
                roomText.setVisibility(View.GONE);
                weekText.setVisibility(View.GONE);
                timeText.setBackgroundColor(primaryColor);
                infoText.setBackgroundColor(primaryColor);
                descText.setBackgroundColor(primaryColor);

                break;
            case 2:
                date.setVisibility(View.GONE);
                building.setVisibility(View.GONE);
                room.setVisibility(View.GONE);
                weekNo.setVisibility(View.GONE);
                desc.setVisibility(View.GONE);
                topic.setVisibility(View.GONE);
                dText.setVisibility(View.GONE);
                topicText.setVisibility(View.GONE);
                buildText.setVisibility(View.GONE);
                roomText.setVisibility(View.GONE);
                weekText.setVisibility(View.GONE);
                dateText.setBackgroundColor(primaryColor);
                infoText.setBackgroundColor(primaryColor);
                descText.setBackgroundColor(primaryColor);

                break;
            case 3:
                date.setVisibility(View.GONE);
                time.setVisibility(View.GONE);
                desc.setVisibility(View.GONE);
                topic.setVisibility(View.GONE);
                dText.setVisibility(View.GONE);
                topicText.setVisibility(View.GONE);
                dateText.setBackgroundColor(primaryColor);
                timeText.setBackgroundColor(primaryColor);
                descText.setBackgroundColor(primaryColor);

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
                dateText.setBackgroundColor(primaryColor);
                timeText.setBackgroundColor(primaryColor);
                infoText.setBackgroundColor(primaryColor);
                break;
        }
    }

    public int getColor(Context context, int colorResId) {
        TypedValue typedValue = new TypedValue();
        TypedArray typedArray = context.obtainStyledAttributes(typedValue.data, new int[] {colorResId});
        int color = typedArray.getColor(0, 0);
        typedArray.recycle();
        return color;
    }
}
