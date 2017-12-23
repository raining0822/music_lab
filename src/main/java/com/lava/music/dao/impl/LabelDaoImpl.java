package com.lava.music.dao.impl;

import com.lava.music.dao.BaseDao;
import com.lava.music.dao.LabelDao;
import com.lava.music.model.Label;
import com.lava.music.util.Page;
import com.mysql.jdbc.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by mac on 2017/8/23.
 */
@Repository
public class LabelDaoImpl extends BaseDao implements LabelDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Long insert(final Label label) {
        final String sql = "insert into label(labelName, labelNo, labelLevel, fatherId, effect, createTime) values (?,?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        //return jdbcTemplate.update(sql, label.getLabelName(),null,label.getLabelLevel(), label.getFatherId(), label.getEffect());
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, label.getLabelName());
                ps.setString(2,"");
                ps.setInt(3, label.getLabelLevel());
                ps.setLong(4, label.getFatherId());
                ps.setInt(5, label.getEffect());
                ps.setObject(6, label.getCreateTime());
                return ps;
            }
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Integer delById(Long id) {
        String sql = "update label set effect = 0 where id = ?;";
        Integer result = jdbcTemplate.update(sql, id);
        String sql2 = "update r_song_label set effect = 0 where labelId = ?;";
        Integer result2 = jdbcTemplate.update(sql2, id);
        return result + result2;
    }

    @Override
    public Integer update(Label label) {
        if(label.getLabelLevel() != null){
            String sql = "update label set labelName = ?, labelLevel = ? where id = ?;";
            return jdbcTemplate.update(sql, label.getLabelName(), label.getLabelLevel(), label.getId());
        }else{
            String sql = "update label set labelName = ? where id = ?;";
            return jdbcTemplate.update(sql, label.getLabelName(), label.getId());
        }
    }



    @Override
    public Label selectById(Long id) {
        String sql = "select * from label where id = ?;";
        RowMapper<Label> labelRowMapper = new BeanPropertyRowMapper<Label>(Label.class);
        return jdbcTemplate.queryForObject(sql, labelRowMapper, id);
    }

    @Override
    public List<Label> selectAll() {
        return null;
    }

    @Override
    public Page<Label> selectByPage(Page<Label> page) {
        return null;
    }

    @Override
    public List<Label> findByLevel(Integer labelLevel) {
        String sql = "select * from label where labelLevel = ? and effect = 1 order by id asc;";
        RowMapper<Label> labelRowMapper = new BeanPropertyRowMapper<Label>(Label.class);
        return jdbcTemplate.query(sql, labelRowMapper, labelLevel);
    }

    @Override
    public List<Label> findByFatherId(Long fatherId) {
        String sql = "select * from label where fatherId = ? and effect = 1 order by id asc;";
        RowMapper<Label> labelRowMapper = new BeanPropertyRowMapper<Label>(Label.class);
        return jdbcTemplate.query(sql, labelRowMapper, fatherId);
    }

    @Override
    public Label selectRootLabel() {
        String sql = "select * from label where labelLevel = 0;";
        RowMapper<Label> labelRowMapper = new BeanPropertyRowMapper<Label>(Label.class);
        return jdbcTemplate.queryForObject(sql, labelRowMapper);
    }

    @Override
    public Integer updateFatherId(Long fatherId, Long labelId) {
        String sql = "update label set fatherId = ? where id = ?;";
        return jdbcTemplate.update(sql, fatherId, labelId);
    }

    @Override
    public List<Label> selectLabelBySongId(String songId) {
        String sql = "select label.* from r_song_label as rsl inner join label on rsl.labelId = label.id  where rsl.songId = ? and label.effect = 1 and rsl.effect = 1;";
        RowMapper<Label> labelRowMapper = new BeanPropertyRowMapper<Label>(Label.class);
        return jdbcTemplate.query(sql, labelRowMapper, songId);
    }

    @Override
    public Label selectEffectById(Long id) {
        String sql = "select * from label where id = ? and effect = 1 order by id asc;";
        RowMapper<Label> labelRowMapper = new BeanPropertyRowMapper<Label>(Label.class);
        List<Label> labelList = jdbcTemplate.query(sql, labelRowMapper, id);
        if(labelList != null && labelList.size() > 0){
            return labelList.get(0);
        }
        return null;
    }

    @Override
    public List<Label> selectByIds(String labelIds) {
        String sql = "select * from label where id in (" + labelIds + ") and effect = 1;";
        RowMapper<Label> labelRowMapper = new BeanPropertyRowMapper<Label>(Label.class);
        return jdbcTemplate.query(sql, labelRowMapper);
    }
}
