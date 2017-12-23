package com.lava.music.dao;

import com.lava.music.model.TagAuth;
import com.lava.music.util.Page;

import java.util.List;

/**
 * Created by mac on 2017/12/19.
 */
public interface TagAuthDao {
    Integer insert(TagAuth tagAuth);
    Integer delById(Long id);
    Integer update(TagAuth tagAuth);
    TagAuth selectById(Long id);
    List<TagAuth> selectAll();
    Page<TagAuth> selectByPage(Page<TagAuth> page, String channelName);
}
