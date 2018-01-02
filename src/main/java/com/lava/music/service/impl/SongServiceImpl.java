package com.lava.music.service.impl;

import com.lava.music.dao.*;
import com.lava.music.model.*;
import com.lava.music.service.BaseService;
import com.lava.music.service.SongService;
import com.lava.music.util.LabelUtil;
import com.lava.music.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

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

    @Autowired
    private LabelDao labelDao;

    @Autowired
    private UserRecordDao userRecordDao;

    @Override
    public Integer findSongTotalCount() {
        return songDao.selectSongTotalCount();
    }

    @Override
    public Integer searchSongTotalCount(String keyword, Integer searchType){
        return songDao.selectSearchTotalCount(keyword, searchType);
    }

    @Override
    public Page<Song> findSongBySearchPage(Page<Song> page, String keyword, Integer searchType) {
        return songDao.selectSearchBySearch(page, keyword, searchType);
    }

    @Override
    public void back() {
        songDao.back();
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
       /* //获取用户的标签权限
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
        return songList;*/
       return null;
    }


    @Override
    public void addLabels(String songId, String labelIds, Long userId) {
        //获取单曲下的标签
        List<Label> songLabels = labelDao.selectLabelBySongId(songId);
        //如果标签与原有一直，则不做任何操作
        if(LabelUtil.checkTag(songLabels, labelIds.split(","))){
            return;
        }else{
            songDao.addLabels(Long.valueOf(songId), labelIds);
            //刷新单曲的标签权限标识
            songDao.updateSongTagFlag(Long.valueOf(songId));
            List<Label> songNewLabels = labelDao.selectLabelBySongId(songId);
            boolean basicFlag = false;
            boolean reasonFlag = false;
            boolean sensFlag = false;
            for(Label label : songNewLabels){
                String labelNo = label.getLabelNo();
                if(labelNo.startsWith("000001") && !basicFlag){
                    Song song = new Song();
                    song.setId(Long.valueOf(songId));
                    song.setBasicTag(1);
                    songDao.updateSongTagFlag(song);
                    basicFlag = true;
                }
                else if(labelNo.startsWith("000002") && !reasonFlag){
                    Song song = new Song();
                    song.setId(Long.valueOf(songId));
                    song.setReasonTag(1);
                    songDao.updateSongTagFlag(song);
                    reasonFlag = true;
                }
                else if(labelNo.startsWith("000003") && !sensFlag){
                    Song song = new Song();
                    song.setId(Long.valueOf(songId));
                    song.setSensibilityTag(1);
                    songDao.updateSongTagFlag(song);
                    sensFlag = true;
                }
            }
        }
    }

    @Override
    public void auditLabels(String songId, String labelIds, Long userId) {
        Song auditSong = songDao.selectById(Long.valueOf(songId));
        User user = userDao.selectById(auditSong.getTaskUserId());
        //获取单曲下的标签
        List<Label> songLabels = labelDao.selectLabelBySongId(songId);
        //如果标签与原有一直，则不做任何操作
        Boolean checkResult = LabelUtil.checkTag(songLabels, labelIds.split(","));
        if(checkResult){
            //记录该单曲打标签用户的正确率
            user.setAuditNumber(user.getAuditNumber() + 1);
            userDao.updateUserAuditNumber(user);
            auditSong.setAuditResult("OK");
        }else{
            songDao.addLabels(Long.valueOf(songId), labelIds);
            //刷新单曲的标签权限标识
            songDao.updateSongTagFlag(Long.valueOf(songId));
            List<Label> songNewLabels = labelDao.selectLabelBySongId(songId);
            boolean basicFlag = false;
            boolean reasonFlag = false;
            boolean sensFlag = false;
            for(Label label : songNewLabels){
                String labelNo = label.getLabelNo();
                if(labelNo.startsWith("000001") && !basicFlag){
                    Song song = new Song();
                    song.setId(Long.valueOf(songId));
                    song.setBasicTag(1);
                    songDao.updateSongTagFlag(song);
                    basicFlag = true;
                }
                else if(labelNo.startsWith("000002") && !reasonFlag){
                    Song song = new Song();
                    song.setId(Long.valueOf(songId));
                    song.setReasonTag(1);
                    songDao.updateSongTagFlag(song);
                    reasonFlag = true;
                }
                else if(labelNo.startsWith("000003") && !sensFlag){
                    Song song = new Song();
                    song.setId(Long.valueOf(songId));
                    song.setSensibilityTag(1);
                    songDao.updateSongTagFlag(song);
                    sensFlag = true;
                }
            }
            auditSong.setAuditResult("PROBLEM");
        }
        //修改单曲状态为审核已通过
        auditSong.setSongStatus(Song.AUDITED);
        songDao.updateSongStatus(auditSong);
        //添加审核的日志
        SongRecord songRecord = new SongRecord();
        songRecord.setAction(SongRecord.AUDIT_SONG);
        songRecord.setCreateTime(new Date());
        songRecord.setSongId(Long.valueOf(songId));
        songRecord.setUserId(userId);
        songRecord.setMetaData(auditSong.getAuditResult() + "|" + auditSong.getTaskUserId());
        songRecordDao.insert(songRecord);

        //添加日志
        UserRecord userRecord = new UserRecord();
        userRecord.setAction(UserRecord.SONG_AUDIT_LABEL);
        userRecord.setCreateTime(new Date());
        userRecord.setUserId(userId);
        userRecord.setSourceData(songId + "|" + labelIds);
        userRecordDao.insert(userRecord);

    }

    @Override
    public void checkLabels(String songId, String labelIds, Long userId) {
        Song auditSong = songDao.selectById(Long.valueOf(songId));
        User user = userDao.selectById(auditSong.getTaskUserId());
        //获取单曲下的标签
        List<Label> songLabels = labelDao.selectLabelBySongId(songId);
        //如果标签与原有一直，则不做任何操作
        Boolean checkResult = LabelUtil.checkTag(songLabels, labelIds.split(","));
        if(checkResult){
            return;
        }else{
            songDao.addLabels(Long.valueOf(songId), labelIds);
            //刷新单曲的标签权限标识
            songDao.updateSongTagFlag(Long.valueOf(songId));
            List<Label> songNewLabels = labelDao.selectLabelBySongId(songId);
            boolean basicFlag = false;
            boolean reasonFlag = false;
            boolean sensFlag = false;
            for(Label label : songNewLabels){
                String labelNo = label.getLabelNo();
                if(labelNo.startsWith("000001") && !basicFlag){
                    Song song = new Song();
                    song.setId(Long.valueOf(songId));
                    song.setBasicTag(1);
                    songDao.updateSongTagFlag(song);
                    basicFlag = true;
                }
                else if(labelNo.startsWith("000002") && !reasonFlag){
                    Song song = new Song();
                    song.setId(Long.valueOf(songId));
                    song.setReasonTag(1);
                    songDao.updateSongTagFlag(song);
                    reasonFlag = true;
                }
                else if(labelNo.startsWith("000003") && !sensFlag){
                    Song song = new Song();
                    song.setId(Long.valueOf(songId));
                    song.setSensibilityTag(1);
                    songDao.updateSongTagFlag(song);
                    sensFlag = true;
                }
            }
            //添加日志
            UserRecord userRecord = new UserRecord();
            userRecord.setAction(UserRecord.SONG_ADD_LABEL);
            userRecord.setCreateTime(new Date());
            userRecord.setUserId(userId);
            userRecord.setSourceData(songId + "|" + labelIds);
            userRecordDao.insert(userRecord);
        }

    }

    @Override
    public void allotTask() {
        //获取所有推送的单曲
        List<Song> songList = songDao.selectSongBySongStatus(Song.PUSHED);
        if(songList == null || songList.size() < 1){
            return;
        }
        //获取可以领取任务的用户
        List<User> users = userDao.selectUserByType(0,1,2);
        if(users != null && users.size() > 0){
            for(User user : users){
                List<Song> taskList = new ArrayList<Song>();
                List<TagAuth> tagAuthList  = userDao.selectUserTagAuth(String.valueOf(user.getId()));
                if(songList != null && songList.size() > 0){
                    Integer songCount = 0;
                    Iterator<Song> iterator = songList.iterator();
                    while (iterator.hasNext()){
                        Song song = iterator.next();
                        Integer basicTag = song.getBasicTag();
                        Integer reasonTag = song.getReasonTag();
                        Integer sensibilityTag = song.getSensibilityTag();
                        if(basicTag == null || basicTag == 0){
                            for(TagAuth tagAuth : tagAuthList){
                                if(tagAuth.getName().trim().equals("基础")){
                                    song.setTaskUserId(user.getId());
                                    song.setSongStatus(Song.PULLED);
                                    song.setTaskTime(new Date());
                                    taskList.add(song);
                                    iterator.remove();
                                    songCount ++;
                                    break;
                                }
                            }
                        }
                        else if(reasonTag == null || reasonTag == 0){
                            for(TagAuth tagAuth : tagAuthList){
                                if(tagAuth.getName().trim().equals("理性")){
                                    song.setTaskUserId(user.getId());
                                    song.setSongStatus(Song.PULLED);
                                    song.setTaskTime(new Date());
                                    taskList.add(song);
                                    iterator.remove();
                                    songCount ++;
                                    break;
                                }
                            }
                        }
                        else if(sensibilityTag == null || sensibilityTag == 0){
                            for(TagAuth tagAuth : tagAuthList){
                                if(tagAuth.getName().trim().equals("感性")){
                                    song.setTaskUserId(user.getId());
                                    song.setSongStatus(Song.PULLED);
                                    song.setTaskTime(new Date());
                                    taskList.add(song);
                                    iterator.remove();
                                    songCount ++;
                                    break;
                                }
                            }
                        }
                        if(songCount >= 5)break;
                    }
                    //批量刷新任务
                    songDao.updateSongs(taskList);
                    user.setTaskNumber(user.getTaskNumber() + taskList.size());
                    userDao.updateUserTaskNumber(user);
                }
            }
        }
    }

    @Override
    public void allotAuditTask() {
        //获取待审核的单曲
        List<Song> songList = songDao.selectSongBySongStatus(Song.SUBMITED);
        if(songList == null || songList.size() < 1){
            return;
        }
        //获取所有的管理员
        List<User> users = userDao.selectUserByType(0,1);
        if(users != null && users.size() > 0){
            for(User user : users){
                List<TagAuth> tagAuthList  = userDao.selectUserTagAuth(String.valueOf(user.getId()));
                List<Song> taskList = new ArrayList<Song>();

                if(songList != null && songList.size() > 0){
                    Integer songCount = 0;
                    Iterator<Song> iterator = songList.iterator();
                    while(iterator.hasNext()){
                        Song song = iterator.next();
                        Integer basicTag = song.getBasicTag();
                        Integer reasonTag = song.getReasonTag();
                        Integer sensibilityTag = song.getSensibilityTag();
                        if(basicTag != null && basicTag == 1){
                            for(TagAuth tagAuth : tagAuthList){
                                if(tagAuth.getName().trim().equals("基础")){
                                    song.setAuditUserId(user.getId());
                                    song.setSongStatus(Song.SUBMITPULLED);
                                    taskList.add(song);
                                    iterator.remove();
                                    songCount ++;
                                    break;
                                }
                            }
                        }
                        else if(reasonTag != null || reasonTag == 1){
                            for(TagAuth tagAuth : tagAuthList){
                                if(tagAuth.getName().trim().equals("理性")){
                                    song.setAuditUserId(user.getId());
                                    song.setSongStatus(Song.SUBMITPULLED);
                                    taskList.add(song);
                                    iterator.remove();
                                    songCount ++;
                                    break;
                                }
                            }
                        }
                        else if(sensibilityTag != null || sensibilityTag == 1){
                            for(TagAuth tagAuth : tagAuthList){
                                if(tagAuth.getName().trim().equals("感性")){
                                    song.setAuditUserId(user.getId());
                                    song.setSongStatus(Song.SUBMITPULLED);
                                    taskList.add(song);
                                    iterator.remove();
                                    songCount ++;
                                    break;
                                }
                            }
                        }
                        if(songCount >= 5)break;
                    }
                    //批量刷新任务
                    songDao.updateSongsOfAudit(taskList);
                }
            }
        }
    }



}
