package com.fyss.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Posts {

    @SerializedName("body")
    @Expose
    private String body;
    @SerializedName("gid")
    @Expose
    private Group gid;
    @SerializedName("pid")
    @Expose
    private Integer pid;
    @SerializedName("syid")
    @Expose
    private SyUser syid;
    @SerializedName("title")
    @Expose
    private String title;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Group getGid() {
        return gid;
    }

    public void setGid(Group gid) {
        this.gid = gid;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public SyUser getSyid() {
        return syid;
    }

    public void setSyid(SyUser syid) {
        this.syid = syid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

