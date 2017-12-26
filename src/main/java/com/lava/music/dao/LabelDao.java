package com.lava.music.dao;

import com.lava.music.model.Label;
import com.lava.music.util.Page;

import java.util.List;

/**
 * Created by mac on 2017/8/23.
 */
public interface LabelDao {
    Long insert(Label label);
    Integer delById(Long id);
    Integer update(Label label);
    Label selectById(Long id);
    List<Label> selectAll();
    Page<Label> selectByPage(Page<Label> page);

    List<Label> findByLevel(Integer labelLevel);
    List<Label> findByFatherId(Long fatherId);

    Label selectRootLabel();
    Integer updateFatherId(Long fatherId, Long labelId);

    List<Label> selectLabelBySongId(String songId);


    Label selectEffectById(Long id);

    List<Label> selectByIds(String labelIds);

    Integer updateLabelNo(Label label);

    Integer updateLabelNo(List<Label> sonList);
}
