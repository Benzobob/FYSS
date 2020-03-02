package com.fyss.controller;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class WebViewClientImpl extends WebViewClient {
    private AppCompatActivity activity = null;

    public WebViewClientImpl(AppCompatActivity activity){
        this.activity = activity;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String URL){
      /*  if(URL.indexOf("https://www.studentvolunteer.ie/user-login/") > -1) return false;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
        activity.startActivity(intent);
        return true;*/
      return false;
    }
}
