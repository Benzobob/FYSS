package com.fyss.controller;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import static com.fyss.service.Const.PREFS_NAME;
import static com.fyss.service.Const.PREF_DARK_THEME;
import com.fyss.R;
import com.fyss.model.GroupMeeting;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FyMeetingPageActivity extends FragmentActivity implements OnMapReadyCallback {


    private Button attendBtn, pvaBtn, qualBtn;
    private ImageButton backBtn, mapBtn;
    private SharedPreferences sharedPreferences;
    private Toolbar toolbar;
    private TextView meeting_num, topic, desc;
    private TextView weekNo, date, time, building, room;
    private LinearLayout when, where;
    private GoogleMap mMap;
    private GroupMeeting meeting;
    private SupportMapFragment mapFragment;
    private ScrollView sv;

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
        setContentView(R.layout.activity_meeting_page_fy);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        mapBtn  = findViewById(R.id.mapBtn);
        sv = findViewById(R.id.scrollView3);

        //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        //When and where
        setMeetingDetails();

        //Topic and Description
        setMeetingInfo();

        setOnClickListeners();

        mapFragment.getView().setVisibility(View.GONE);
    }

    private void setOnClickListeners() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleMap();
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
        String da = meeting.getDateStr();

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



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng building = new LatLng(meeting.getLon(), meeting.getLat());
        mMap.addMarker(new MarkerOptions().position(building).title(meeting.getBuilding() + " Building"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(building, 17.0f));
    }

    public void toggleMap() {

        if(mapFragment.getView().getVisibility() == View.GONE){
                mapFragment.getView().setVisibility(View.VISIBLE);
                sv.setVisibility(View.GONE);
            LatLng building = new LatLng(meeting.getLon(), meeting.getLat());
            mMap.addMarker(new MarkerOptions().position(building).title(meeting.getBuilding() + " Building"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(building, 17.0f));
        }
        else{
            mapFragment.getView().setVisibility(View.GONE);
            sv.setVisibility(View.VISIBLE);
        }
    }
}