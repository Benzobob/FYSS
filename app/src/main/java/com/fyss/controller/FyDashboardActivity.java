package com.fyss.controller;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.fyss.R;
import com.fyss.controller.ui.attendance.FyAttendance;
import com.fyss.controller.ui.dashboard.adapter.SectionsPagerAdapterFy;
import com.fyss.controller.ui.dashboard.fy.fragment.FragDashFy1;
import com.fyss.controller.ui.dashboard.fy.fragment.FragDashFy2;
import com.fyss.controller.ui.dashboard.fy.fragment.FragDashFy3;
import com.fyss.model.FyUser;
import com.fyss.session.SessionManager;
import com.google.android.material.tabs.TabLayout;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FyDashboardActivity extends AppCompatActivity
        implements FragDashFy1.OnFragmentInteractionListener,
        FragDashFy2.OnFragmentInteractionListener,
        FragDashFy3.OnFragmentInteractionListener {


    private Toolbar toolbar;
    private SessionManager sm;
    private String FyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_fy);
        sm = new SessionManager(getApplicationContext());

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        HashMap<String, String> user = sm.getUserDetails();
        if (user.get(SessionManager.KEY_USER_ID) != null) {
            FyId  = user.get(SessionManager.KEY_USER_ID);
        }


        ImageButton profileBtn = findViewById(R.id.profileBtn);

        profileBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(FyDashboardActivity.this, FyProfileActivity.class);
                intent.putExtra("FyId", FyId);
                startActivity(intent);
            }
        });

        ImageButton nfcBtn = findViewById(R.id.nfcBtn);

        nfcBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(FyDashboardActivity.this, FyAttendance.class);
                startActivity(intent);
            }
        });

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

