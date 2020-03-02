package com.fyss.controller;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fyss.R;
import com.fyss.controller.adapter.MembersAttendanceAdapter;
import com.fyss.controller.ui.dashboard.adapter.MembersAdapter;
import com.fyss.model.Attendance;
import com.fyss.model.FyUser;
import com.fyss.model.GroupMeeting;
import com.fyss.network.JsonPlaceHolderApi;
import com.fyss.network.RetrofitClientInstance;
import com.fyss.session.SessionManager;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.io.File;
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

import static android.nfc.NdefRecord.createMime;

public class SyAttendanceActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    public static final String MIME_TEXT_PLAIN = "text/plain";
    private ArrayList<FyUser> membersList;
    private RecyclerView recyclerView;
    private MembersAttendanceAdapter mAdapter;
    private Retrofit retrofit;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private MembersAttendanceAdapter.RecyclerViewClickListener listener;
    private GroupMeeting meeting;
    private SessionManager sm;
    private FloatingActionButton submitBtn;
    private String FyId;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_sy);
        meeting = (GroupMeeting) getIntent().getSerializableExtra("meeting");
        sm = new SessionManager(getApplicationContext());
        retrofit = RetrofitClientInstance.getRetrofitInstance();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView2);
        submitBtn = findViewById(R.id.floatingActionButton);
        getGroupId();


        if (!isNfcSupported()) {
            Toast.makeText(this, "Nfc is not supported on this device", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC disabled on this device. Turn on to proceed", Toast.LENGTH_SHORT).show();
        }

        submitBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (membersList.get(0).isSelected()) {
                    Toast.makeText(SyAttendanceActivity.this, "selected", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean isNfcSupported() {
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        return this.nfcAdapter != null;
    }


    private void getGroupId() {
        int id = 0;
        HashMap<String, String> user = sm.getUserDetails();
        if (user.get(SessionManager.KEY_USER_ID) != null) {
            id = Integer.parseInt(user.get(SessionManager.KEY_USER_ID));
        }
        Call<ResponseBody> call = jsonPlaceHolderApi.getGroupId(id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    String result = "Code: " + response.code();
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                    return;
                } else {
                    String gid = null;
                    try {
                        gid = response.body().string();
                        prepareMembersData(Integer.parseInt(gid));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void prepareMembersData(int gid) {
        //get user data from session
        Call<List<FyUser>> call = jsonPlaceHolderApi.getGroupMembers(gid);

        call.enqueue(new Callback<List<FyUser>>() {
            @Override
            public void onResponse(Call<List<FyUser>> call, Response<List<FyUser>> response) {
                if (!response.isSuccessful()) {
                    String result = "Code: " + response.code();
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                    return;
                }

                membersList = new ArrayList<>(response.body());
                mAdapter = new MembersAttendanceAdapter(membersList, listener);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(mAdapter);

                saveMembersList(membersList);
            }

            @Override
            public void onFailure(Call<List<FyUser>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveMembersList(ArrayList<FyUser> membersList) {
        Gson gson = new Gson();
        String jsonText = gson.toJson(membersList);
        String jsonText1 = gson.toJson(meeting);
        sharedPreferences = getSharedPreferences("Members", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("membersList", jsonText);
        editor.putString("meeting", jsonText1);
        editor.apply();
    }

    private FyUser[] getMembersList() {
        Gson gson = new Gson();
        sharedPreferences = getSharedPreferences("Members", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String jsonText = sharedPreferences.getString("membersList", null);
        FyUser[] members = gson.fromJson(jsonText, FyUser[].class);
        return members;
    }

    private GroupMeeting getMeeting(){
        Gson gson = new Gson();
        sharedPreferences = getSharedPreferences("Members", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String jsonText = sharedPreferences.getString("meeting", null);
        GroupMeeting m = gson.fromJson(jsonText, GroupMeeting.class);
        return m;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    void processIntent(Intent intent) {
        String action = intent.getAction();
        Parcelable[] ndefMessageArray;
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            ndefMessageArray = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage message = (NdefMessage) ndefMessageArray[0];
            FyId = new String(message.getRecords()[0].getPayload());
            updateAttendance(FyId);
            Toast.makeText(this, FyId, Toast.LENGTH_LONG).show();
        }
    }

    private void updateAttendance(String FyId){
        FyUser[] mems = getMembersList();
        FyUser user = null;
        membersList = new ArrayList<FyUser>(Arrays.asList(mems));
        for (int i = 0; i < membersList.size(); i++) {
            if (FyId.matches(membersList.get(i).getFyid().toString())) {
                user = membersList.get(i);
            }
        }
        Attendance a = new Attendance();
        a.setAttend(1);
        a.setFyid(user);
        a.setGmid(getMeeting());

        Call<Void> call = jsonPlaceHolderApi.create(a);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    String result = "Cde: " + response.code();
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(getApplicationContext(), "Attendance Noted", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

}

    /* public void tickBoxForUser(String FyId){
        FyUser[] mems = getMembersList();
        membersList = new ArrayList<FyUser>(Arrays.asList(mems));
       // Toast.makeText(this, mems[1].getFyid() + " " + FyId, Toast.LENGTH_LONG).show();
        for (int i = 0; i < membersList.size(); i++) {
            if (FyId.matches(membersList.get(i).getFyid().toString())) {
                // if (!mems[i].isSelected()) {
                //mems[i].setSelected(true);
                membersList.remove(i);
            }
        }
       // membersList = new ArrayList<FyUser>(Arrays.asList(mems));

        mAdapter = new MembersAttendanceAdapter(membersList, listener);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        saveMembersList(membersList);

    }

}











    /*

    @Override
    protected void onResume() {
        super.onResume();
        // foreground dispatch should be enabled here, as onResume is the guaranteed place where app
        // is in the foreground
        //enableForegroundDispatch(this, this.nfcAdapter);
        receiveMessageFromDevice(getIntent());

    }

    @Override
    protected void onPause() {
        super.onPause();
        disableForegroundDispatch(this, this.nfcAdapter);
    }

    private void receiveMessageFromDevice(Intent intent) throws NullPointerException{
        String action = intent.getAction();
        Parcelable[] ndefMessageArray;
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            ndefMessageArray = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                NdefMessage message = (NdefMessage) ndefMessageArray[0];
                FyId = new String(message.getRecords()[0].getPayload());
                tickBoxForUser(FyId, membersList);
                Toast.makeText(this, FyId, Toast.LENGTH_LONG).show();
            getGroupId();

        }
    }

    public void tickBoxForUser(String FyId, ArrayList<FyUser> m){
       // if(membersList != null) {
            for (int i = 0; i < m.size(); i++) {
                if (FyId.matches(m.get(i).getFyid().toString())) {
                    if (!m.get(i).isSelected()) {
                        m.get(i).setSelected(true);
                    }
                }
            }
        //}
    }

    // Foreground dispatch holds the highest priority for capturing NFC intents
    // then go activities with these intent filters:
    // 1) ACTION_NDEF_DISCOVERED
    // 2) ACTION_TECH_DISCOVERED
    // 3) ACTION_TAG_DISCOVERED

    // always try to match the one with the highest priority, cause ACTION_TAG_DISCOVERED is the most
    // general case and might be intercepted by some other apps installed on your device as well

    // When several apps can match the same intent Android OS will bring up an app chooser dialog
    // which is undesirable, because user will most likely have to move his device from the tag or another
    // NFC device thus breaking a connection, as it's a short range

    public void enableForegroundDispatch(AppCompatActivity activity, NfcAdapter adapter) {

        // here we are setting up receiving activity for a foreground dispatch
        // thus if activity is already started it will take precedence over any other activity or app
        // with the same intent filters


        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //
        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException ex) {
            throw new RuntimeException("Check your MIME type");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    public void disableForegroundDispatch(final AppCompatActivity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }
}


*/