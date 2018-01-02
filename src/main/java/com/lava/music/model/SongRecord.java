package com.lava.music.model;

import java.util.Date;

/**
 * Created by Leon on 2017/12/24.
 */
public class SongRecord {

    public static final Integer PUSH_SONG = 1;
    public static final Integer PULL_SONG = 2;
    public static final Integer SUBMIT_SONG = 3;
    public static final Integer AUDIT_SONG = 4;

    private Long id;
    private Long songId;
    private Integer action;
    private Date createTime;
    private Long userId;
    private String metaData;

    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSongId() {
        return songId;
    }

    public void setSongId(Long songId) {
        this.songId = songId;
    }

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
