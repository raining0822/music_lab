package com.lava.music.model;

import java.util.Date;

/**
 * Created by mac on 2017/8/23.
 */
public class Song {

    public static final Integer WAIT_PUSH = 0;
    public static final Integer PUSHED = 1;
    public static final Integer PULLED = 2;
    public static final Integer SUBMITED = 3;
    public static final Integer SUBMITPULLED = 5;
    public static final Integer AUDITED = 4;
    public static final Integer BACK = 6;




    private Long id;
    private String songId;
    private String songName;
    private String language;
    private String artistName;
    private String albumName;
    private String realAudioUrl;
    private String picUrl;
    private Integer effect;
    private Long taskUserId;
    private Long auditUserId;
    private String auditResult;
    private Date taskTime;
    private Date submitTime;
    private String userTrueName;

    public String getUserTrueName() {
        return userTrueName;
    }

    public void setUserTrueName(String userTrueName) {
        this.userTrueName = userTrueName;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public Long getTaskUserId() {
        return taskUserId;
    }

    public void setTaskUserId(Long taskUserId) {
        this.taskUserId = taskUserId;
    }

    public Long getAuditUserId() {
        return auditUserId;
    }

    public void setAuditUserId(Long auditUserId) {
        this.auditUserId = auditUserId;
    }

    public String getAuditResult() {
        return auditResult;
    }

    public void setAuditResult(String auditResult) {
        this.auditResult = auditResult;
    }

    public Date getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(Date taskTime) {
        this.taskTime = taskTime;
    }

    private String tsId;
    private String albumPic;
    //基础标签
    private Integer basicTag;
    //理性标签
    private Integer reasonTag;
    //感性标签
    private Integer sensibilityTag;

    private Integer songStatus;


    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public Integer getBasicTag() {
        return basicTag;
    }

    public void setBasicTag(Integer basicTag) {
        this.basicTag = basicTag;
    }

    public Integer getReasonTag() {
        return reasonTag;
    }

    public void setReasonTag(Integer reasonTag) {
        this.reasonTag = reasonTag;
    }

    public Integer getSensibilityTag() {
        return sensibilityTag;
    }

    public void setSensibilityTag(Integer sensibilityTag) {
        this.sensibilityTag = sensibilityTag;
    }

    public Integer getSongStatus() {
        return songStatus;
    }

    public void setSongStatus(Integer songStatus) {
        this.songStatus = songStatus;
    }

    public String getAlbumPic() {
        return albumPic;
    }

    public void setAlbumPic(String albumPic) {
        this.albumPic = albumPic;
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

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }


    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getRealAudioUrl() {
        return realAudioUrl;
    }

    public void setRealAudioUrl(String realAudioUrl) {
        this.realAudioUrl = realAudioUrl;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getTsId() {
        return tsId;
    }

    public void setTsId(String tsId) {
        this.tsId = tsId;
    }
}
