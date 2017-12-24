package com.lava.music.service;

import com.lava.music.model.Song;
import com.lava.music.model.TagAuth;
import com.lava.music.model.User;
import com.lava.music.util.Page;

import java.util.List;

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

    User login(String userName, String userPwd);

    User findByUserName(String userName);

    void updateLoginTime(Long id);

    void updatePwd(Long id, String s);

    User findById(String userId);

    Integer updateUser(User user, String userAuthority);

    Integer findWorkSongTotalCount(User user);


    List<TagAuth> findUserTagAuth(Long userId);
}
