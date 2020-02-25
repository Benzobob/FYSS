package com.fyss.controller;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.fyss.R;
import com.fyss.controller.ui.dashboard.sy.fragment.FragDashSy1;
import com.fyss.controller.ui.dashboard.sy.fragment.FragDashSy2;
import com.fyss.controller.ui.dashboard.sy.fragment.FragDashSy3;
import com.fyss.service.MyFirebaseMessagingService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;


import com.fyss.controller.ui.dashboard.adapter.SectionsPagerAdapterSy;
import com.google.firebase.iid.InstanceIdResult;

public class SyDashboardActivity extends AppCompatActivity
        implements FragDashSy1.OnFragmentInteractionListener,
        FragDashSy2.OnFragmentInteractionListener,
        FragDashSy3.OnFragmentInteractionListener {


    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_sy);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ImageButton profileBtn = findViewById(R.id.profileBtn);

        profileBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(SyDashboardActivity.this, SyProfileActivity.class);
                startActivity(intent);
            }
        });

        SectionsPagerAdapterSy sectionsPagerAdapterSy = new SectionsPagerAdapterSy(this, getSupportFragmentManager());

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapterSy);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);


    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
