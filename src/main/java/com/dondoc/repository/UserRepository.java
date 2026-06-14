package com.dondoc.repository;


import com.dondoc.entity.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate; // MySQL에 쿼리를 날리는 기능을 가지고 있음.

    public UserRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> findAll(){
        String sql = "SELECT * FROM users"; //rs는 DB에서 온 한 행의 데이터 전체를 가지고 있다.
        return jdbcTemplate.query(sql, (rs,rowNum) -> new User(  // .query 메서드 : 첫 번째 인자-날릴 쿼리문, 두 번째 인자-결과를 어떤 객체로 바꿀지 정의
                rs.getLong("id"),
                rs.getString("user_id"),
                rs.getString("user_password"),
                rs.getString("name"),
                rs.getInt("age"),
                rs.getInt("current_pig_level"),
                rs.getInt("current_house_level"),
                rs.getInt("current_character_level"),
                rs.getLong("monthly_income"),
                rs.getInt("target_expense_ratio"),
                rs.getObject("created_at", LocalDateTime.class)
        ));
    }

    public void save(User user){
        String sql = "INSERT INTO users (user_id, user_password, name, age, current_pig_level, current_house_level, current_character_level, monthly_income, target_expense_ratio, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                user.getUserId(),
                user.getUserPassword(),
                user.getName(),
                user.getAge(),
                user.getCurrentPigLevel(),
                user.getCurrentHouseLevel(),
                user.getCurrentCharacterLevel(),
                user.getMonthlyIncome(),
                user.getTargetExpenseRatio(),
                user.getCreatedAt());
    }

    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> new User(
                rs.getLong("id"),
                rs.getString("user_id"),
                rs.getString("user_password"),
                rs.getString("name"),
                rs.getInt("age"),
                rs.getInt("current_pig_level"),
                rs.getInt("current_house_level"),
                rs.getInt("current_character_level"),
                rs.getLong("monthly_income"),
                rs.getInt("target_expense_ratio"),
                rs.getObject("created_at", LocalDateTime.class)
        ), id);
        return users.stream().findFirst();
    }
}