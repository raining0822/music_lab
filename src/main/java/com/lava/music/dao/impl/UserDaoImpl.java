package com.lava.music.dao.impl;

import com.lava.music.dao.BaseDao;
import com.lava.music.dao.UserDao;
import com.lava.music.model.Song;
import com.lava.music.model.TagAuth;
import com.lava.music.model.User;
import com.lava.music.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mac on 2017/8/23.
 */
@Repository
public class UserDaoImpl extends BaseDao implements UserDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Long insert(final User user, String[] ids) {
        final String sql = "insert into user(userName, userPwd, createTime, userType, effect) values(?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, user.getUserName());
                preparedStatement.setString(2, user.getUserPwd());
                preparedStatement.setObject(3, user.getCreateTime());
                preparedStatement.setInt(4, user.getUserType());
                preparedStatement.setInt(5, user.getEffect());
                return preparedStatement;
            }
        }, keyHolder);
        Long userId = keyHolder.getKey().longValue();
        String sql2 = "insert into r_tag_authority_user(tag_auth_id, user_id, effect) values(?,?,?);";
        for(String tagAuthId : ids){
            jdbcTemplate.update(sql2, tagAuthId, userId, 1);
        }
        return userId;
    }


    @Override
    public Integer delById(Long id) {
        String sql = "update user set effect = 0 where id = ?;";
        return jdbcTemplate.update(sql, id);
    }

    @Override
    public Integer update(User user) {
        return null;
    }

    @Override
    public User selectById(Long id) {
        String sql = "select * from user where id = ?;";
        RowMapper<User> userRowMapper = new BeanPropertyRowMapper<User>(User.class);
        List<User> userList = jdbcTemplate.query(sql, userRowMapper, id);
        if(userList != null && userList.size() > 0){
            User user = userList.get(0);
            user.setTagAuthList(selectUserTagAuth(String.valueOf(user.getId())));
            return user;
        }
        return null;
    }

    @Override
    public List<User> selectAll() {
        String sql = "select * from user;";
        RowMapper<User> userRowMapper = new BeanPropertyRowMapper<User>(User.class);
        return jdbcTemplate.query(sql, userRowMapper);
    }

    @Override
    public Page<User> selectByPage(Page<User> page) {
        String sql = "select * from user order by createTime desc limit ?, ?";
        RowMapper<User> userRowMapper = new BeanPropertyRowMapper<User>(User.class);
        List<User> userList = jdbcTemplate.query(sql, userRowMapper, (page.getPageNo() - 1) * page.getPageSize(), page.getPageSize());
        for(User user : userList){
            if(user != null){
                Integer userType = user.getUserType();
                if(userType == 1){
                    user.setUserTypeName("专业的耳朵");
                }else if(userType == 0){
                    user.setUserTypeName("管理员");
                }else if(userType == 2){
                    user.setUserTypeName("超级管理员");
                }
                user.setTagAuthList(selectUserTagAuth(String.valueOf(user.getId())));
            }
        }
        page.setList(userList);
        return page;
    }

    @Override
    public Integer selectTotalCount() {
        String sql = "select count(0) from user;";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    @Override
    public Integer updateUserById(Long id) {
        String sql = "update user set effect = 1 where id = ?;";
        return jdbcTemplate.update(sql, id);
    }

    @Override
    public User selectByUserNameAndUserPwd(String userName, String userPwd) {
        String sql = "select * from user where userName = ? and userPwd = ?;";
        RowMapper<User> userRowMapper = new BeanPropertyRowMapper<User>(User.class);
        List<User> userList = jdbcTemplate.query(sql, userRowMapper, userName, userPwd);
        if(userList != null && userList.size() > 0){
            return userList.get(0);
        }
        return null;
    }

    @Override
    public User selectByUserName(String userName) {
        String sql = "select * from user where userName = ?;";
        RowMapper<User> userRowMapper = new BeanPropertyRowMapper<User>(User.class);
        List<User> userList = jdbcTemplate.query(sql, userRowMapper, userName);
        if(userList != null && userList.size() > 0 ){
            return userList.get(0);
        }
        return null;
    }

    @Override
    public Integer updateLoginTime(Long id) {
        String sql = "update user set lastLoginTime = ? where id = ?;";
        return jdbcTemplate.update(sql, new Date(), id);
    }

    @Override
    public Integer updatePwd(Long id, String newPwd) {
        String sql = "update user set userPwd = ? where id = ?;";
        return jdbcTemplate.update(sql, newPwd, id);
    }

    @Override
    public Integer updateUser(User user, String[] ids) {
        String sql = "update user set userType = ? where id = ?;";
        jdbcTemplate.update(sql, user.getUserType(), user.getId());
        String sql2 = "delete from r_tag_authority_user where user_id = ?;";
        jdbcTemplate.update(sql2, user.getId());
        String sql3 = "insert into r_tag_authority_user(tag_auth_id, user_id, effect) values(?,?,?);";
        for(String id : ids){
            jdbcTemplate.update(sql3, id, user.getId(), 1);
        }
        return 0;
    }

    @Override
    public Integer selectUserTaskCount(Long userId) {
        String sql = "select count(0) from user_task where userId = ?;";
        return jdbcTemplate.queryForObject(sql, Integer.class, userId);
    }
    @Override
    public List<TagAuth> selectUserTagAuth(String userId){
        String sql = "select ta.id, ta.name from `r_tag_authority_user` as rtau inner join tag_authority as ta on rtau.`tag_auth_id` = ta.`id` where user_id = ? and effect = 1;";
        RowMapper<TagAuth> tagAuthRowMapper = new BeanPropertyRowMapper<TagAuth>(TagAuth.class);
        List<TagAuth> authList = jdbcTemplate.query(sql, tagAuthRowMapper, userId);
        return authList;
    }

    /**
     * 批量插入用户任务
     * @param userId
     * @param
     * @return
     */
    public Integer insertUserTask(Long userId, List<Song> songList){
       List<Object[]> params = new ArrayList<Object[]>();
       for(Song song : songList){
           params.add(new Object[]{userId, song.getId()});
       }
       String sql = "insert into user_task(userId, songId)values(?,?);";
       int[] number = jdbcTemplate.batchUpdate(sql, params);
       return number.length;
    }
}
