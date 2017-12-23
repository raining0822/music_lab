package com.lava.music.dao.impl;

import com.lava.music.dao.BaseDao;
import com.lava.music.dao.UserRecordDao;
import com.lava.music.model.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by mac on 2017/8/30.
 */
@Repository
public class UserRecordDaoImpl extends BaseDao implements UserRecordDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Integer insert(UserRecord userRecord) {
        String sql = "insert into user_record(userId,action,createTime,sourceData)values(?,?,?,?);";
        return jdbcTemplate.update(sql, userRecord.getUserId(), userRecord.getAction(), userRecord.getCreateTime(), userRecord.getSourceData());
    }

    public Integer selectAddLabel(Long userId){
        String sql = "select count(0) from user_record where action = ? and userId = ?;";
        return jdbcTemplate.queryForObject(sql, Integer.class, UserRecord.SONG_ADD_LABEL, userId);
    }

    public Integer selectDelLabel(Long userId){
        String sql = "select count(0) from user_record where action = ? and userId = ?;";
        return jdbcTemplate.queryForObject(sql, Integer.class, UserRecord.SONG_DEL_LABEL, userId);
    }

    public Integer selectSong(Long userId){
        String sql = "select * from user_record where userid = ? and (action = ? or action = ?);";
        RowMapper<UserRecord> userRecordRowMapper = new BeanPropertyRowMapper<UserRecord>(UserRecord.class);
        List<UserRecord> userRecordList = jdbcTemplate.query(sql, userRecordRowMapper, userId, UserRecord.SONG_DEL_LABEL, UserRecord.SONG_ADD_LABEL);
        Set<String> songIds = new HashSet<String>();
        for(UserRecord userRecord : userRecordList){
            String sourceData = userRecord.getSourceData();
            if(StringUtils.hasText(sourceData) && sourceData.contains("|")){
                String songId = sourceData.split("\\|")[0];
                songIds.add(songId);
            }
        }
        return songIds.size();
    }
}
