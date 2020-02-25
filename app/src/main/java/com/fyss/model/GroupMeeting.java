package com.fyss.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupMeeting implements Serializable {

    @SerializedName("GMDate")
    @Expose
    private String gMDate;
    @SerializedName("building")
    @Expose
    private String building;
    @SerializedName("gid")
    @Expose
    private Group gid;
    @SerializedName("gmid")
    @Expose
    private Integer gmid;
    @SerializedName("room")
    @Expose
    private String room;
    @SerializedName("topic")
    @Expose
    private String topic;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("weekNum")
    @Expose
    private Integer weekNum;

    public String getGMDate() {
        return gMDate;
    }

    public void setGMDate(String gMDate) {
        this.gMDate = gMDate;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public Group getGid() {
        return gid;
    }

    public void setGid(Group gid) {
        this.gid = gid;
    }

    public Integer getGmid() {
        return gmid;
    }

    public void setGmid(Integer gmid) {
        this.gmid = gmid;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getWeekNum() {
        return weekNum;
    }

    public void setWeekNum(Integer weekNum) {
        this.weekNum = weekNum;
    }

}
