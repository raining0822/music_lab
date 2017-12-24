package com.lava.music.service.impl;

import com.lava.music.dao.SongDao;
import com.lava.music.dao.SongRecordDao;
import com.lava.music.dao.UserDao;
import com.lava.music.model.Song;
import com.lava.music.model.SongRecord;
import com.lava.music.model.TagAuth;
import com.lava.music.model.User;
import com.lava.music.service.BaseService;
import com.lava.music.service.SongService;
import com.lava.music.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by mac on 2017/8/23.
 */
@Service
public class SongServiceImpl extends BaseService implements SongService {

    @Autowired
    private SongDao songDao;

    @Autowired
    private SongRecordDao songRecordDao;

    @Autowired
    private UserDao userDao;

    @Override
    public Integer findSongTotalCount() {
        return songDao.selectSongTotalCount();
    }

    @Override
    public Integer findSongLabelTotalCount(Long labelId) {
        return songDao.selectSongCountByLabel(labelId);
    }

    @Override
    public Page<Song> findSongByPage(Page<Song> page) {
        return songDao.selectByPage(page);
    }

    @Override
    public Song findById(String songId) {
        return songDao.selectById(Long.valueOf(songId));
    }


    @Override
    public Page<Song> findSongByLabelPage(Page<Song> page, Long labelId) {
        return songDao.findSongByLabelPage(page, labelId);
    }


    @Override
    public Integer findSongLabelsTotalCount(String labelIds) {

        return songDao.selectSongCountByLabels(labelIds);
    }

    @Override
    public Page<Song> findSongByLabelsPage(Page<Song> page, String labelIds) {
        return songDao.findSongByLabelsPage(page, labelIds);
    }

    @Override
    public void addLabels(String songId, String labelIds) {
        songDao.addLabels(Long.valueOf(songId), labelIds);
    }

    @Override
    public List<Song> findAll() {
        return songDao.selectAll();
    }

    @Override
    public void updateSongTsId(String songId) {
        String tsId = songDao.selectTsId(songId);
        if(StringUtils.hasText(tsId)){
            songDao.updateTsId(songId, tsId);
        }
    }

    @Transactional
    @Override
    public void pushSong(String pushIds, Long userId) {
        String[] ids = pushIds.split(",");
        songDao.pushSong(ids);
        //添加操作记录
        for(String songId : ids){
            SongRecord songRecord = new SongRecord();
            songRecord.setAction(SongRecord.PUSH_SONG);
            songRecord.setCreateTime(new Date());
            songRecord.setSongId(Long.valueOf(songId));
            songRecord.setUserId(userId);
            songRecordDao.insert(songRecord);
        }

    }

    @Transactional
    @Override
    public List<Song> pullSongTask(User user, Integer count) {
        //获取用户的标签权限
        List<TagAuth> tagAuthList = userDao.selectUserTagAuth(String.valueOf(user.getId()));
        //领取任务
        List<Song> songList = songDao.selectUserTaskSongFromSong(tagAuthList, count);
        if(songList != null && songList.size() > 0){
            //批量更新单曲的状态
            songDao.updateSongStatus(songList, Song.PULLED);
            //批量添加更新状态的日志
            songRecordDao.insert(songList, Song.PULLED, user.getId());
            //批量插入用户任务表
            userDao.insertUserTask(user.getId(), songList);
        }
        return songList;
    }

    @Override
    public List<Song> findUserTask(Long userId) {
        return songDao.selectUserTaskSongFromTask(userId);
    }
}
