package ru.itis.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.itis.model.Session;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SessionRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Session> sessionRowMapper = (rs, rowNum) -> {
        Session session = new Session();
        session.setId(rs.getLong("id"));
        session.setUserId(rs.getLong("user_id"));
        session.setSessionId(rs.getString("session_id"));
        session.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return session;
    };

    public SessionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Session createSession(Long userId) {
        String sessionId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        String sql = "INSERT INTO sessions (user_id, session_id, created_at) VALUES (?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, userId);
            ps.setString(2, sessionId);
            ps.setTimestamp(3, Timestamp.valueOf(now));
            return ps;
        }, keyHolder);

        Session session = new Session();
        session.setUserId(userId);
        session.setSessionId(sessionId);
        session.setCreatedAt(now);

        if (keyHolder.getKeys() != null) {
            Map<String, Object> keys = keyHolder.getKeys();
            if (keys.containsKey("id")) {
                session.setId(((Number) keys.get("id")).longValue());
            }
        }

        return session;
    }

    public Optional<Session> findBySessionId(String sessionId) {
        String sql = "SELECT id, user_id, session_id, created_at FROM sessions WHERE session_id = ?";
        try {
            Session session = jdbcTemplate.queryForObject(sql, sessionRowMapper, sessionId);
            return Optional.ofNullable(session);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
    public Optional<Session> findByUserId(Long userId) {
        String sql = "SELECT id, user_id, session_id, created_at FROM sessions WHERE user_id = ? ORDER BY created_at DESC LIMIT 1";
        try {
            Session session = jdbcTemplate.queryForObject(sql, sessionRowMapper, userId);
            return Optional.ofNullable(session);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
    public void deleteSession(String sessionId) {
        String sql = "DELETE FROM sessions WHERE session_id = ?";
        jdbcTemplate.update(sql, sessionId);
    }
}