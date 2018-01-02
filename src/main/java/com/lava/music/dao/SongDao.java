package com.lava.music.dao;

import com.lava.music.model.Song;
import com.lava.music.model.TagAuth;
import com.lava.music.model.User;
import com.lava.music.util.Page;

import java.util.List;
import java.util.Map;

/**
 * Created by mac on 2017/8/23.
 */
public interface SongDao {

    Integer insert(Song song);
    Integer delById(Long id);
    Integer update(Song song);
    Song selectById(Long id);
    List<Song> selectAll();
    Page<Song> selectByPage(Page<Song> page);

    Integer selectSongTotalCount();
    Integer addLabel(Long songId, Long labelId);
    Integer delLabel(Long songId, Long labelId);

    Integer selectSongCountByLabel(Long labelId);

    Page<Song> findSongByLabelPage(Page<Song> page, Long labelId);


    Integer selectSongCountByLabels(String labelIds);

    Page<Song> findSongByLabelsPage(Page<Song> page, String labelIds);

    void addLabels(Long songId, String labelIds);

    String selectTsId(String songId);

    void updateTsId(String songId, String tsId);

    void pushSong(String[] ids);

    List<Song> selectUserTaskSongFromSong(List<TagAuth> tagAuthList, Integer count);
    Integer updateSongStatus(Song song);
    Integer updateSongStatus(List<Song> songList, Integer songStatus);

    List<Song> selectUserTaskSongFromTask(Long userId);
    List<Song> selectUserSubmitSong(Long userId);
    List<Song> selectUserAuditSong(Long userId);
    List<Song> selectUserDoneSong(Long userId);

    void updateSongTagFlag(Song song);
    void updateSongTagFlag(Long songId);

    List<Song> selectSongBySongStatus(int songStatus);

    void updateSongs(List<Song> taskList);
    Integer updateSongStatusAndSubmitTime(Song song);

    void updateSongsOfAudit(List<Song> taskList);

    Integer selectSearchTotalCount(String keyword, Integer searchType);

    Page<Song> selectSearchBySearch(Page<Song> page, String keyword, Integer searchType);

    void back();
}
