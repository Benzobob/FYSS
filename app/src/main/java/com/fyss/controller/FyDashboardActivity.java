package com.fyss.controller;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.fyss.R;
import com.fyss.controller.ui.dashboard.adapter.SectionsPagerAdapterFy;
import com.fyss.controller.ui.dashboard.fy.fragment.FragDashFy1;
import com.fyss.controller.ui.dashboard.fy.fragment.FragDashFy2;
import com.fyss.controller.ui.dashboard.fy.fragment.FragDashFy3;
import com.google.android.material.tabs.TabLayout;


public class FyDashboardActivity extends AppCompatActivity
        implements FragDashFy1.OnFragmentInteractionListener,
        FragDashFy2.OnFragmentInteractionListener,
        FragDashFy3.OnFragmentInteractionListener {


    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_fy);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ImageButton profileBtn = findViewById(R.id.profileBtn);

        profileBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(FyDashboardActivity.this, FyProfileActivity.class);
                startActivity(intent);
            }
        });

        ImageButton nfcBtn = findViewById(R.id.nfcBtn);

        nfcBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(FyDashboardActivity.this, FyAttendanceActivity.class);
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

