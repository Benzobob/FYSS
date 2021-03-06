package com.fyss.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class FyUser implements Serializable {

    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("firstname")
    @Expose
    private String firstname;
    @SerializedName("fyid")
    @Expose
    private Integer fyid;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("phoneNum")
    @Expose
    private String phoneNum;
    @SerializedName("surname")
    @Expose
    private String surname;
    @SerializedName("gid")
    @Expose
    private Group gid;
    @SerializedName("profileImg")
    @Expose
    private String profileImg;

    private boolean isSelected;

    public Group getGid(){
        return this.gid;
    }

    public void setGid(Group gid){
        this.gid = gid;
    }

    public boolean isSelected(){
        return isSelected;
    }

    public void setSelected(boolean selected){
        this.isSelected = selected;
    }

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

    public Integer getFyid() {
        return fyid;
    }

    public void setFyid(Integer fyid) {
        this.fyid = fyid;
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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

}