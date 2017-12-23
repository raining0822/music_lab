package com.lava.music.dao.impl;

import com.lava.music.dao.BaseDao;
import com.lava.music.dao.TagAuthDao;
import com.lava.music.model.TagAuth;
import com.lava.music.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by mac on 2017/12/19.
 */
@Repository
public class TagAuthDaoImpl extends BaseDao implements TagAuthDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Integer insert(TagAuth tagAuth) {
        return null;
    }

    @Override
    public Integer delById(Long id) {
        return null;
    }

    @Override
    public Integer update(TagAuth tagAuth) {
        return null;
    }

    @Override
    public TagAuth selectById(Long id) {
        return null;
    }

    @Override
    public List<TagAuth> selectAll() {
        return null;
    }

    @Override
    public Page<TagAuth> selectByPage(Page<TagAuth> page, String channelName) {
        return null;
    }
}
