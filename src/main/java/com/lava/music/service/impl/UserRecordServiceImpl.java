package com.lava.music.service.impl;

import com.lava.music.dao.UserRecordDao;
import com.lava.music.model.UserRecord;
import com.lava.music.service.BaseService;
import com.lava.music.service.UserRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by mac on 2017/8/30.
 */
@Service
public class UserRecordServiceImpl extends BaseService implements UserRecordService {

    @Autowired
    private UserRecordDao userRecordDao;

    @Override
    public Integer addRecord(UserRecord userRecord) {
        return userRecordDao.insert(userRecord);
    }

    @Override
    public Integer selectAddLabel(Long userId) {
        return userRecordDao.selectAddLabel(userId);
    }

    @Override
    public Integer selectDelLabel(Long userId) {
        return userRecordDao.selectDelLabel(userId);
    }

    @Override
    public Integer selectSong(Long userId) {
        return userRecordDao.selectSong(userId);
    }
}
