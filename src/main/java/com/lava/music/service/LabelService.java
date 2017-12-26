package com.lava.music.service;

import com.lava.music.model.Label;

import java.util.List;

/**
 * Created by mac on 2017/8/23.
 */
public interface LabelService {

    Long addLabel(String labelName, Label fatherLabel);
    List<Label> findDimension();
    List<Label> findLabel(Long fatherId);
    Integer moveLabel(Long labelId, Long fatherId);
    Integer delLabel(Long id);

    List<Label> findLabelBySongId(String songId);

    Label findById(Long aLong);

    List<Label> findLabels(String labelIds);

    List<Label> findLabel(Label label);

    void updateLabel(Long labelId, String labelName);

    void initLabelNo();

    void updateLabelNo(Label label);

    Integer updateLabel(Label targetLabel, List<Label> targetLabelSonList);
}
