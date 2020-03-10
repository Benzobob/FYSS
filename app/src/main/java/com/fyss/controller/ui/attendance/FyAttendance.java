package com.fyss.controller.ui.attendance;


import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.format.Time;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.fyss.R;
import com.fyss.controller.ui.attendance.adapter.SectionsPagerAdapter;
import com.fyss.controller.ui.dashboard.adapter.SectionsPagerAdapterFy;
import com.fyss.model.Attendance;
import com.fyss.model.FyUser;
import com.fyss.model.GroupMeeting;
import com.fyss.network.JsonPlaceHolderApi;
import com.fyss.network.RetrofitClientInstance;
import com.fyss.session.SessionManager;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class FyAttendance extends AppCompatActivity
        implements  NfcAdapter.CreateNdefMessageCallback,
        NfcAdapter.OnNdefPushCompleteCallback {

    private NfcAdapter mNfcAdapter;
    private Retrofit retrofit;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private static final int MESSAGE_SENT = 1;
    private GroupMeeting meeting;
    private SharedPreferences sharedPreferences;
    private SessionManager sm;
    private PendingIntent pendingIntent;
    IntentFilter [] mFilters;
    String[][] mTechLists;
    private String attendanceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);

        try {
            ndef.addDataType("*/*");
        }
        catch(IntentFilter.MalformedMimeTypeException e){
            throw new RuntimeException("fail", e);
        }

        mFilters = new IntentFilter [] {
                ndef,
        };

        mTechLists = new String [] [] {new String []  {NfcF.class.getName()}};

        sm = new SessionManager(getApplicationContext());
        retrofit = RetrofitClientInstance.getRetrofitInstance();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);


        HashMap<String, String> user = sm.getUserDetails();
        if (user.get(SessionManager.KEY_USER_TYPE) != null) {
            if (user.get(SessionManager.KEY_USER_TYPE).equals("FY")) {
                setContentView(R.layout.activity_attendance_fy);
                Toast.makeText(getApplicationContext(), " fy", Toast.LENGTH_LONG).show();

            } else {
                setContentView(R.layout.activity_attendance);

                if(mNfcAdapter != null){
                    mNfcAdapter.setNdefPushMessage(null, this);
                }
                SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

                ViewPager viewPager = findViewById(R.id.view_pager);
                viewPager.setAdapter(sectionsPagerAdapter);

                TabLayout tabs = findViewById(R.id.tabs);
                tabs.setupWithViewPager(viewPager);

                meeting = getMeeting();
            }
        }
        mNfcAdapter.setNdefPushMessageCallback(this, this);
    }

    private GroupMeeting getMeeting() {
        Gson gson = new Gson();
        sharedPreferences = getSharedPreferences("Meeting", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String jsonText = sharedPreferences.getString("meeting", null);
        GroupMeeting m = gson.fromJson(jsonText, GroupMeeting.class);
        return m;
    }



    /**
     * Implementation for the CreateNdefMessageCallback interface
     */
    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String type = "";
        String fyid = "";
        NdefMessage msg = new NdefMessage(NdefRecord.createMime(
                "text/plain", "SY".getBytes()));

        HashMap<String, String> user = sm.getUserDetails();
        if (user.get(SessionManager.KEY_USER_TYPE) != null) {
            type = user.get(SessionManager.KEY_USER_TYPE);
            fyid = user.get(SessionManager.KEY_USER_ID);
        }

        if (type.matches("FY")) {
            msg = new NdefMessage(NdefRecord.createMime(
                    "text/plain", fyid.getBytes()));
        }

        return msg;
    }

    /**
     * Implementation for the OnNdefPushCompleteCallback interface
     */
    @Override
    public void onNdefPushComplete(NfcEvent arg0) {
        // A handler is needed to send messages to the activity when this
        // callback occurs, because it happens from a binder thread
        mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
    }

    /**
     * This handler receives a message from onNdefPushComplete
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SENT:
                    Toast.makeText(getApplicationContext(), "Message sent!", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

}
