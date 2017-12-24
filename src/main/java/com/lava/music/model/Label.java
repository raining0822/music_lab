package com.lava.music.model;

import java.util.Date;
import java.util.List;

/**
 * Created by mac on 2017/8/23.
 */
public class Label {

    private Long id;
    private String labelName;
    private Long fatherId;
    private Integer sonNum;
    private Integer effect;
    private Integer labelLevel;
    private Date createTime;
    private String labelNo;
    private List<Label> sonLabels;


    public String getLabelNo() {
        return labelNo;
    }

    public void setLabelNo(String labelNo) {
        this.labelNo = labelNo;
    }

    public List<Label> getSonLabels() {
        return sonLabels;
    }

    public void setSonLabels(List<Label> sonLabels) {
        this.sonLabels = sonLabels;
    }

    public Integer getSonNum() {
        return sonNum;
    }

    public void setSonNum(Integer sonNum) {
        this.sonNum = sonNum;
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

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public Long getFatherId() {
        return fatherId;
    }

    public void setFatherId(Long fatherId) {
        this.fatherId = fatherId;
    }

    public Integer getLabelLevel() {
        return labelLevel;
    }

    public void setLabelLevel(Integer labelLevel) {
        this.labelLevel = labelLevel;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
