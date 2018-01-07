package com.lava.music.service;

import com.lava.music.model.SongRecord;

import java.util.List;
import java.util.Map;

/**
 * Created by Leon on 2017/12/24.
 */
public interface SongRecordService {

    Integer add(SongRecord songRecord);

    List<Map<String, Object>> findLogBySongId(Long songId);
}
