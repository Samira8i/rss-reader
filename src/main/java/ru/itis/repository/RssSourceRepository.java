package ru.itis.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.itis.model.RssSource;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class RssSourceRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<RssSource> sourceRowMapper = (rs, rowNum) -> {
        RssSource source = new RssSource();
        source.setId(rs.getLong("id"));
        source.setUserId(rs.getLong("user_id"));
        source.setName(rs.getString("name"));
        source.setUrl(rs.getString("url"));
        source.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        Timestamp lastCheckedAt = rs.getTimestamp("last_checked_at");
        if (lastCheckedAt != null) {
            source.setLastCheckedAt(lastCheckedAt.toLocalDateTime());
        }

        return source;
    };

    public RssSourceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public List<RssSource> findByUserId(Long userId) {
        String sql = "SELECT id, user_id, name, url, created_at, last_checked_at " +
                "FROM rss_sources WHERE user_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, sourceRowMapper, userId);
    }

    public Optional<RssSource> findById(Long id) {
        String sql = "SELECT id, user_id, name, url, created_at, last_checked_at " +
                "FROM rss_sources WHERE id = ?";
        try {
            RssSource source = jdbcTemplate.queryForObject(sql, sourceRowMapper, id);
            return Optional.ofNullable(source);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public RssSource save(RssSource source) {
        String sql = "INSERT INTO rss_sources (user_id, name, url, created_at, last_checked_at) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, source.getUserId());
            ps.setString(2, source.getName());
            ps.setString(3, source.getUrl());
            ps.setTimestamp(4, Timestamp.valueOf(source.getCreatedAt()));
            ps.setTimestamp(5, source.getLastCheckedAt() != null
                    ? Timestamp.valueOf(source.getLastCheckedAt())
                    : null);
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            source.setId(key.longValue());
            System.out.println("Generated ID: " + source.getId());
        } else {
            System.out.println("No generated ID found!");
        }

        return source;
    }


    public void updateLastCheckedAt(Long id, LocalDateTime lastCheckedAt) {
        String sql = "UPDATE rss_sources SET last_checked_at = ? WHERE id = ?";
        jdbcTemplate.update(sql, Timestamp.valueOf(lastCheckedAt), id);
    }

    public boolean existsByUserIdAndUrl(Long userId, String url) {
        String sql = "SELECT COUNT(*) FROM rss_sources WHERE user_id = ? AND url = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, url);
        return count != null && count > 0;
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM rss_sources WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

}