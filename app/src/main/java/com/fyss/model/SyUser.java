package com.fyss.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SyUser {

    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("firstname")
    @Expose
    private String firstname;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("phoneNum")
    @Expose
    private String phoneNum;
    @SerializedName("pva")
    @Expose
    private Integer pva;
    @SerializedName("surname")
    @Expose
    private String surname;
    @SerializedName("syid")
    @Expose
    private Integer syid;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public Integer getPva() {
        return pva;
    }

    public void setPva(Integer pva) {
        this.pva = pva;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Integer getSyid() {
        return syid;
    }

    public void setSyid(Integer syid) {
        this.syid = syid;
    }

}
