package com.lava.music.dao.impl;

import com.lava.music.dao.BaseDao;
import com.lava.music.dao.SongDao;
import com.lava.music.model.Label;
import com.lava.music.model.Song;
import com.lava.music.model.TagAuth;
import com.lava.music.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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
            String sql = "update song set songStatus = ? where id in (" + sqlStr + ");";
            jdbcTemplate.update(sql, Song.PUSHED);
        }
    }

    /**
     * 根据用户的标签权限，查询用户匹配的任务
     * @param tagAuthList
     * @return
     */
    @Override
    public List<Song> selectUserTaskSongFromSong(List<TagAuth> tagAuthList, Integer count) {
        StringBuffer sql = new StringBuffer("SELECT s.* FROM song AS s inner join song_record as sr on s.id = sr.songId where `effect` = 1 and `songStatus` = 1 and sr.action = '1' ");
        if(tagAuthList != null && tagAuthList.size() > 0){
            sql.append("and ( ");
            if(tagAuthList.size() == 1){
                TagAuth tagAuth = tagAuthList.get(0);
                sql.append(getTagAuthSqlStr(tagAuth));
            }
            else if(tagAuthList.size() == 2){
                TagAuth tagAuth0 = tagAuthList.get(0);
                sql.append(getTagAuthSqlStr(tagAuth0));
                TagAuth tagAuth1 = tagAuthList.get(1);
                sql.append(" OR ");
                sql.append(getTagAuthSqlStr(tagAuth1));
            }
            else if(tagAuthList.size() == 3){
                TagAuth tagAuth0 = tagAuthList.get(0);
                sql.append(getTagAuthSqlStr(tagAuth0));
                TagAuth tagAuth1 = tagAuthList.get(1);
                sql.append(" OR ");
                sql.append(getTagAuthSqlStr(tagAuth1));
                TagAuth tagAuth2 = tagAuthList.get(2);
                sql.append(" OR ");
                sql.append(getTagAuthSqlStr(tagAuth2));
            }
            sql.append(" ) ");
            sql.append(" ORDER BY sr.createTime DESC LIMIT 0, ?;");
        }
        RowMapper<Song> rowMapper = new BeanPropertyRowMapper<Song>(Song.class);
        List<Song> taskList = jdbcTemplate.query(sql.toString(), rowMapper, count);
        return taskList;
    }

    private String getTagAuthSqlStr(TagAuth tagAuth){
        if(tagAuth.getId() == 1){
            return " `basicTag` is null ";
        }
        else if(tagAuth.getId() == 2){
            return " `reasonTag` IS NULL ";
        }
        else if(tagAuth.getId() == 3){
            return " `sensibilityTag` is null ";
        }
        return null;
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


    public Integer updateSongStatus(Song song){
        String sql = "update song set songStatus = ? where id = ?;";
        return jdbcTemplate.update(sql, song.getSongStatus(), song.getId());
    }

    @Override
    public Integer updateSongStatus(List<Song> songList, Integer songStatus) {
        List<Object[]> params = new ArrayList<Object[]>();
        for(Song song : songList){
            params.add(new Object[]{songStatus, song.getId()});
        }
        String sql = "update song set songStatus = ? where id = ?;";
        int[] numbers = jdbcTemplate.batchUpdate(sql, params);
        return numbers.length;
    }

    @Override
    public List<Song> selectUserTaskSongFromTask(Long userId) {
        String sql = "select s.* from song as s INNER JOIN user_task AS ut ON s.id = ut.songId INNER JOIN song_record AS sr ON sr.songId= ut.songId WHERE ut.userId = ? AND sr.action = '1' order by sr.createTime desc;";
        RowMapper<Song> rowMapper = new BeanPropertyRowMapper<Song>(Song.class);
        return jdbcTemplate.query(sql, rowMapper, userId);
    }


}
