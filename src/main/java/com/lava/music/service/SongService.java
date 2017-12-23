package com.lava.music.service;

import com.lava.music.model.Song;
import com.lava.music.model.User;
import com.lava.music.util.Page;

import java.util.List;
import java.util.Map;

/**
 * Created by mac on 2017/8/23.
 */
public interface SongService {
    Integer findSongTotalCount();
    Integer findSongLabelTotalCount(Long labelId);

    Page<Song> findSongByPage(Page<Song> page);

    Song findById(String songId);

    Page<Song> findSongByLabelPage(Page<Song> page, Long labelId);


    Integer findSongLabelsTotalCount(String labelIds);

    Page<Song> findSongByLabelsPage(Page<Song> page, String labelIds);

    void addLabels(String songId, String labelIds);



    List<Song> findAll();

    void updateSongTsId(String songId);

    void pushSong(String pushIds);


    List<Song> pullSongTask(User user);
}
