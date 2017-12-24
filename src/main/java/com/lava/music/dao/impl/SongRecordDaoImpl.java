package com.lava.music.dao.impl;

import com.lava.music.dao.BaseDao;
import com.lava.music.dao.SongRecordDao;
import com.lava.music.model.Song;
import com.lava.music.model.SongRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Leon on 2017/12/24.
 */
@Repository
public class SongRecordDaoImpl extends BaseDao implements SongRecordDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Integer insert(SongRecord songRecord) {
        String sql = "insert into song_record(songId, `action`, createTime, userId)values(?,?,?,?);";
        return jdbcTemplate.update(sql, songRecord.getSongId(), songRecord.getAction(), songRecord.getCreateTime(), songRecord.getUserId());
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
}
