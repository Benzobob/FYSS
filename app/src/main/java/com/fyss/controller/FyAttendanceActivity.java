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
import com.fyss.session.SessionManager;

public class FyAttendanceActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback{

    private SessionManager sm;
    private ProgressBar pb;
    private ImageButton backBtn;

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

        NfcAdapter mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
            Toast.makeText(this, "This device does not have nfc", Toast.LENGTH_LONG).show();
            return;
        }

        if (!mAdapter.isEnabled()) {
            Toast.makeText(this, "Please enable NFC via Settings.", Toast.LENGTH_LONG).show();
        }

        mAdapter.setNdefPushMessageCallback(this, this);
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent nfcEvent) {
       //Create a string with id number of logged in user and send it
        String id = "1";
      //  HashMap<String, String> user = sm.getUserDetails();
      //  if (user.get(SessionManager.KEY_USER_ID) != null) {
      //      id = user.get(SessionManager.KEY_USER_ID);
       // }

        assert id != null;
        NdefRecord ndefRecord = NdefRecord.createMime("text/plain", id.getBytes());
        NdefMessage ndefMessage = new NdefMessage(ndefRecord);
        pb.setVisibility(View.GONE);
        return ndefMessage;
    }
}
