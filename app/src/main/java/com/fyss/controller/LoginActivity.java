package com.fyss.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fyss.model.FCM_Token;
import com.fyss.model.FyUser;
import com.fyss.model.SyUser;
import com.fyss.network.JsonPlaceHolderApi;
import com.fyss.R;
import com.fyss.network.RetrofitClientInstance;
import com.fyss.service.MyFirebaseMessagingService;
import com.fyss.session.SessionManager;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;

public class LoginActivity extends AppCompatActivity {

    EditText emailTxt, passwordTxt;
    TextView tv;
    Button btnLogin;
    private SessionManager sm;
    private String fcmToken;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MyFirebaseMessagingService m = new MyFirebaseMessagingService();
        fcmToken = m.getToken(getApplicationContext());

        sm = new SessionManager(getApplicationContext());
        emailTxt = findViewById(R.id.email);
        passwordTxt = findViewById(R.id.password);
        tv = findViewById(R.id.link_signup);
        btnLogin = findViewById(R.id.button);

        if (sm.isLoggedIn()) {
            HashMap<String, String> user = sm.getUserDetails();
            Intent intent;

            if (user.get(SessionManager.KEY_USER_ID) != null) {
                String type = user.get(SessionManager.KEY_USER_TYPE);

                assert type != null;
                switch (type) {
                    case "FY":
                        intent = new Intent(getApplicationContext(), FyDashboardActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case "SY":
                        intent = new Intent(getApplicationContext(), SyDashboardActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                }
            }
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginFy(emailTxt.getText().toString(), passwordTxt.getText().toString());
            }
        });

        //tODO: Make the add user a single activity, check if they want to sign up a fy or sy
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddUserActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    private void loginFy(String email, String password) {

        Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        final JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<ResponseBody> call = jsonPlaceHolderApi.loginFy(email, password, fcmToken);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    String token = null;
                    try {
                        token = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    sm.createLoginSession(token, emailTxt.getText().toString(), "FY");

                    Intent intent = new Intent(getApplicationContext(), FyDashboardActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    loginSy();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("TAG", "=======onFailure: " + t.toString());
                t.printStackTrace();

            }
        });
    }

    private void loginSy() {
        Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        final JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<ResponseBody> call = jsonPlaceHolderApi.loginSy(emailTxt.getText().toString(), passwordTxt.getText().toString(), fcmToken);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    String token = null;
                    try {
                        token = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    sm.createLoginSession(token, emailTxt.getText().toString(), "SY");

                    Intent intent = new Intent(getApplicationContext(), SyDashboardActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Credentials are not Valid.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("TAG", "=======onFailure: " + t.toString());
                t.printStackTrace();

            }
        });
    }
}




