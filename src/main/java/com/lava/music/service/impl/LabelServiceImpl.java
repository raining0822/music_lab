package com.lava.music.service.impl;

import com.lava.music.dao.LabelDao;
import com.lava.music.model.Label;
import com.lava.music.service.BaseService;
import com.lava.music.service.LabelService;
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
    public Long addLabel(String labelName, Integer labelLevel, Long fatherId) {
        Label label = new Label();
        label.setLabelName(labelName);
        label.setLabelLevel(labelLevel);
        label.setEffect(1);
        label.setSonNum(0);
        label.setCreateTime(new Date());
        label.setFatherId(fatherId);
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


}
