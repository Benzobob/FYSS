package com.fyss.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import static com.fyss.service.Const.PREFS_NAME;
import static com.fyss.service.Const.PREF_DARK_THEME;
import androidx.appcompat.app.AppCompatActivity;

import com.fyss.R;
import com.fyss.model.Group;
import com.fyss.model.Posts;
import com.fyss.model.SyUser;
import com.fyss.network.JsonPlaceHolderApi;
import com.fyss.network.RetrofitClientInstance;
import com.fyss.session.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SyAddPostActivity extends AppCompatActivity {

    private SessionManager sm;
    private Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
    private final JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
    private ImageButton backBtn;
    private EditText title, body;
    private FloatingActionButton addBtn;
    private SyUser user;
    private String syid,b,t;
    private Group g;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);

        if(useDarkTheme) {
            setTheme(R.style.AppTheme_DarkTheme_NoActionBar);
        }else{
            setTheme(R.style.AppTheme_LightTheme_NoActionBar);
        }
        setContentView(R.layout.activity_add_post);
        sm = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sm.getUserDetails();
        if (user.get(SessionManager.KEY_USER_ID) != null) {
            syid = user.get(SessionManager.KEY_USER_ID);
        }


        backBtn = findViewById(R.id.backBtn);
        addBtn = findViewById(R.id.floatingActionButton);
        body = findViewById(R.id.postBody);
        title = findViewById(R.id.postTitle);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInput();
            }
        });
    }

    private void validateInput() {
         t = title.getText().toString();
         b = body.getText().toString();

        if (t.matches("") || b.matches("")) {
            Toast.makeText(getApplicationContext(), "Please fill out entire form.", Toast.LENGTH_LONG).show();
        } else {
            setUser();
        }
    }

    private void setUser() {
        Call<SyUser> call = jsonPlaceHolderApi.findSyUserById(Integer.parseInt(syid));

        call.enqueue(new Callback<SyUser>() {
            @Override
            public void onResponse(Call<SyUser> call, Response<SyUser> response) {
                if (!response.isSuccessful()) {
                    String result = "Cod: " + response.code();
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                } else {
                    user = response.body();
                    setGroup(user.getSyid());
                }
            }

            @Override
            public void onFailure(Call<SyUser> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setGroup(Integer syid) {
        Call<Group> call = jsonPlaceHolderApi.getGroupSy(syid);

        call.enqueue(new Callback<Group>() {
            @Override
            public void onResponse(Call<Group> call, Response<Group> response) {
                if (!response.isSuccessful()) {
                    String result = "Cod: " + response.code();
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                } else {
                    g = response.body();
                    addPost();
                }
            }

            @Override
            public void onFailure(Call<Group> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addPost() {
        Posts p = new Posts();
        p.setBody(b);
        p.setTitle(t);
        p.setSyid(user);
        p.setGid(g);

        Call<Void> call = jsonPlaceHolderApi.create(p);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    String result = "Cde: " + response.code();
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(getApplicationContext(), "Post Created\nGroup will be notified.", Toast.LENGTH_LONG).show();

                Intent i = new Intent(getApplicationContext(), SyDashboardActivity.class);
                startActivity(i);
                finish();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }
}
