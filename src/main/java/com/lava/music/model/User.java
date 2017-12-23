package com.lava.music.model;

import java.util.Date;
import java.util.List;

/**
 * Created by mac on 2017/8/23.
 */
public class User {

    private Long id;
    private String userName;
    private String userPwd;
    private Integer effect;
    private Date createTime;
    private Date lastLoginTime;
    private Integer userType;
    private String userTypeName;
    private List<TagAuth> tagAuthList;

    public List<TagAuth> getTagAuthList() {
        return tagAuthList;
    }

    public void setTagAuthList(List<TagAuth> tagAuthList) {
        this.tagAuthList = tagAuthList;
    }

    public String getUserTypeName() {
        return userTypeName;
    }

    public void setUserTypeName(String userTypeName) {
        this.userTypeName = userTypeName;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getEffect() {
        return effect;
    }

    public void setEffect(Integer effect) {
        this.effect = effect;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

}
