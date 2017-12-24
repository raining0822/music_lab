package com.lava.music.dao;

import com.lava.music.model.Song;
import com.lava.music.model.SongRecord;

import java.util.List;

/**
 * Created by Leon on 2017/12/24.
 */
public interface SongRecordDao {

    Integer insert(SongRecord songRecord);
    Integer insert(List<Song> songList, Integer action, Long userId);
}
