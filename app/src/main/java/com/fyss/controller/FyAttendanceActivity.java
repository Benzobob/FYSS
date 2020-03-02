package com.fyss.controller;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fyss.R;
import com.fyss.controller.nfc.OutcomingNfcManager;
import com.fyss.session.SessionManager;

import java.util.HashMap;

public class FyAttendanceActivity extends AppCompatActivity implements OutcomingNfcManager.NfcActivity{

    private SessionManager sm;
    private ProgressBar pb;
    private ImageButton backBtn;
    private NfcAdapter mAdapter;
    private OutcomingNfcManager outcomingNfccallback;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_fy);

        sm = new SessionManager(getApplicationContext());
        pb = findViewById(R.id.progressBar);
        backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FyDashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
            Toast.makeText(this, "This device does not have nfc", Toast.LENGTH_LONG).show();
            return;
        }

        if (!mAdapter.isEnabled()) {
            Toast.makeText(this, "Please enable NFC via Settings.", Toast.LENGTH_LONG).show();
        }

        // encapsulate sending logic in a separate class
        this.outcomingNfccallback = new OutcomingNfcManager(this);
        this.mAdapter.setOnNdefPushCompleteCallback(outcomingNfccallback, this);
        this.mAdapter.setNdefPushMessageCallback(outcomingNfccallback, this);

       // mAdapter.setNdefPushMessageCallback(this, this);
    }

   /* @Override
    public NdefMessage createNdefMessage(NfcEvent nfcEvent) {
       //Create a string with id number of logged in user and send it
        String id = "1";
        HashMap<String, String> user = sm.getUserDetails();
        if (user.get(SessionManager.KEY_USER_ID) != null) {
            id = user.get(SessionManager.KEY_USER_ID);
       }

        assert id != null;
        NdefRecord ndefRecord = NdefRecord.createMime("text/plain", id.getBytes());
        NdefMessage ndefMessage = new NdefMessage(ndefRecord);
        pb.setVisibility(View.GONE);
        return ndefMessage;
    }*/

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    private boolean isNfcSupported() {
        this.mAdapter = NfcAdapter.getDefaultAdapter(this);
        return this.mAdapter != null;
    }

    @Override
    public String getOutcomingMessage() {
        HashMap<String, String> user = sm.getUserDetails();
        if (user.get(SessionManager.KEY_USER_ID) != null) {
            id = user.get(SessionManager.KEY_USER_ID);
        }
        return "3";
    }

    @Override
    public void signalResult() {
        // this will be triggered when NFC message is sent to a device.
        // should be triggered on UI thread. We specify it explicitly
        // cause onNdefPushComplete is called from the Binder thread

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FyAttendanceActivity.this, id, Toast.LENGTH_SHORT).show();
            }
        });
    }
}