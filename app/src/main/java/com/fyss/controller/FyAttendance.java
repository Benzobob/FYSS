package com.fyss.controller;


import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.fyss.R;
import com.fyss.network.JsonPlaceHolderApi;
import com.fyss.network.RetrofitClientInstance;
import com.fyss.session.SessionManager;
import java.util.HashMap;
import retrofit2.Retrofit;
import static com.fyss.service.Const.PREFS_NAME;
import static com.fyss.service.Const.PREF_DARK_THEME;

public class FyAttendance extends AppCompatActivity
        implements  NfcAdapter.CreateNdefMessageCallback,
        NfcAdapter.OnNdefPushCompleteCallback {

    private NfcAdapter mNfcAdapter;
    private Retrofit retrofit;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private static final int MESSAGE_SENT = 1;
    private SharedPreferences sharedPreferences;
    private SessionManager sm;
    private ImageButton backBtn;

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
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        sm = new SessionManager(getApplicationContext());
        retrofit = RetrofitClientInstance.getRetrofitInstance();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        setContentView(R.layout.activity_attendance_fy);
        mNfcAdapter.setNdefPushMessageCallback(this, this);
        backBtn = findViewById(R.id.backBtn);

        setOnClickListeners();
    }

    private void setOnClickListeners() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
