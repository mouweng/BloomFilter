package com.mouweng.dao;

import com.mouweng.domain.User;
import com.mouweng.utils.JDBCUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class UserDao {
    private JdbcTemplate template = new JdbcTemplate(JDBCUtils.getDataSource());

    public User findByName(String username) {
        User user = null;
        try {
            // 定义SQL语句
            String sql = "select * from user where name = ?";
            // 执行SQL
            user = template.queryForObject(sql,new BeanPropertyRowMapper<User>(User.class), username);

        } catch (Exception e) {

        }
        return user;
    }

    public User findByUsername(String username) {
        User user = null;
        try {
            // 定义SQL语句
            String sql = "select * from tab_user where username = ?";
            // 执行SQL
            user = template.queryForObject(sql,new BeanPropertyRowMapper<User>(User.class),username);

        } catch (Exception e) {

        }
        return user;
    }

    public User findByUid(Integer id) {
        User user = null;
        try {
            // 定义SQL语句
            String sql = "select * from tab_user where uid = ?";
            // 执行SQL
            user = template.queryForObject(sql,new BeanPropertyRowMapper<User>(User.class),id);

        } catch (Exception e) {

        }
        return user;
    }

    public List<User> findAll() {
        List<User> users = null;
        try {
            // 定义SQL语句
            String sql = "select * from tab_user";
            // 执行SQL
            users = template.query(sql,new BeanPropertyRowMapper<User>(User.class));

        } catch (Exception e) {

        }
        return users;
    }

}
