package com.lava.music.dao.impl;

import com.lava.music.dao.BaseDao;
import com.lava.music.dao.SongRecordDao;
import com.lava.music.model.Song;
import com.lava.music.model.SongRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Leon on 2017/12/24.
 */
@Repository
public class SongRecordDaoImpl extends BaseDao implements SongRecordDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Integer insert(SongRecord songRecord) {
        String sql = "insert into song_record(songId, `action`, createTime, userId, metaData)values(?,?,?,?,?);";
        return jdbcTemplate.update(sql, songRecord.getSongId(), songRecord.getAction(), songRecord.getCreateTime(), songRecord.getUserId(), songRecord.getMetaData());
    }

    @Override
    public Integer insert(List<Song> songList, Integer action, Long userId) {
        List<Object[]> params = new ArrayList<Object[]>();
        for(Song song : songList){
            params.add(new Object[]{song.getId(),action, new Date(), userId});
        }
        String sql = "insert into song_record(songId, `action`, createTime, userId)values(?,?,?,?);";
        int[] numbers = jdbcTemplate.batchUpdate(sql, params);
        return numbers.length;
    }

    @Override
    public void insert(List<SongRecord> songRecordList) {
        String sql = "insert into song_record(songId, action, createTime, userId, metaData)values(?,?,?,?,?);";
        List<Object[]> params = new ArrayList<Object[]>();
        for(SongRecord songRecord : songRecordList){
            params.add(new Object[]{songRecord.getSongId(), songRecord.getAction(), songRecord.getCreateTime(), songRecord.getUserId(), songRecord.getMetaData()});
        }
        jdbcTemplate.batchUpdate(sql, params);
    }

    @Override
    public List<SongRecord> selectBySongId(Long songId) {
        String sql = "select * from song_record where songId = ? order by createTime ASC ;";
        RowMapper<SongRecord> songRecordRowMapper = new BeanPropertyRowMapper<SongRecord>(SongRecord.class);
        return jdbcTemplate.query(sql, songRecordRowMapper, songId);
    }

    public List<Map<String, Object>> selectSongMsg(Long songId){
        String sql = "select sr.songId,s.songName,u.userType,u.email,sr.userId,sr.createTime, sr.action, sr.metaData,u1.email as dataEmail from song_record as sr left join song as s on sr.songId = s.id left join user as u on sr.userId = u.id left join user as u1 on sr.metaData = u1.id  where sr.songId = ?;";
        return jdbcTemplate.queryForList(sql, songId);
    }

    @Override
    public List<SongRecord> selectSongRecord(Long songId, Integer action) {
        String sql = "SELECT * FROM song_record WHERE songId = ? AND `action` = ? ORDER BY createTime DESC LIMIT 1;";
        RowMapper<SongRecord> songRecordRowMapper = new BeanPropertyRowMapper<SongRecord>(SongRecord.class);
        return jdbcTemplate.query(sql, songRecordRowMapper, songId, action);
    }
}
