package com.fyss.controller;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import static com.fyss.service.Const.PREFS_NAME;
import static com.fyss.service.Const.PREF_DARK_THEME;
import com.fyss.R;
import com.fyss.controller.adapter.MembersAttendanceAdapter;
import com.fyss.model.Attendance;
import com.fyss.model.FyUser;
import com.fyss.model.GroupMeeting;
import com.fyss.network.JsonPlaceHolderApi;
import com.fyss.network.RetrofitClientInstance;
import com.fyss.session.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SyAttendance extends AppCompatActivity {

    private NfcAdapter mNfcAdapter;
    private NfcManager mNfcManager;
    private Retrofit retrofit;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private static final int MESSAGE_SENT = 1;
    private SharedPreferences sharedPreferences;
    private SessionManager sm;
    private MembersAttendanceAdapter.RecyclerViewClickListener listener;
    private ArrayList<FyUser> membersList;
    private RecyclerView recyclerView;
    private MembersAttendanceAdapter mAdapter;
    private PendingIntent pendingIntent;
    private boolean flag;
    private TextView info, search;
    private ProgressBar progBar;
    private ImageButton backBtn;
    IntentFilter[] mFilters;
    String[][] mTechLists;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);

        if(useDarkTheme) {
            setTheme(R.style.AppTheme_DarkTheme_NoActionBar);
        }else{
            setTheme(R.style.AppTheme_LightTheme_NoActionBar);
        }
        setContentView(R.layout.activity_attendance_sy);
        sm = new SessionManager(getApplicationContext());
        retrofit = RetrofitClientInstance.getRetrofitInstance();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        mNfcManager = (NfcManager) getApplicationContext().getSystemService(Context.NFC_SERVICE);
        mNfcAdapter = mNfcManager.getDefaultAdapter();
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        recyclerView = findViewById(R.id.recyclerView2);
        info = findViewById(R.id.infoTextBox);
        progBar = findViewById(R.id.progBar);
        search = findViewById(R.id.searchText);
        backBtn = findViewById(R.id.backBtn);


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

        if(mNfcAdapter != null && mNfcAdapter.isEnabled()){
            mNfcAdapter.setNdefPushMessage(null, this);
            search.setVisibility(View.VISIBLE);
            progBar.setVisibility(View.VISIBLE);
            info.setText("Use NFC or manually mark attendance.");
        }
        else{
            info.setText("Click on switch to mark attendance.");
        }

        prepareMembersData();
    }

    private void setOnClickListeners() {
        listener = new MembersAttendanceAdapter.RecyclerViewClickListener(){
            @Override
            public void onClick(View v, int position) {
                checkDuplicate(membersList.get(position).getFyid().toString());
            }
        };

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private GroupMeeting getMeeting() {
        Gson gson = new Gson();
        sharedPreferences = getSharedPreferences("Meeting", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String jsonText = sharedPreferences.getString("meeting", null);
        GroupMeeting m = gson.fromJson(jsonText, GroupMeeting.class);
        return m;
    }


    @Override
    public void onPause(){
        super.onPause();
        mNfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mNfcAdapter.enableForegroundDispatch(this, pendingIntent, mFilters, mTechLists);
    }

    @Override
    public void onNewIntent(Intent intent) {
        //super.onNewIntent(intent);
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage msg = (NdefMessage) rawMsgs[0];

        //This is the message sent from the other device
        String msg1 = new String(msg.getRecords()[0].getPayload());

        //If its a first year, then update attendance
        if (!msg1.matches("SY")) {
            checkDuplicate(msg1);
        }
    }

    private boolean checkDuplicate(final String id){

        GroupMeeting g = getMeeting();
        Call<String> call = jsonPlaceHolderApi.checkDuplicate(Integer.parseInt(id), g.getGmid());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (!response.isSuccessful()) {
                    String result = "Code: " + response.code();
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                    return;
                }
                if(response.body().matches("Duplicate")){
                    flag = false;
                    Toast.makeText(getApplicationContext(), "Attendance has already been noted.", Toast.LENGTH_LONG).show();
                }else{
                    flag = true;
                    String aid = response.body();
                    getAttendance(aid);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getApplicationContext(),  t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return flag;
    }

    private void getAttendance(final String aid) {
        Call<Attendance> call = jsonPlaceHolderApi.getAttendance(Integer.parseInt(aid));


        call.enqueue(new Callback<Attendance>() {
            @Override
            public void onResponse(Call<Attendance> call, Response<Attendance> response) {
                if (!response.isSuccessful()) {
                    String result = "Code: " + response.code();
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                    return;
                }

                updateAttendance(response.body());

            }

            @Override
            public void onFailure(Call<Attendance> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"2" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateAttendance(Attendance a) {
        a.setAttend(1);

        Call<Void> call = jsonPlaceHolderApi.editAttendance(a.getAid().intValue(), a);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    String result = "Code: " + response.code();
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                    return;
                }
                Snackbar.make(findViewById(android.R.id.content),"Attendance Noted", Snackbar.LENGTH_LONG).show();
                prepareMembersData();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    /**
     * This method calls the API to get an ArrayList of students who have not signed in yet.
     * It then updates the adapter with the new ArrayList, and updates the UI.
     * */
    private void prepareMembersData() {
        GroupMeeting g = getMeeting();
        Call<List<FyUser>> call = jsonPlaceHolderApi.getRemainingStudents(g.getGmid());

        call.enqueue(new Callback<List<FyUser>>() {
            @Override
            public void onResponse(Call<List<FyUser>> call, Response<List<FyUser>> response) {
                if (!response.isSuccessful()) {
                    String result = "Code: " + response.code();
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                    return;
                }
                setOnClickListeners();
                membersList = new ArrayList<>(response.body());

                    mAdapter = new MembersAttendanceAdapter(membersList, listener);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(mAdapter);


                if(membersList.size() == 0){
                    search.setVisibility(View.GONE);
                    progBar.setVisibility(View.GONE);
                    info.setText("Attendance taken.");
                }


            }

            @Override
            public void onFailure(Call<List<FyUser>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

