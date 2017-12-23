package com.lava.music.dao.impl;

import com.lava.music.dao.BaseDao;
import com.lava.music.dao.SongDao;
import com.lava.music.model.Label;
import com.lava.music.model.Song;
import com.lava.music.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by mac on 2017/8/23.
 */
@Repository
public class SongDaoImpl extends BaseDao implements SongDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Integer insert(Song song) {
        return null;
    }

    @Override
    public Integer delById(Long id) {
        return null;
    }

    @Override
    public Integer update(Song song) {
        return null;
    }

    @Override
    public Song selectById(Long id) {
        String sql = "select * from song where id = ?;";
        RowMapper<Song> songRowMapper = new BeanPropertyRowMapper<Song>(Song.class);
        //return jdbcTemplate.queryForObject(sql, songRowMapper, id);
        List<Song> songList = jdbcTemplate.query(sql, songRowMapper, id);
        if(songList != null && songList.size() > 0){
            return songList.get(0);
        }
        return null;
    }

    @Override
    public List<Song> selectAll() {
        String sql = "select * from song;";
        RowMapper<Song> songRowMapper = new BeanPropertyRowMapper<Song>(Song.class);
        return jdbcTemplate.query(sql, songRowMapper);
    }

    @Override
    public Page<Song> selectByPage(Page<Song> page) {
        RowMapper<Song> songRowMapper = new BeanPropertyRowMapper<Song>(Song.class);
        List<Song> songList = null;
        String sql = "select * from song order by id asc limit ?, ?;";
        songList = jdbcTemplate.query(sql, songRowMapper, (page.getPageNo() - 1) * page.getPageSize(), page.getPageSize());
        page.setList(songList);
        return page;
    }

    @Override
    public Page<Song> findSongByLabelPage(Page<Song> page, Long labelId) {
        String sql = "select s.* from r_song_label as rsl left join song as s on rsl.songId = s.id where rsl.effect = 1 and rsl.labelId = ? order by s.id asc limit ?,?;";
        RowMapper<Song> songRowMapper = new BeanPropertyRowMapper<Song>(Song.class);
        List<Song> songList = jdbcTemplate.query(sql, songRowMapper, labelId, (page.getPageNo() - 1) * page.getPageSize(), page.getPageSize());
        page.setList(songList);
        return page;
    }

    @Override
    public Page<Song> findSongByLabelsPage(Page<Song> page, String labelIds) {
        String sql = "select distinct(s.id),s.songId,s.audioUrl,s.picId,s.songName,s.language,s.artistName,s.realAudioUrl,s.effect,s.picUrl,s.tsUrl from r_song_label as rsl left join song as s on rsl.songId = s.id where rsl.effect = 1 and rsl.labelId in (" + labelIds + ") order by s.id desc limit ?,?;";
        RowMapper<Song> songRowMapper = new BeanPropertyRowMapper<Song>(Song.class);
        List<Song> songList = jdbcTemplate.query(sql, songRowMapper, (page.getPageNo() - 1) * page.getPageSize(), page.getPageSize());
        page.setList(songList);
        return page;
    }

    @Override
    public void addLabels(Long songId, String labelIds) {
        String sql = "select label.* from r_song_label as rsl inner join label on rsl.labelId = label.id  where rsl.songId = ? and label.effect = 1 and rsl.effect = 1;";
        RowMapper<Label> labelRowMapper = new BeanPropertyRowMapper<Label>(Label.class);
        List<Label> labelList = jdbcTemplate.query(sql, labelRowMapper, songId);
        String[] labelArr = labelIds.split(",");
        for(String labelId : labelArr){
            addLabel(songId, Long.valueOf(labelId));
        }
        for(Label label : labelList){
            if(!labelIds.contains(String.valueOf(label.getId()))){
                delLabel(songId, label.getId());
            }
        }
    }



    @Override
    public String selectTsId(String songId) {
        try{
            String sql = "select tsId from r_song_ts where songId = ?;";
            return jdbcTemplate.queryForObject(sql, new Object[]{songId}, String.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void updateTsId(String songId, String tsId) {
        String sql = "update song set tsId = ? where songId = ?;";
        jdbcTemplate.update(sql, tsId, songId);
    }

    @Override
    public void pushSong(String[] ids) {
        if(ids != null && ids.length > 0){
            String sqlStr = "";
            for(String id : ids){
                sqlStr += id + ",";
            }
            if(sqlStr.endsWith(",")){
                sqlStr = sqlStr.substring(0, sqlStr.lastIndexOf(","));
            }
            String sql = "update song set songStatus = 1 where id in (" + sqlStr + ")";
            jdbcTemplate.update(sql);
        }
    }


    @Override
    public Integer selectSongCountByLabels(String labelIds) {
        String sql = "select count(distinct(songId)) from r_song_label where labelId in (" + labelIds + ") and effect = 1;";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }



    @Override
    public Integer selectSongTotalCount() {
        String sql = "select count(0) from song;";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    @Override
    public Integer addLabel(Long songId, Long labelId) {
        String selectSql = "select * from r_song_label where songId = ? and labelId = ?;";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(selectSql, songId, labelId);
        if(list != null && list.size() > 0){
            String updateSql = "update r_song_label set effect = 1 where songId = ? and labelId = ?;";
            return jdbcTemplate.update(updateSql, songId, labelId);
        }else{
            String sql = "insert into r_song_label(songId, labelId, effect) values(?,?,?);";
            return jdbcTemplate.update(sql, songId, labelId, 1);
        }
    }

    @Override
    public Integer delLabel(Long songId, Long labelId) {
        String sql = "update r_song_label set effect = 0 where songId = ? and labelId = ?;";
        return jdbcTemplate.update(sql, songId, labelId);
    }

    @Override
    public Integer selectSongCountByLabel(Long labelId) {
        String sql = "select count(0) from r_song_label where labelId = ? and effect = 1;";
        return jdbcTemplate.queryForObject(sql, Integer.class, labelId);
    }


}
