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

    User selectByEmailAndUserPwd(String userName, String userPwd);

    User selectByUserName(String userName);

    Integer updateLoginTime(Long id);

    Integer updatePwd(Long id, String newPwd);
    Integer updateUser(User user, String[] ids);

    List<TagAuth> selectUserTagAuth(String userId);

    void updateUserAuditNumber(User user);

    List<User> selectUserByType(int userType);
    List<User> selectUserByType(int... userType);

    void updateUserTaskNumber(User user);

    void updateUserSubmitNumber(User user);

    User findByUserTrueNameOrEmail(String trueName, String email);
    User selectByEmailAndUserTmpPwd(String email, String userTmpPwd);

    void updateUserAuditTaskNumber(List<User> fathers);

    void updateUserDoneTaskNumber(User user1);
}
