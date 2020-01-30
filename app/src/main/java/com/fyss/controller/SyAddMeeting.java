package com.fyss.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fyss.R;
import com.fyss.model.Group;
import com.fyss.model.GroupMeeting;
import com.fyss.model.SyUser;
import com.fyss.network.JsonPlaceHolderApi;
import com.fyss.network.RetrofitClientInstance;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SyAddMeeting extends AppCompatActivity {

    EditText date, time, room, weekNo;
    Spinner building;
    FloatingActionButton addBtn;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meeting);

        date     = findViewById(R.id.dateTxt);
        time     = findViewById(R.id.timeTxt);
        room     = findViewById(R.id.roomTxt);
        weekNo   = findViewById(R.id.weekNotxt);
        building = findViewById(R.id.buildingsId);
        addBtn   = findViewById(R.id.actionBtn);

        Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        final JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        addBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                GroupMeeting meeting = new GroupMeeting();
                meeting.setGMDate(date.getText().toString() + "T" + time.getText().toString() + ":00");
                meeting.setBuilding(building.getSelectedItem().toString());
                meeting.setRoom(room.getText().toString());
                meeting.setWeekNum(Integer.parseInt(weekNo.getText().toString()));


                // TODO: 30/01/2020  Change this to be the group of the logged in user

                SyUser s = new SyUser();
                s.setSyid(1);
                s.setEmail("jeff@gmail.com");
                s.setPassword("jeff");
                s.setFirstname("Jeff");
                s.setSurname("Jefferson");
                s.setPhoneNum("055489364");
                s.setPva(0);

                Group g = new Group();
                g.setGid(2);
                g.setGroupLeader(s);

                meeting.setGid(g);

                Call<Void> call = jsonPlaceHolderApi.create(meeting);

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (!response.isSuccessful()) {
                            String result = "Code: " + response.code();
                            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                            return;
                        }
                        Toast.makeText(getApplicationContext(), "Meeting Added.\nGroup will be notified.", Toast.LENGTH_LONG).show();

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
        });
    }

}
