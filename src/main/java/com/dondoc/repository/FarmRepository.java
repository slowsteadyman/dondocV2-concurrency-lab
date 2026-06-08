package com.dondoc.repository;

import com.dondoc.entity.Farm;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class FarmRepository {

    private final JdbcTemplate jdbcTemplate;

    public FarmRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Farm> findAll(){
        String sql = "SELECT * FROM farms";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Farm(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getObject("created_at", LocalDateTime.class)
        ));
    }

    public void save(Farm farm) {
        String sql = "INSERT INTO farms (name, created_at) VALUES (?, ?)";
        jdbcTemplate.update(sql, farm.getName(), farm.getCreatedAt());
    }

    public boolean existsByName(String name) {
        String sql = "SELECT COUNT(*) FROM farms WHERE name = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, name);
        return count != null && count > 0;
    }

    public Long saveAndReturnId(Farm farm) {
        String sql = "INSERT INTO farms (name, created_at) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        PreparedStatementCreator statementCreator = connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, farm.getName());
            ps.setObject(2, farm.getCreatedAt());
            return ps;
        };

        jdbcTemplate.update(statementCreator, keyHolder);
        return keyHolder.getKey().longValue();
    }
}
