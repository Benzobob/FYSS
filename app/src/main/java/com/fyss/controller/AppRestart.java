package com.fyss.controller;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class AppRestart extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.exit(0);
    }
    public static void doRestart(AppCompatActivity anyActivity) {
        anyActivity.startActivity(new Intent(anyActivity.getApplicationContext(), AppRestart.class));
    }
}
