package com.fyss.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Attendance {

    @SerializedName("aid")
    @Expose
    private Integer aid;
    @SerializedName("attend")
    @Expose
    private Integer attend;
    @SerializedName("fyid")
    @Expose
    private FyUser fyid;
    @SerializedName("gmid")
    @Expose
    private GroupMeeting gmid;

    public Integer getAid() {
        return aid;
    }

    public void setAid(Integer aid) {
        this.aid = aid;
    }

    public Integer getAttend() {
        return attend;
    }

    public void setAttend(Integer attend) {
        this.attend = attend;
    }

    public FyUser getFyid() {
        return fyid;
    }

    public void setFyid(FyUser fyid) {
        this.fyid = fyid;
    }

    public GroupMeeting getGmid() {
        return gmid;
    }

    public void setGmid(GroupMeeting gmid) {
        this.gmid = gmid;
    }

}