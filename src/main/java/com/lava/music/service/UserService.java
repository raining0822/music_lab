package com.lava.music.service;

import com.lava.music.model.Label;
import com.lava.music.model.Song;
import com.lava.music.model.TagAuth;
import com.lava.music.model.User;
import com.lava.music.util.Page;

import java.util.List;
import java.util.Map;

/**
 * Created by mac on 2017/8/23.
 */
public interface UserService {
    List<User> findAll();

    Integer getTotalCount();

    Page<User> getPage(Page<User> page);

    void delUser(String userId);

    void activeUser(String userId);

    void addUser(String userName, Integer userType, String userAuthority);

    User tmpLogin(String userName, String userPwd);
    User login(String userName, String userPwd);

    User findByUserName(String userName);

    void updateLoginTime(Long id);

    void updatePwd(Long id, String s);

    User findById(String userId);

    Integer updateUser(User user, String userAuthority);

    List<TagAuth> findUserTagAuth(Long userId);

    boolean checkSongByUser(Long userId, Long songId);

    List<Song> findUserLabelTask(Long userId);
    List<Song> findUserSubmitTask(Long userId);

    List<Song> findUserAuditTask(Long userId);
    List<Song> findUserDoneTask(Long userId);

    Integer submit(Long userId, Long songId, List<Label> songLabels);

    User findByUserTrueNameOrEmail(String trueName, String email);

    User addUser(String email, String trueName, Long fatherId, Integer userType, String userAuthority);

    Map<String,Object> findSonMsg(Long id);

    User findUserMsg(String userId);
}
