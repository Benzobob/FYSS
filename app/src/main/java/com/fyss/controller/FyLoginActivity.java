package com.fyss.controller;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fyss.session.AlertDialogManager;
import com.fyss.network.JsonPlaceHolderApi;
import com.fyss.R;
import com.fyss.network.RetrofitClientInstance;
import com.fyss.session.SessionManager;

import retrofit2.Retrofit;

public class FyLoginActivity extends AppCompatActivity {

    EditText emailTxt, passwordTxt;
    TextView tv;
    Button btnLogin;
    AlertDialogManager alert = new AlertDialogManager();
    SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session     = new SessionManager(getApplicationContext());
        emailTxt    = findViewById(R.id.email);
        passwordTxt = findViewById(R.id.password);
        tv          = findViewById(R.id.link_signup);
        btnLogin    = findViewById(R.id.button);

        Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

            }
        });
    }
}







            /*
                // Get username, password from EditText
                String username = emailTxt.getText().toString();
                String password = passwordTxt.getText().toString();

                // Check if username, password is filled
                if (username.trim().length() > 0 && password.trim().length() > 0) {
                    Call<ResponseBody> call = jsonPlaceHolderApi.login(username, password);

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (!response.isSuccessful()) {
                                String result = "Code: " + response.code();
                                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                                return;
                            }

                            ResponseBody accs = response.body();
                            tv.setText(accs.toString());
                        }


                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            tv.setText(t.getMessage());
                        }
                    });
                }
            }
        });
    }
}



                        // Creating user login session
                        // For testing i am stroing name, email as follow
                        // Use user real data
                        session.createLoginSession("Android Hive", "anroidhive@gmail.com");

                        // Staring MainActivity
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                        finish();

                    }else{
                        // username / password doesn't match
                        alert.showAlertDialog(FyLoginActivity.this, "Login failed..", "Username/Password is incorrect", false);
                    }
                }else{
                    // user didn't entered username or password
                    // Show alert asking him to enter the details
                    alert.showAlertDialog(FyLoginActivity.this, "Login failed..", "Please enter username and password", false);
                }

            }
        }); */
