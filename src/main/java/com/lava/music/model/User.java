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
    private Integer taskNumber;
    private Integer submitNumber;
    private Integer doneNumber;
    private Integer auditNumber;
    private Integer auditTaskNumber;
    private Integer doneTaskNumber;
    private String fatherEmail;

    public Integer getDoneNumber() {
        return doneNumber;
    }

    public void setDoneNumber(Integer doneNumber) {
        this.doneNumber = doneNumber;
    }

    public String getFatherEmail() {
        return fatherEmail;
    }

    public void setFatherEmail(String fatherEmail) {
        this.fatherEmail = fatherEmail;
    }

    public Integer getAuditTaskNumber() {
        return auditTaskNumber;
    }

    public void setAuditTaskNumber(Integer auditTaskNumber) {
        this.auditTaskNumber = auditTaskNumber;
    }

    public Integer getDoneTaskNumber() {
        return doneTaskNumber;
    }

    public void setDoneTaskNumber(Integer doneTaskNumber) {
        this.doneTaskNumber = doneTaskNumber;
    }

    private String email;
    private String trueName;
    private Long fatherId;
    private String tmpPwd;

    public String getTmpPwd() {
        return tmpPwd;
    }

    public void setTmpPwd(String tmpPwd) {
        this.tmpPwd = tmpPwd;
    }

    private List<TagAuth> tagAuthList;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTrueName() {
        return trueName;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName;
    }

    public Long getFatherId() {
        return fatherId;
    }

    public void setFatherId(Long fatherId) {
        this.fatherId = fatherId;
    }

    public Integer getTaskNumber() {
        return taskNumber;
    }

    public void setTaskNumber(Integer taskNumber) {
        this.taskNumber = taskNumber;
    }

    public Integer getSubmitNumber() {
        return submitNumber;
    }

    public void setSubmitNumber(Integer submitNumber) {
        this.submitNumber = submitNumber;
    }

    public Integer getAuditNumber() {
        return auditNumber;
    }

    public void setAuditNumber(Integer auditNumber) {
        this.auditNumber = auditNumber;
    }

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
