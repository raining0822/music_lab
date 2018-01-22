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
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * Created by mac on 2017/8/23.
 */
@Repository
public class UserDaoImpl extends BaseDao implements UserDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;




    @Override
    public Long insert(final User user, String[] ids) {
        final String sql = "insert into user(email, trueName, tmpPwd, createTime, userType, effect, taskNumber, submitNumber, auditNumber, auditTaskNumber, doneTaskNumber, fatherId) values(?,?,?,?,?,?,?,?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, user.getEmail());
                preparedStatement.setString(2, user.getTrueName());
                preparedStatement.setString(3, user.getTmpPwd());
                preparedStatement.setObject(4, user.getCreateTime());
                preparedStatement.setInt(5, user.getUserType());
                preparedStatement.setInt(6, user.getEffect());
                preparedStatement.setInt(7, user.getTaskNumber());
                preparedStatement.setInt(8, user.getSubmitNumber());
                preparedStatement.setInt(9, user.getAuditNumber());
                preparedStatement.setInt(10, user.getAuditTaskNumber());
                preparedStatement.setInt(11, user.getDoneTaskNumber());
                preparedStatement.setObject(12, user.getFatherId());
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
        String sql = "update user set effect = 0, userPwd = NULL where id = ?;";
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
            Long fatherId = user.getFatherId();
            if(fatherId != null){
                User father = selectById(fatherId);
                if(father != null){
                    user.setFatherEmail(father.getEmail());
                }
            }
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
        String sql = "select * from user order by createTime desc limit ?, ?;";
        RowMapper<User> userRowMapper = new BeanPropertyRowMapper<User>(User.class);
        List<User> userList = jdbcTemplate.query(sql, userRowMapper, (page.getPageNo() - 1) * page.getPageSize(), page.getPageSize());
        for(User user : userList){
            if(user != null){
                Integer userType = user.getUserType();
                if(userType == 1){
                    user.setUserTypeName("管理员");
                }else if(userType == 0){
                    user.setUserTypeName("超级管理员");
                }else if(userType == 2){
                    user.setUserTypeName("音乐分析师");
                }
                user.setTagAuthList(selectUserTagAuth(String.valueOf(user.getId())));
                Long fatherId = user.getFatherId();
                if(fatherId != null){
                    User father = selectById(fatherId);
                    if(father != null){
                        user.setFatherEmail(father.getEmail());
                    }
                }
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
    public User selectByEmailAndUserPwd(String email, String userPwd) {
        String sql = "select * from user where email = ? and userPwd = ?;";
        RowMapper<User> userRowMapper = new BeanPropertyRowMapper<User>(User.class);
        List<User> userList = jdbcTemplate.query(sql, userRowMapper, email, userPwd);
        if(userList != null && userList.size() > 0){
            return userList.get(0);
        }
        return null;
    }

    @Override
    public User selectByEmailAndUserTmpPwd(String email, String userTmpPwd) {
        String sql = "select * from user where email = ? and tmpPwd = ?;";
        RowMapper<User> userRowMapper = new BeanPropertyRowMapper<User>(User.class);
        List<User> userList = jdbcTemplate.query(sql, userRowMapper, email, userTmpPwd);
        if(userList != null && userList.size() > 0){
            return userList.get(0);
        }
        return null;
    }

    @Override
    public void updateUserAuditTaskNumber(List<User> fathers) {
        String sql = "update user set auditTaskNumber = ? where id = ?;";
        List<Object[]> params = new ArrayList<Object[]>();
        for(User u : fathers){
            params.add(new Object[]{u.getAuditTaskNumber(), u.getId()});
        }
        jdbcTemplate.batchUpdate(sql,params);
    }

    @Override
    public void updateUserDoneTaskNumber(User user1) {
        String sql = "update user set doneTaskNumber = ? where id = ?;";
        jdbcTemplate.update(sql, user1.getDoneTaskNumber(), user1.getId());
    }

    @Override
    public Map<String, Object> selectSonMsg(Long id) {
        String sql = "select SUM(taskNumber) as taskNumber, SUM(submitNumber) as submitNumber, SUM(auditNumber) as auditNumber from user where fatherId = ?;";
        return jdbcTemplate.queryForMap(sql, id);
    }

    @Override
    public Integer selectUserDoneCount(String userId) {
        String sql = "SELECT COUNT(songId) FROM song_record WHERE songId IN(SELECT songId FROM song_record WHERE metaData = ?) AND `action` = '4';";
        return jdbcTemplate.queryForObject(sql, Integer.class,userId);
    }

    @Override
    public List<User> selectSonList(Long id) {
        String sql = "select * from user where fatherId = ?;";
        RowMapper<User> userRowMapper = new BeanPropertyRowMapper<User>(User.class);
        return jdbcTemplate.query(sql, userRowMapper, id);
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
        String sql = "update user set userType = ?, trueName = ?, email = ?, fatherId = ? where id = ?;";
        jdbcTemplate.update(sql, user.getUserType(), user.getTrueName(), user.getEmail(), user.getFatherId(), user.getId());
        String sql2 = "delete from r_tag_authority_user where user_id = ?;";
        jdbcTemplate.update(sql2, user.getId());
        String sql3 = "insert into r_tag_authority_user(tag_auth_id, user_id, effect) values(?,?,?);";
        for(String id : ids){
            jdbcTemplate.update(sql3, id, user.getId(), 1);
        }
        return 0;
    }

    @Override
    public List<TagAuth> selectUserTagAuth(String userId){
        String sql = "select ta.id, ta.name from `r_tag_authority_user` as rtau inner join tag_authority as ta on rtau.`tag_auth_id` = ta.`id` where user_id = ? and effect = 1;";
        RowMapper<TagAuth> tagAuthRowMapper = new BeanPropertyRowMapper<TagAuth>(TagAuth.class);
        List<TagAuth> authList = jdbcTemplate.query(sql, tagAuthRowMapper, userId);
        return authList;
    }




    @Override
    public List<User> selectUserByType(int userType) {
        String sql = "select * from user where userType = ? and effect = 1;";
        RowMapper<User> userRowMapper = new BeanPropertyRowMapper<User>(User.class);
        return jdbcTemplate.query(sql,userRowMapper, userType);
    }

    @Override
    public List<User> selectUserByType(int... userType) {
        String sql = "select * from user where 1 = 1 ";
        if(userType != null && userType.length > 0){
            sql += " and userType in ( ";
            for(int type : userType){
                sql += type + ",";
            }
            sql = sql.substring(0,sql.length() - 1);
            sql += " ) and effect = 1;";
        }
        RowMapper<User> userRowMapper = new BeanPropertyRowMapper<User>(User.class);
        return jdbcTemplate.query(sql,userRowMapper);
    }

    @Override
    public void updateUserTaskNumber(User user) {
        String sql = "update user set taskNumber = ? where id = ?;";
        jdbcTemplate.update(sql, user.getTaskNumber(), user.getId());
    }

    @Override
    public void updateUserSubmitNumber(User user) {
        String sql = "update user set submitNumber = ? where id = ?;";
        jdbcTemplate.update(sql, user.getSubmitNumber(), user.getId());
    }

    @Override
    public User findByUserTrueNameOrEmail(String trueName, String email) {
        String sql = "select * from user where 1 = 1 ";
        RowMapper<User> rowMapper = new BeanPropertyRowMapper<User>(User.class);
        List<User> users = null;
        if(StringUtils.hasText(trueName)){
            sql += " AND trueName = ? ;";
            users = jdbcTemplate.query(sql, rowMapper, trueName);
        }
        else if(StringUtils.hasText(email)){
            sql += " AND email = ?;";
            users = jdbcTemplate.query(sql, rowMapper, email);
        }
        if(users != null && users.size() > 0){
            return users.get(0);
        }
        return null;
    }


    @Override
    public void updateUserAuditNumber(User user) {
        String sql = "update user set auditNumber = ? where id = ? ;";
        jdbcTemplate.update(sql, user.getAuditNumber(), user.getId());
    }

}
