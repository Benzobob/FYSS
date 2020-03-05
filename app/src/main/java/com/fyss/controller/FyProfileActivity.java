package com.fyss.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fyss.R;
import com.fyss.model.FyUser;
import com.fyss.model.SyUser;
import com.fyss.network.JsonPlaceHolderApi;
import com.fyss.network.RetrofitClientInstance;
import com.fyss.session.SessionManager;

import java.util.HashMap;

import retrofit2.Retrofit;

public class FyProfileActivity extends AppCompatActivity {
    private TextView name, email, num, group;
    private FyUser user;
    private ImageButton homeBtn, settingsBtn;
    private SessionManager sm;
    private Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
    private final JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        user = (FyUser) getIntent().getSerializableExtra("user");
        sm = new SessionManager(getApplicationContext());

        name = findViewById(R.id.nameText);
        email = findViewById(R.id.emailText);
        num = findViewById(R.id.numText);
        group = findViewById(R.id.groupText);
        homeBtn = findViewById(R.id.homeBtn);
        settingsBtn = findViewById(R.id.settingsBtn);

        setInfo();
        //check if you are on your own page or on another persons page
        HashMap<String, String> loggedInUser = sm.getUserDetails();
        if (loggedInUser.get(SessionManager.KEY_USER_ID) != null) {
            if (user.getFyid().toString().matches(loggedInUser.get(SessionManager.KEY_USER_ID)) &&
                    loggedInUser.get(SessionManager.KEY_USER_TYPE).matches("FY")) {
                setOnClickListeners("FY");
            }
            else if(loggedInUser.get(SessionManager.KEY_USER_TYPE).matches("FY")){
                settingsBtn.setVisibility(View.GONE);
                setOnClickListeners("FY");
            }
            else{
                settingsBtn.setVisibility(View.GONE);
                setOnClickListeners("SY");
            }
        }
    }

    private void setOnClickListeners(final String type) {
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if(type.matches("FY")){intent = new Intent(getApplicationContext(), FyDashboardActivity.class);}
                else{intent = new Intent(getApplicationContext(), SyDashboardActivity.class);}
                startActivity(intent);
                finish();
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FyEditProfileActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setInfo() {
        name.setText(user.getFirstname() + " " + user.getSurname());
        email.setText(user.getEmail());
        num.setText(user.getPhoneNum());
        group.setText("1st Year - Member of group " + user.getGid().getGid());
    }

}
