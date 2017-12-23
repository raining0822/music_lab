package com.lava.music.service.impl;

import com.lava.music.dao.SongDao;
import com.lava.music.model.Song;
import com.lava.music.model.TagAuth;
import com.lava.music.model.User;
import com.lava.music.service.BaseService;
import com.lava.music.service.SongService;
import com.lava.music.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by mac on 2017/8/23.
 */
@Service
public class SongServiceImpl extends BaseService implements SongService {

    @Autowired
    private SongDao songDao;

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

    @Override
    public void pushSong(String pushIds) {
        String[] ids = pushIds.split(",");
        songDao.pushSong(ids);
    }

    @Override
    public List<Song> pullSongTask(User user) {
        List<TagAuth> tagAuthList = user.getTagAuthList();

        return null;
    }
}
