package com.lava.music.dao.impl;

import com.lava.music.dao.BaseDao;
import com.lava.music.dao.SongDao;
import com.lava.music.model.Label;
import com.lava.music.model.Song;
import com.lava.music.model.SongRecord;
import com.lava.music.model.TagAuth;
import com.lava.music.util.LabelUtil;
import com.lava.music.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.*;

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
    public Integer selectSearchTotalCount(String keyword, Integer searchType) {
        StringBuffer sql = new StringBuffer("select count(0) from song where 1 = 1 ");
        if(StringUtils.hasText(keyword) && !keyword.trim().equals("nodata")){
            if(searchType != null && searchType == 0){
                sql.append(" and songName like ? ");
            }
            else if(searchType != null && searchType == 1){
                sql.append(" and albumName like ? ");
            }
            else if(searchType != null && searchType == 2){
                sql.append(" and artistName like ? ");
            }
            return jdbcTemplate.queryForObject(sql.toString(), Integer.class, "%" + keyword + "%");
        }
        return jdbcTemplate.queryForObject(sql.toString(), Integer.class);
    }

    @Override
    public Page<Song> selectSearchBySearch(Page<Song> page, String keyword, Integer searchType) {
        StringBuffer sql = new StringBuffer("select * from song where 1 = 1 ");
        RowMapper<Song> songRowMapper = new BeanPropertyRowMapper<Song>(Song.class);
        List<Song> songList = null;
        if(StringUtils.hasText(keyword) && !keyword.trim().equals("nodata")){
            if(searchType != null && searchType == 0){
                sql.append(" and songName like ? ");
            }
            else if(searchType != null && searchType == 1){
                sql.append(" and albumName like ? ");
            }
            else if(searchType != null && searchType == 2){
                sql.append(" and artistName like ? ");
            }
            sql.append(" order by id asc limit ?, ?; ");
            songList = jdbcTemplate.query(sql.toString(), songRowMapper, "%" + keyword +"%", (page.getPageNo() - 1) * page.getPageSize(), page.getPageSize());
        }else{
            sql.append(" order by id asc limit ?, ?;");
            songList = jdbcTemplate.query(sql.toString(), songRowMapper, (page.getPageNo() - 1) * page.getPageSize(), page.getPageSize());
        }
        page.setList(songList);
        return page;
    }

    @Override
    public void back() {
        String sql = "update song set songStatus = 0 where songStatus = 4;";
        jdbcTemplate.update(sql);
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
    public Page<Song> findSongByLabelPage(Page<Song> page, Long labelId) {
        String sql = "select s.* from r_song_label as rsl left join song as s on rsl.songId = s.id where rsl.effect = 1 and rsl.labelId = ? order by s.id asc limit ?,?;";
        RowMapper<Song> songRowMapper = new BeanPropertyRowMapper<Song>(Song.class);
        List<Song> songList = jdbcTemplate.query(sql, songRowMapper, labelId, (page.getPageNo() - 1) * page.getPageSize(), page.getPageSize());
        page.setList(songList);
        return page;
    }



    @Override
    public void addLabels(Long songId, String labelIds) {
        if(StringUtils.hasText(labelIds)){
            //删除单曲上打的标签
            String deleteSql = "delete from r_song_label where songId = ?;";
            jdbcTemplate.update(deleteSql, songId);
            //将新的标签和单曲关系入库
            List<Object[]> params = new ArrayList<Object[]>();
            String insertSql = "insert into r_song_label(songId, labelId, effect)values(?,?,?);";
            for(String labelId : labelIds.split(",")){
                params.add(new Object[]{songId, labelId, 1});
            }
            jdbcTemplate.batchUpdate(insertSql, params);
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
        //String sql = "select count(distinct(songId)) from r_song_label where labelId in (" + labelIds + ") and effect = 1;";
        String sql = "SELECT COUNT(songId) AS number  FROM r_song_label WHERE labelId IN (" + labelIds + ") GROUP BY songId HAVING number = ?;";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql, labelIds.split(",").length);
        return maps.size();
    }

    @Override
    public Page<Song> findSongByLabelsPage(Page<Song> page, String labelIds) {
        //String sql = "select distinct(s.id),s.songId,s.audioUrl,s.picId,s.songName,s.language,s.artistName,s.realAudioUrl,s.effect,s.picUrl,s.tsUrl from r_song_label as rsl left join song as s on rsl.songId = s.id where rsl.effect = 1 and rsl.labelId in (" + labelIds + ") order by s.id desc limit ?,?;";
        String sql = "SELECT s.*,rsl.songId, COUNT(rsl.songId) AS number  FROM r_song_label AS rsl INNER JOIN song AS s ON rsl.songId = s.id WHERE rsl.labelId IN (" + labelIds + ") GROUP BY rsl.songId HAVING number = ? order by s.id desc limit ?,?;";
        RowMapper<Song> songRowMapper = new BeanPropertyRowMapper<Song>(Song.class);
        List<Song> songList = jdbcTemplate.query(sql, songRowMapper, labelIds.split(",").length, (page.getPageNo() - 1) * page.getPageSize(), page.getPageSize());
        page.setList(songList);
        return page;
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
        String sql = "update song set songStatus = ?,auditResult = ?  where id = ?;";
        return jdbcTemplate.update(sql, song.getSongStatus(), song.getAuditResult(), song.getId());
    }

    public Integer updateSongStatusAndSubmitTime(Song song){
        String sql = "update song set songStatus = ?, submitTime = ? where id = ?;";
        return jdbcTemplate.update(sql, song.getSongStatus(), song.getSubmitTime(), song.getId());
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
        String sql = "select * from song where taskUserId = ? and songStatus = 2 order by taskTime desc;";
        RowMapper<Song> rowMapper = new BeanPropertyRowMapper<Song>(Song.class);
        return jdbcTemplate.query(sql, rowMapper, userId);
    }

    public void updateSongTagFlag(Long songId){
        String sql1 = "update song set basicTag = 0, reasonTag = 0, sensibilityTag = 0 where id = ?;";
        jdbcTemplate.update(sql1, songId);
    }

    @Override
    public List<Song> selectSongBySongStatus(int songStatus) {
        String sql = "select * from song where songStatus = ?;";
        RowMapper<Song> rowMapper = new BeanPropertyRowMapper<Song>(Song.class);
        return jdbcTemplate.query(sql, rowMapper, songStatus);
    }

    @Override
    public void updateSongs(List<Song> taskList) {
        List<Object[]> params = new ArrayList<Object[]>();
        String sql = "update song set songStatus = ?, taskTime = ?, taskUserId = ? where id = ?;";
        for(Song song : taskList){
            params.add(new Object[]{song.getSongStatus(), song.getTaskTime(), song.getTaskUserId(), song.getId()});
        }
        jdbcTemplate.batchUpdate(sql, params);
    }

    @Override
    public void updateSongsOfAudit(List<Song> taskList) {
        List<Object[]> params = new ArrayList<Object[]>();
        String sql = "update song set auditUserId = ?, songStatus = ? where id = ?;";
        for(Song song : taskList){
            params.add(new Object[]{song.getAuditUserId(), song.getSongStatus(), song.getId()});
        }
        jdbcTemplate.batchUpdate(sql, params);
    }


    @Override
    public List<Song> selectUserSubmitSong(Long userId) {
        String sql = "select * from song where taskUserId = ? and songStatus in (3,4,5) order by taskTime desc;";
        RowMapper<Song> rowMapper = new BeanPropertyRowMapper<Song>(Song.class);
        return jdbcTemplate.query(sql, rowMapper, userId);
    }

    @Override
    public List<Song> selectUserAuditSong(Long userId) {
        String sql = "select * from song where auditUserId = ? and songStatus = 5 order by taskTime desc;";
        RowMapper<Song> rowMapper = new BeanPropertyRowMapper<Song>(Song.class);
        return jdbcTemplate.query(sql, rowMapper, userId);
    }

    @Override
    public List<Song> selectUserDoneSong(Long userId) {
        String sql = "select * from song where auditUserId = ? and songStatus = 4 order by taskTime desc;";
        RowMapper<Song> rowMapper = new BeanPropertyRowMapper<Song>(Song.class);
        return jdbcTemplate.query(sql, rowMapper, userId);
    }

    @Override
    public void updateSongTagFlag(Song song) {
        StringBuffer sql = new StringBuffer("update song set id = ?  ");
        if(song.getBasicTag() != null){
            sql.append(", basicTag = 1 ");
        }
        if(song.getReasonTag() != null){
            sql.append(", reasonTag = 1 ");
        }
        if(song.getSensibilityTag() != null){
            sql.append(", sensibilityTag = 1 ");
        }
        sql.append("where id = ?;");
        jdbcTemplate.update(sql.toString(), song.getId(), song.getId());
    }


}
