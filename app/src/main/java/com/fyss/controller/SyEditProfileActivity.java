package com.fyss.controller;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.fyss.service.Const.PREFS_NAME;
import static com.fyss.service.Const.PREF_DARK_THEME;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.IntentCompat;

import com.fyss.R;
import com.fyss.model.SyUser;
import com.fyss.network.JsonPlaceHolderApi;
import com.fyss.network.RetrofitClientInstance;
import com.fyss.service.MyFirebaseMessagingService;
import com.fyss.session.SessionManager;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SyEditProfileActivity extends AppCompatActivity {
    private SessionManager session;
    private String fcmToken;
    private ImageButton logout, backBtn;
    private Switch themeBtn;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);

        if(useDarkTheme) {
            setTheme(R.style.AppTheme_DarkTheme_NoActionBar);
        }else{
            setTheme(R.style.AppTheme_LightTheme_NoActionBar);
        }

        super.onCreate(savedInstanceState);
        session = new SessionManager(getApplicationContext());
        setContentView(R.layout.activity_edit_profile_sy);

        //SyUser user = (SyUser) getIntent().getSerializableExtra("user");

        logout = findViewById(R.id.logoutBtn);
        backBtn = findViewById(R.id.backBtn);
        themeBtn = findViewById(R.id.themeBtn);
        themeBtn.setChecked(useDarkTheme);


        MyFirebaseMessagingService m = new MyFirebaseMessagingService();
        fcmToken = m.getToken(getApplicationContext());


        themeBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    toggleTheme(isChecked);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                session.logoutUser();
                removeFcmToken(fcmToken);
                Intent intent = new Intent(SyEditProfileActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
    }

    private void toggleTheme(boolean darkTheme) throws InterruptedException {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(PREF_DARK_THEME, darkTheme);
        editor.apply();
        Thread.sleep(97);
        AppRestart.doRestart(this);



        /*Intent intent = getIntent();
        finish();

        startActivity(intent);*/
    }

    private void removeFcmToken(String fcmToken) {
        Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        final JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<Void> call = jsonPlaceHolderApi.removeFcmToken(fcmToken);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i("TAG", "success");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("TAG", "=======onFailure: " + t.toString());
                t.printStackTrace();

            }
        });
    }
}
