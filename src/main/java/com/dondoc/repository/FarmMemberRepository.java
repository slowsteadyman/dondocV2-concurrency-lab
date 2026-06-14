package com.dondoc.repository;

import com.dondoc.entity.FarmMember;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public class FarmMemberRepository {

    private final JdbcTemplate jdbcTemplate;

    public FarmMemberRepository(JdbcTemplate jdbcTemplate) {this.jdbcTemplate = jdbcTemplate;}

    public List<FarmMember> findAll(){
        String sql = "SELECT * FROM farm_members";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new FarmMember(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getLong("farm_id"),
                rs.getObject("joined_at", LocalDateTime.class)
        ));
    }

    public void save(FarmMember farmMember) {
        String sql = "INSERT INTO farm_members (user_id, farm_id, joined_at) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, farmMember.getUserId(), farmMember.getFarmId(), farmMember.getJoinedAt());
    }

    public Map<Long, Integer> findMemberCount() {
        Map<Long, Integer> result = new HashMap<>();
        String sql = "SELECT farm_id, count(*) member_count FROM farm_members GROUP BY farm_id";
        jdbcTemplate.query(sql, rs -> {
            result.put(rs.getLong("farm_id"), rs.getInt("member_count"));
        });
        return result;
    }

    public List<Long> findJoinedFarmIdsByUserId(Long userId) {
        String sql = "SELECT farm_id FROM farm_members WHERE user_id = ?";
        return jdbcTemplate.queryForList(sql, Long.class, userId);
    }
}
