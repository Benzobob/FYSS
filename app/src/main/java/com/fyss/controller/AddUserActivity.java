package com.fyss.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fyss.R;
import com.fyss.model.FyUser;
import com.fyss.model.SyUser;
import com.fyss.network.JsonPlaceHolderApi;
import com.fyss.network.RetrofitClientInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddUserActivity extends AppCompatActivity {

    private TextView info, tv;
    private EditText firstname, surname, email, password, phoneNum;
    private Button createBtn;
    private CheckBox cb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firstname = findViewById(R.id.name);
        surname = findViewById(R.id.surname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        phoneNum = findViewById(R.id.phoneNum);
        createBtn = findViewById(R.id.buttonSignUp);
        info = findViewById(R.id.info);
        cb = findViewById(R.id.checkBox);
        tv = findViewById(R.id.tv1);

        Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        final JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (checkInput() == 0) {
                    Call<Void> call;

                    if(cb.isChecked()){
                        SyUser newUser = new SyUser();
                        newUser.setEmail(email.getText().toString());
                        newUser.setFirstname(firstname.getText().toString());
                        newUser.setSurname(surname.getText().toString());
                        newUser.setPassword(password.getText().toString());
                        newUser.setPhoneNum(phoneNum.getText().toString());
                        call = jsonPlaceHolderApi.create(newUser);
                    }
                    else{
                        FyUser newUser = new FyUser();
                        newUser.setEmail(email.getText().toString());
                        newUser.setFirstname(firstname.getText().toString());
                        newUser.setSurname(surname.getText().toString());
                        newUser.setPassword(password.getText().toString());
                        newUser.setPhoneNum(phoneNum.getText().toString());
                        call = jsonPlaceHolderApi.create(newUser);
                    }


                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (!response.isSuccessful()) {
                                String result = "Code: " + response.code();
                                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                                return;
                            }
                            Toast.makeText(getApplicationContext(), "User Added", Toast.LENGTH_LONG).show();

                            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(i);
                            finish();
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else if (checkInput() == 1) {
                    info.setText(getString(R.string.reg_warn_1));
                    info.setTextColor(getResources().getColor(R.color.colorRed));
                } else if (checkInput() == 2) {
                    info.setText("Password must contain at least \n1 upper case character, 1 lower case character, \n1 digit and must be at least 8 characters.");
                    info.setTextColor(getResources().getColor(R.color.colorRed));
                }
            }
        });
    }

    /*
     * Checks if all text boxes have user input
     * */
    public int checkInput() {
        //String pattern = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]){8,40}";
        String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}";
        int flag = 0;

        if (!(password.getText().toString().matches(pattern)))
            flag = 2;

        if (firstname.getText().toString().matches(""))
            flag = 1;
        else if (surname.getText().toString().matches(""))
            flag = 1;
        else if (email.getText().toString().matches(""))
            flag = 1;
        else if (phoneNum.getText().toString().matches(""))
            flag = 1;
        else if (password.getText().toString().matches("")) {
            flag = 1;
        }

        return flag;
    }


}
