package com.lava.music.service.impl;

import com.lava.music.dao.UserDao;
import com.lava.music.model.Song;
import com.lava.music.model.User;
import com.lava.music.service.BaseService;
import com.lava.music.service.UserService;
import com.lava.music.util.CommonUtil;
import com.lava.music.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by mac on 2017/8/23.
 */
@Service
public class UserServiceImpl extends BaseService implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public List<User> findAll() {
        return userDao.selectAll();
    }

    @Override
    public Integer getTotalCount() {
        return userDao.selectTotalCount();
    }

    @Override
    public Page<User> getPage(Page<User> page) {
        return userDao.selectByPage(page);
    }

    @Override
    public void delUser(String userId) {
        Integer result = userDao.delById(Long.valueOf(userId));
    }

    @Override
    public void activeUser(String userId) {
        Integer result = userDao.updateUserById(Long.valueOf(userId));
    }

    @Override
    public void addUser(String userName, Integer userType, String userAuthority) {
        User user = new User();
        user.setUserType(userType);
        user.setEffect(1);
        user.setCreateTime(new Date());
        user.setUserName(userName);
        user.setUserPwd(CommonUtil.MD5("lavaradio"));
        if(StringUtils.hasText(userAuthority)){
            String[] ids = userAuthority.split(",");
            Long userId = userDao.insert(user, ids);
        }
    }

    @Override
    public User login(String userName, String userPwd) {
        userPwd = CommonUtil.MD5(userPwd);
        User user = userDao.selectByUserNameAndUserPwd(userName, userPwd);
        return user;
    }

    @Override
    public User findByUserName(String userName) {
        User user = userDao.selectByUserName(userName);
        return user;
    }

    @Override
    public void updateLoginTime(Long id) {
        Integer result = userDao.updateLoginTime(id);
    }

    @Override
    public void updatePwd(Long id, String newPwd) {
        Integer result = userDao.updatePwd(id, newPwd);
    }

    @Override
    public User findById(String userId) {
        return userDao.selectById(Long.valueOf(userId));
    }

    @Override
    public Integer updateUser(User user, String userAuthority) {
        if(StringUtils.hasText(userAuthority)){
            String[] ids = userAuthority.split(",");
            return userDao.updateUser(user, ids);
        }
        return 0;
    }

    @Override
    public Integer findWorkSongTotalCount(User user) {
        Long userId =  user.getId();
        return userDao.selectUserTaskCount(userId);
    }



}
