package com.fyss.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fyss.R;
import com.fyss.model.FyUser;
import com.fyss.model.SyUser;
import com.fyss.network.JsonPlaceHolderApi;
import com.fyss.network.RetrofitClientInstance;
import com.fyss.session.SessionManager;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FyProfileActivity extends AppCompatActivity {
    private TextView name, email, num, group;
    private String id;
    private FyUser user;
    private ImageButton homeBtn, settingsBtn;
    private SessionManager sm;
    private Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
    private final JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        // user = (FyUser) getIntent().getSerializableExtra("user");
        id = (String) getIntent().getSerializableExtra("FyId");
        sm = new SessionManager(getApplicationContext());

        name = findViewById(R.id.nameText);
        email = findViewById(R.id.emailText);
        num = findViewById(R.id.numText);
        group = findViewById(R.id.groupText);
        homeBtn = findViewById(R.id.homeBtn);
        settingsBtn = findViewById(R.id.settingsBtn);

        setUser(id);
    }

    private void setOnClickListeners(final String type) {
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent;
                //if(type.matches("FY")){intent = new Intent(getApplicationContext(), FyDashboardActivity.class);}
                //else{intent = new Intent(getApplicationContext(), SyDashboardActivity.class);}
                //startActivity(intent);
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


    private void setUser(String id) {
        Call<FyUser> call = jsonPlaceHolderApi.findFyUserById(Integer.parseInt(id));

        call.enqueue(new Callback<FyUser>() {
            @Override
            public void onResponse(Call<FyUser> call, Response<FyUser> response) {
                if (!response.isSuccessful()) {
                    String result = "Cod: " + response.code();
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                } else {
                    user = response.body();
                    setInfo();
                    checkUserOnPage();
                }
            }

            @Override
            public void onFailure(Call<FyUser> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void checkUserOnPage() {
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


}
