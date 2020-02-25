package com.fyss.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FCM_Token implements Serializable {

    @SerializedName("emailFy")
    @Expose
    private FyUser emailFy;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("emailSy")
    @Expose
    private SyUser emailSy;

    public FyUser getEmailFy() {
        return emailFy;
    }

    public void setEmailFy(FyUser emailFy) {
        this.emailFy = emailFy;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public SyUser getEmailSy() {
        return emailSy;
    }

    public void setEmailSy(SyUser emailSy) {
        this.emailSy = emailSy;
    }

}