package com.lava.music.service;

import com.lava.music.model.UserRecord;

/**
 * Created by mac on 2017/8/30.
 */
public interface UserRecordService {

    Integer addRecord(UserRecord userRecord);

    Integer selectAddLabel(Long userId);

    Integer selectDelLabel(Long userId);

    Integer selectSong(Long userId);
}
