package com.fyss.controller;


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
    private SharedPreferences sharedPreferences;
    private SessionManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        sm = new SessionManager(getApplicationContext());
        retrofit = RetrofitClientInstance.getRetrofitInstance();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        setContentView(R.layout.activity_attendance_fy);
        mNfcAdapter.setNdefPushMessageCallback(this, this);
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
