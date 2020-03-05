package com.fyss.controller.ui.attendance;



import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.fyss.R;
import com.fyss.controller.ui.dashboard.adapter.SectionsPagerAdapterFy;
import com.google.android.material.tabs.TabLayout;


public class AttendanceActivity extends AppCompatActivity
        implements FragAttendance1.OnFragmentInteractionListener,
        FragAttendance2.OnFragmentInteractionListener {


    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        SectionsPagerAdapterFy sectionsPagerAdapterSy = new SectionsPagerAdapterFy(this, getSupportFragmentManager());

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapterSy);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);


    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}

