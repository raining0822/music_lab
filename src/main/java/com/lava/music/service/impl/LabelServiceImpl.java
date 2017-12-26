package com.lava.music.service.impl;

import com.lava.music.dao.LabelDao;
import com.lava.music.model.Label;
import com.lava.music.service.BaseService;
import com.lava.music.service.LabelService;
import com.lava.music.util.LabelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by mac on 2017/8/23.
 */
@Service
public class LabelServiceImpl extends BaseService implements LabelService {

    @Autowired
    private LabelDao labelDao;

    @Override
    public Long addLabel(String labelName, Label fatherLabel) {
        Label label = new Label();
        label.setLabelName(labelName);
        label.setLabelLevel(fatherLabel.getLabelLevel() + 1);
        label.setEffect(1);
        label.setSonNum(0);
        label.setCreateTime(new Date());
        label.setFatherId(fatherLabel.getId());
        label.setLabelNo(LabelUtil.getLabelNo(fatherLabel, labelDao.findByFatherId(fatherLabel.getId())));
        return labelDao.insert(label);
    }

    public List<Label> findDimension(){
        return labelDao.findByLevel(1);
    }

    public List<Label> findLabel(Long fatherId){
        return labelDao.findByFatherId(fatherId);
    }

    @Override
    public Integer moveLabel(Long labelId, Long fatherId) {
        Label label = labelDao.selectById(labelId);
        Label fatherLabel = labelDao.selectById(fatherId);
        if(label != null && fatherLabel != null){
            label.setLabelLevel(fatherLabel.getLabelLevel() + 1);
            labelDao.update(label);
            labelDao.updateFatherId(fatherId, labelId);
        }
        return null;
    }

    @Override
    public Integer delLabel(Long id) {
        return labelDao.delById(id);
    }

    @Override
    public List<Label> findLabelBySongId(String songId) {
        return labelDao.selectLabelBySongId(songId);
    }

    @Override
    public Label findById(Long id) {
        return labelDao.selectEffectById(id);
    }

    @Override
    public List<Label> findLabels(String labelIds) {
        return labelDao.selectByIds(labelIds);
    }

    @Override
    public List<Label> findLabel(Label label) {
        return labelDao.findByFatherId(label.getId());
    }

    @Override
    public void updateLabel(Long labelId, String labelName) {
        Label label = new Label();
        label.setId(labelId);
        label.setLabelName(labelName);
        labelDao.update(label);
    }

    @Override
    public void initLabelNo() {
        Label label = labelDao.selectRootLabel();
        label.setLabelLevel(-1);
        label.setLabelNo("000");
        labelDao.updateLabelNo(label);
        handlerLabelNo(label);
    }

    private void handlerLabelNo(Label label){
        String labelNo = label.getLabelNo();
        List<Label> sonList = labelDao.findByFatherId(label.getId());
        if(sonList != null && sonList.size() > 0){
            int i = 1;
            for(Label lab : sonList){
                lab.setLabelLevel(label.getLabelLevel() + 1);
                String no = LabelUtil.addZero(i, 3);
                lab.setLabelNo(labelNo + no);
                labelDao.updateLabelNo(lab);
                i ++;
                handlerLabelNo(lab);
            }
        }else{
            return;
        }
    }

    @Override
    public void updateLabelNo(Label label) {
        labelDao.updateLabelNo(label);
    }

    @Override
    public Integer updateLabel(Label label, List<Label> sonList) {
        sonList.add(label);
        return labelDao.updateLabelNo(sonList);
    }




}
