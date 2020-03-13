package com.fyss.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.fyss.R;
import com.fyss.model.SyUser;
import com.fyss.network.JsonPlaceHolderApi;
import com.fyss.network.RetrofitClientInstance;
import com.fyss.service.MyFirebaseMessagingService;
import com.fyss.session.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SyEditProfileActivity extends AppCompatActivity {
    private SessionManager session;
    private String fcmToken;
    private ImageButton logout, backBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(getApplicationContext());
        setContentView(R.layout.activity_edit_profile_sy);

        SyUser user = (SyUser) getIntent().getSerializableExtra("user");


        logout = findViewById(R.id.logoutBtn);
        backBtn = findViewById(R.id.backBtn);

        MyFirebaseMessagingService m = new MyFirebaseMessagingService();
        fcmToken = m.getToken(getApplicationContext());



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
