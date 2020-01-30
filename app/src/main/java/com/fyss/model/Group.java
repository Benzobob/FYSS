package com.fyss.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Group {

    @SerializedName("gid")
    @Expose
    private Integer gid;
    @SerializedName("groupLeader")
    @Expose
    private SyUser groupLeader;

    public Integer getGid() {
        return gid;
    }

    public void setGid(Integer gid) {
        this.gid = gid;
    }

    public SyUser getGroupLeader() {
        return groupLeader;
    }

    public void setGroupLeader(SyUser groupLeader) {
        this.groupLeader = groupLeader;
    }

}