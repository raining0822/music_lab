package com.lava.music.service.impl;

import com.lava.music.dao.SongRecordDao;
import com.lava.music.model.SongRecord;
import com.lava.music.service.BaseService;
import com.lava.music.service.SongRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Leon on 2017/12/24.
 */
@Service
public class SongRecordServiceImpl extends BaseService implements SongRecordService {

    @Autowired
    private SongRecordDao songRecordDao;

    @Override
    public Integer add(SongRecord songRecord) {
        return songRecordDao.insert(songRecord);
    }
}
