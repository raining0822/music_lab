package com.lava.music.dao;

import com.lava.music.model.Song;
import com.lava.music.model.TagAuth;
import com.lava.music.model.User;
import com.lava.music.util.Page;

import java.util.List;

/**
 * Created by mac on 2017/8/23.
 */
public interface UserDao {

    Long insert(User user, String[] ids);
    Integer delById(Long id);
    Integer update(User user);
    User selectById(Long id);
    List<User> selectAll();
    Page<User> selectByPage(Page<User> page);

    Integer selectTotalCount();

    Integer updateUserById(Long id);

    User selectByUserNameAndUserPwd(String userName, String userPwd);

    User selectByUserName(String userName);

    Integer updateLoginTime(Long id);

    Integer updatePwd(Long id, String newPwd);
    Integer updateUser(User user, String[] ids);

    Integer selectUserTaskCount(Long userId);
    List<TagAuth> selectUserTagAuth(String userId);
    Integer insertUserTask(Long userId, List<Song> songList);
}
