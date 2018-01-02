package com.lava.music.model;

import java.util.Date;

/**
 * Created by mac on 2017/8/30.
 */
public class UserRecord {

    public static final Integer LOGIN = 1;
    public static final Integer LOGIN_OUT = 2;
    public static final Integer DELETE_USER = 3;
    public static final Integer ACTIVE_USER = 4;
    public static final Integer ADD_USER = 5;
    public static final Integer CHANGE_PWD = 6;

    public static final Integer SONG_ADD_LABEL = 7;
    public static final Integer SONG_AUDIT_LABEL = 8;
    public static final Integer SONG_CHECK_LABEL = 9;
    public static final Integer SONG_DEL_LABEL = 10;
    public static final Integer SUBMIT = 11;
    public static final Integer AUDIT = 12;

    public static final Integer ADD_LABEL = 13;
    public static final Integer MOVE_LABEL = 14;
    public static final Integer DELETE_LABEL = 15;
    public static final Integer EDIT_Label = 16;


    private Long id;
    private Long userId;
    private Integer action;
    private Date createTime;
    private String sourceData;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getSourceData() {
        return sourceData;
    }

    public void setSourceData(String sourceData) {
        this.sourceData = sourceData;
    }

}
