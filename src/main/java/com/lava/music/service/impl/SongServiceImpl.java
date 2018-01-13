package com.lava.music.service.impl;

import com.lava.music.dao.*;
import com.lava.music.model.*;
import com.lava.music.service.BaseService;
import com.lava.music.service.SongService;
import com.lava.music.util.LabelUtil;
import com.lava.music.util.Page;
import javassist.bytecode.LineNumberAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.*;
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
        List<Song> songList = songDao.selectSongBySongStatus(Song.AUDITED);
        List<Song> taskList = new ArrayList<Song>();
        if(songList != null && songList.size() > 0){
            for(Song song : songList){
                List<SongRecord> songRecordList = songRecordDao.selectSongRecord(song.getId(), Song.AUDITED);
                if(songRecordList != null && songRecordList.size() > 0){
                    SongRecord songRecord = songRecordList.get(0);
                    Long createTime = songRecord.getCreateTime().getTime();
                    Long currentTime = System.currentTimeMillis();
                    if((currentTime - createTime) > 1000*60*60*24*3){
                        song.setSongStatus(Song.BACK);
                        taskList.add(song);
                    }
                }
            }
            songDao.back(taskList);
        }

    }

    @Override
    public void initSongTag() {
        List<Song> songList = songDao.selectAll();
        if(songList != null && songList.size() > 0){
            List<Song> taskList = new ArrayList<Song>();
            int i = 0;
            System.out.println("共" + taskList.size() + "首单曲");
            for(Song song : songList){
                System.out.println("正在处理第" + ++i + "首单曲");
                Song backSong = flushSongsTag(song.getId());
                if(backSong != null){
                    taskList.add(backSong);
                }
            }
            System.out.println("开始批量处理结果！");
            songDao.flushSongTag(taskList);
            System.out.println("处理完成！");
        }
    }

    @Override
    public void pushDIYSong() {
        String filePath = "D://work/first.csv";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            String tmp = "";
            List<Song> taskList = new ArrayList<Song>();
            List<SongRecord> songRecordList = new ArrayList<SongRecord>();
            while((tmp = reader.readLine()) != null){
                System.out.println("获取到艺人关键词：" + tmp);
                List<Song> songList = songDao.selectByArtistName(tmp);
                System.out.println("共找到相关歌曲：" + songList.size() + "首");
                if(songList != null && songList.size() > 0){
                    int i = 0;
                    for(Song song : songList){
                        System.out.println("开始处理第" + ++i + "首 songId : " + song.getId());
                        Integer basicTag = song.getBasicTag();
                        Integer reasonTag = song.getReasonTag();
                        Integer senTag = song.getSensibilityTag();
                        Boolean flag = false;
                        if(basicTag == 1 && !flag) flag = true;
                        if(reasonTag == 1 && !flag) flag = true;
                        if(senTag == 1 && !flag) flag = true;
                        if(flag){
                            System.out.println("检测到已经有标签" + song.getId());
                        }
                        if(!flag){
                            System.out.println("符合推送标准" + song.getId());
                            taskList.add(song);
                            //添加日志
                            SongRecord songRecord = new SongRecord();
                            songRecord.setAction(SongRecord.PUSH_SONG);
                            songRecord.setCreateTime(new Date());
                            songRecord.setSongId(song.getId());
                            songRecord.setUserId(-1L);
                            songRecordList.add(songRecord);
                        }
                    }
                }
            }
            System.out.println("开始进行批量处理！");
            //批量推送单曲
            songDao.updateSongStatus(taskList, Song.PUSHED);
            System.out.println("推送结束！");
            //批量添加日志
            songRecordDao.insert(songRecordList);
            System.out.println("日志记录结束！");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            flushSongTag(Long.valueOf(songId));
        }
    }

    private Song flushSongsTag(Long songId){
        //刷新单曲的标签权限标识
        //songDao.updateSongTagFlag(songId);
        List<Label> songNewLabels = labelDao.selectLabelBySongId(String.valueOf(songId));
        if(songNewLabels != null && songNewLabels.size() > 0){
            Song song = new Song();
            song.setId(songId);
            song.setBasicTag(0);
            song.setReasonTag(0);
            song.setSensibilityTag(0);
            boolean basicFlag = false;
            boolean reasonFlag = false;
            boolean sensFlag = false;
            for(Label label : songNewLabels){
                String labelNo = label.getLabelNo();
                if(labelNo.startsWith("000001") && !basicFlag){
                    song.setBasicTag(1);
                    basicFlag = true;
                }
                else if(labelNo.startsWith("000002") && !reasonFlag){
                    song.setReasonTag(1);
                    reasonFlag = true;
                }
                else if(labelNo.startsWith("000003") && !sensFlag){
                    song.setSensibilityTag(1);
                    sensFlag = true;
                }
            }
            return song;
        }
        return null;
    }

    private void flushSongTag(Long songId){
        //刷新单曲的标签权限标识
        songDao.updateSongTagFlag(songId);
        List<Label> songNewLabels = labelDao.selectLabelBySongId(String.valueOf(songId));
        boolean basicFlag = false;
        boolean reasonFlag = false;
        boolean sensFlag = false;
        for(Label label : songNewLabels){
            String labelNo = label.getLabelNo();
            if(labelNo.startsWith("000001") && !basicFlag){
                Song song = new Song();
                song.setId(songId);
                song.setBasicTag(1);
                songDao.updateSongTagFlag(song);
                basicFlag = true;
            }
            else if(labelNo.startsWith("000002") && !reasonFlag){
                Song song = new Song();
                song.setId(songId);
                song.setReasonTag(1);
                songDao.updateSongTagFlag(song);
                reasonFlag = true;
            }
            else if(labelNo.startsWith("000003") && !sensFlag){
                Song song = new Song();
                song.setId(songId);
                song.setSensibilityTag(1);
                songDao.updateSongTagFlag(song);
                sensFlag = true;
            }
        }
    }

    @Override
    public void auditLabels(String songId, String labelIds, Long userId) {
        Song auditSong = songDao.selectById(Long.valueOf(songId));
        User user = userDao.selectById(auditSong.getTaskUserId());
        //获取单曲下的标签
        List<Label> songLabels = labelDao.selectLabelBySongId(songId);
        List<Label> songNewLabels = null;
        //如果标签与原有一直，则不做任何操作
        Boolean checkResult = LabelUtil.checkTag(songLabels, labelIds.split(","));
        if(checkResult){
            //记录该单曲打标签用户的正确率
            user.setAuditNumber(user.getAuditNumber() + 1);
            userDao.updateUserAuditNumber(user);
            auditSong.setAuditResult("OK");
        }else{
            songDao.addLabels(Long.valueOf(songId), labelIds);
            flushSongTag(Long.valueOf(songId));
            auditSong.setAuditResult("PROBLEM");
        }
        songNewLabels = labelDao.selectLabelBySongId(songId);
        //修改单曲状态为审核已通过
        auditSong.setSongStatus(Song.AUDITED);
        songDao.updateSongStatus(auditSong);
        //添加审核的日志
        SongRecord songRecord = new SongRecord();
        songRecord.setAction(SongRecord.AUDIT_SONG);
        songRecord.setCreateTime(new Date());
        songRecord.setSongId(Long.valueOf(songId));
        songRecord.setUserId(userId);
        //songRecord.setMetaData(auditSong.getAuditResult() + "|" + auditSong.getTaskUserId());
        StringBuffer stringBuffer = new StringBuffer();
        for(Label label : songNewLabels){
            stringBuffer.append(label.getId()).append(",");
        }
        stringBuffer.append("|");
        for(Label label : songNewLabels){
            stringBuffer.append(label.getLabelName()).append(",");
        }
        songRecord.setMetaData(stringBuffer.toString());
        songRecordDao.insert(songRecord);

        //添加日志
        UserRecord userRecord = new UserRecord();
        userRecord.setAction(UserRecord.SONG_AUDIT_LABEL);
        userRecord.setCreateTime(new Date());
        userRecord.setUserId(userId);
        userRecord.setSourceData(songId + "|" + labelIds);
        userRecordDao.insert(userRecord);
        //更新用户的审核任务数量
        User user1 = userDao.selectById(userId);
        if(user1.getDoneTaskNumber() == null){
            user1.setDoneTaskNumber(0);
        }
        user1.setDoneTaskNumber(user1.getDoneTaskNumber() + 1);
        userDao.updateUserDoneTaskNumber(user1);
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
            flushSongTag(Long.valueOf(songId));
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
        List<SongRecord> songRecordList = new ArrayList<SongRecord>();
        //获取可以领取任务的用户
        List<User> users = userDao.selectUserByType(2);
        if(users != null && users.size() > 0){
            for(User user : users){
                List<Song> taskList = new ArrayList<Song>();
                List<TagAuth> tagAuthList  = userDao.selectUserTagAuth(String.valueOf(user.getId()));
                if(songList != null && songList.size() > 0){
                    Integer songCount = 0;
                    Iterator<Song> iterator = songList.iterator();
                    while (iterator.hasNext()){
                        Song song = iterator.next();
                        SongRecord songRecord = new SongRecord();
                        Integer basicTag = song.getBasicTag();
                        Integer reasonTag = song.getReasonTag();
                        Integer sensibilityTag = song.getSensibilityTag();
                        Boolean flag = false;
                        if((basicTag == null || basicTag == 0) && !flag){
                            for(TagAuth tagAuth : tagAuthList){
                                if(tagAuth.getName().trim().equals("基础")){
                                    song.setTaskUserId(user.getId());
                                    song.setSongStatus(Song.PULLED);
                                    song.setTaskTime(new Date());
                                    taskList.add(song);
                                    songRecord.setAction(SongRecord.TASK_SONG);
                                    songRecord.setUserId(-1L);
                                    songRecord.setMetaData(String.valueOf(user.getId()));
                                    songRecord.setSongId(song.getId());
                                    songRecord.setCreateTime(new Date());
                                    songRecordList.add(songRecord);
                                    iterator.remove();
                                    songCount ++;
                                    flag = true;
                                    break;
                                }
                            }
                        }
                        if((reasonTag == null || reasonTag == 0) && !flag){
                            for(TagAuth tagAuth : tagAuthList){
                                if(tagAuth.getName().trim().equals("理性")){
                                    song.setTaskUserId(user.getId());
                                    song.setSongStatus(Song.PULLED);
                                    song.setTaskTime(new Date());
                                    taskList.add(song);
                                    songRecord.setAction(SongRecord.TASK_SONG);
                                    songRecord.setUserId(-1L);
                                    songRecord.setMetaData(String.valueOf(user.getId()));
                                    songRecord.setSongId(song.getId());
                                    songRecord.setCreateTime(new Date());
                                    songRecordList.add(songRecord);
                                    iterator.remove();
                                    songCount ++;
                                    flag = true;
                                    break;
                                }
                            }
                        }
                        if((sensibilityTag == null || sensibilityTag == 0) && !flag){
                            for(TagAuth tagAuth : tagAuthList){
                                if(tagAuth.getName().trim().equals("感性")){
                                    song.setTaskUserId(user.getId());
                                    song.setSongStatus(Song.PULLED);
                                    song.setTaskTime(new Date());
                                    taskList.add(song);
                                    songRecord.setAction(SongRecord.TASK_SONG);
                                    songRecord.setUserId(-1L);
                                    songRecord.setMetaData(String.valueOf(user.getId()));
                                    songRecord.setSongId(song.getId());
                                    songRecord.setCreateTime(new Date());
                                    songRecordList.add(songRecord);
                                    iterator.remove();
                                    songCount ++;
                                    flag = true;
                                    break;
                                }
                            }
                        }
                        if(songCount >= 75)break;
                    }
                    //批量刷新任务
                    songDao.updateSongs(taskList);
                    user.setTaskNumber(user.getTaskNumber() + taskList.size());
                    userDao.updateUserTaskNumber(user);
                }
            }
        }
        //批量添加日志
        songRecordDao.insert(songRecordList);
    }

    private User check(List<User> list, User user){
        for(User u : list){
            if(u.getId() == user.getId()){
                return u;
            }
        }
        return null;
    }

    @Override
    public void allotAuditTask() {
        //获取待审核的单曲
        List<Song> songList = songDao.selectSongBySongStatus(Song.SUBMITED);
        //获取所有的管理员
        List<User> users = userDao.selectUserByType(1);
        if(songList == null || songList.size() < 1){
            return;
        }
        if(users == null || users.size() < 1){
            return;
        }
        List<SongRecord> songRecordList = new ArrayList<SongRecord>();
        //处理主子账户的任务推送
        //找到符合有主账户条件的任务集合
        List<Song> fatherSong = new ArrayList<Song>();
        //记录操作的主账户信息
        List<User> fathers = new ArrayList<User>();
        Iterator<Song> songIterator = songList.iterator();
        while(songIterator.hasNext()){
            Song song = songIterator.next();
            SongRecord songRecord = new SongRecord();
            Long userId = song.getTaskUserId();
            User user = userDao.selectById(userId);
            //如果一个任务的提交者，有主账户，将此任务推送给主账户
            Long fatherId = user.getFatherId();
            if(fatherId != null){
                User father = userDao.selectById(fatherId);
                song.setAuditUserId(fatherId);
                song.setSongStatus(Song.SUBMITPULLED);
                fatherSong.add(song);
                songRecord.setAction(SongRecord.AUDIT_TASK_SONG);
                songRecord.setCreateTime(new Date());
                songRecord.setSongId(song.getId());
                songRecord.setUserId(-1L);
                songRecord.setMetaData(String.valueOf(father.getId()));
                songRecordList.add(songRecord);
                //将任务从集合中移除
                songIterator.remove();
                User user1 = check(fathers, father);
                if(user1 != null){
                    user1.setAuditTaskNumber(user1.getAuditTaskNumber() + 1);
                }else{
                    father.setAuditTaskNumber(1);
                    fathers.add(father);
                }
            }
        }
        //批量刷新任务
        songDao.updateSongsOfAudit(fatherSong);
        //批量更新主账户信息
        userDao.updateUserAuditTaskNumber(fathers);

        //处理其它任务
        if(users != null && users.size() > 0){
            for(User user : users){
                List<User> sonList = userDao.selectSonList(user.getId());
                if(sonList != null && sonList.size() < 1){
                    List<TagAuth> tagAuthList  = userDao.selectUserTagAuth(String.valueOf(user.getId()));
                    List<Song> taskList = new ArrayList<Song>();

                    if(songList != null && songList.size() > 0){
                        Integer songCount = 0;
                        Iterator<Song> iterator = songList.iterator();
                        while(iterator.hasNext()){
                            Song song = iterator.next();
                            SongRecord songRecord = new SongRecord();
                            Integer basicTag = song.getBasicTag();
                            Integer reasonTag = song.getReasonTag();
                            Integer sensibilityTag = song.getSensibilityTag();
                            Boolean flag = false;
                            if((basicTag == 1) && !flag){
                                for(TagAuth tagAuth : tagAuthList){
                                    if(tagAuth.getName().trim().equals("基础")){
                                        song.setAuditUserId(user.getId());
                                        song.setSongStatus(Song.SUBMITPULLED);
                                        taskList.add(song);
                                        songRecord.setAction(SongRecord.AUDIT_TASK_SONG);
                                        songRecord.setCreateTime(new Date());
                                        songRecord.setSongId(song.getId());
                                        songRecord.setUserId(-1L);
                                        songRecord.setMetaData(String.valueOf(user.getId()));
                                        songRecordList.add(songRecord);
                                        iterator.remove();
                                        songCount ++;
                                        flag = true;
                                        break;
                                    }
                                }
                            }
                            if((reasonTag == 1) && !flag){
                                for(TagAuth tagAuth : tagAuthList){
                                    if(tagAuth.getName().trim().equals("理性")){
                                        song.setAuditUserId(user.getId());
                                        song.setSongStatus(Song.SUBMITPULLED);
                                        taskList.add(song);
                                        songRecord.setAction(SongRecord.AUDIT_TASK_SONG);
                                        songRecord.setCreateTime(new Date());
                                        songRecord.setSongId(song.getId());
                                        songRecord.setUserId(-1L);
                                        songRecord.setMetaData(String.valueOf(user.getId()));
                                        songRecordList.add(songRecord);
                                        iterator.remove();
                                        songCount ++;
                                        flag = true;
                                        break;
                                    }
                                }
                            }
                            if((sensibilityTag == 1) && !flag){
                                for(TagAuth tagAuth : tagAuthList){
                                    if(tagAuth.getName().trim().equals("感性")){
                                        song.setAuditUserId(user.getId());
                                        song.setSongStatus(Song.SUBMITPULLED);
                                        taskList.add(song);
                                        songRecord.setAction(SongRecord.AUDIT_TASK_SONG);
                                        songRecord.setCreateTime(new Date());
                                        songRecord.setSongId(song.getId());
                                        songRecord.setUserId(-1L);
                                        songRecord.setMetaData(String.valueOf(user.getId()));
                                        songRecordList.add(songRecord);
                                        iterator.remove();
                                        songCount ++;
                                        flag = true;
                                        break;
                                    }
                                }
                            }
                            //if(songCount >= 5)break;
                        }
                        //批量刷新任务
                        songDao.updateSongsOfAudit(taskList);
                        //更新审核任务领取数量
                        List<User> users1 = new ArrayList<User>();
                        user.setAuditTaskNumber(user.getAuditTaskNumber() + taskList.size());
                        users1.add(user);
                        userDao.updateUserAuditTaskNumber(users1);
                    }
                }
            }
        }
        //批量添加日志
        songRecordDao.insert(songRecordList);
    }



}
