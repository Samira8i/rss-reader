package ru.itis.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.itis.model.Post;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class PostRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Post> postRowMapper = (rs, rowNum) -> {
        Post post = new Post();
        post.setId(rs.getLong("id"));
        post.setSourceId(rs.getLong("source_id"));
        post.setTitle(rs.getString("title"));
        post.setDescription(rs.getString("description"));
        post.setLink(rs.getString("link"));
        post.setGuid(rs.getString("guid"));

        Timestamp publishedAt = rs.getTimestamp("published_at");
        if (publishedAt != null) {
            post.setPublishedAt(publishedAt.toLocalDateTime());
        }

        post.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        try {
            post.setRead(rs.getBoolean("is_read"));
        } catch (Exception e) {
            post.setRead(false);
        }

        return post;
    };

    public PostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Post> findByUserId(Long userId, int limit, int offset) {
        String sql = "SELECT p.id, p.source_id, p.title, p.description, p.link, p.guid, " +
                "p.published_at, p.created_at, p.is_read " +
                "FROM posts p " +
                "JOIN rss_sources rs ON p.source_id = rs.id " +
                "WHERE rs.user_id = ? " +
                "ORDER BY p.published_at DESC NULLS LAST, p.created_at DESC " +
                "LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, postRowMapper, userId, limit, offset);
    }

    public List<Post> findByUserIdAndReadStatus(Long userId, int limit, int offset, boolean read) {
        String sql = "SELECT p.id, p.source_id, p.title, p.description, p.link, p.guid, " +
                "p.published_at, p.created_at, p.is_read " +
                "FROM posts p " +
                "JOIN rss_sources rs ON p.source_id = rs.id " +
                "WHERE rs.user_id = ? AND p.is_read = ? " +
                "ORDER BY p.published_at DESC NULLS LAST, p.created_at DESC " +
                "LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, postRowMapper, userId, read, limit, offset);
    }

    public boolean existsBySourceIdAndGuid(Long sourceId, String guid) {
        String sql = "SELECT COUNT(*) FROM posts WHERE source_id = ? AND guid = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, sourceId, guid);
        return count != null && count > 0;
    }

    public void save(Post post) { //по факту не нужен просто по crud сделала
        String sql = "INSERT INTO posts (source_id, title, description, link, guid, published_at, created_at, is_read) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            var ps = connection.prepareStatement(sql);
            ps.setLong(1, post.getSourceId());
            ps.setString(2, post.getTitle());
            ps.setString(3, post.getDescription());
            ps.setString(4, post.getLink());
            ps.setString(5, post.getGuid());
            ps.setTimestamp(6, post.getPublishedAt() != null
                    ? Timestamp.valueOf(post.getPublishedAt())
                    : null);
            ps.setTimestamp(7, Timestamp.valueOf(post.getCreatedAt()));
            ps.setBoolean(8, post.isRead());
            return ps;
        });
    }

    public Post findById(Long id) {
        String sql = "SELECT id, source_id, title, description, link, guid, published_at, created_at, is_read " +
                "FROM posts WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, postRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public int getCountByUserId(Long userId) {
        String sql = "SELECT COUNT(*) FROM posts p " +
                "JOIN rss_sources rs ON p.source_id = rs.id " +
                "WHERE rs.user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count != null ? count : 0;
    }

    public int getCountByUserIdAndReadStatus(Long userId, boolean read) {
        String sql = "SELECT COUNT(*) FROM posts p " +
                "JOIN rss_sources rs ON p.source_id = rs.id " +
                "WHERE rs.user_id = ? AND p.is_read = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, read);
        return count != null ? count : 0;
    }

    public void saveAll(List<Post> posts) {
        String sql = "INSERT INTO posts (source_id, title, description, link, guid, published_at, created_at, is_read) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, posts, 100, (ps, post) -> {
            ps.setLong(1, post.getSourceId());
            ps.setString(2, post.getTitle());
            ps.setString(3, post.getDescription());
            ps.setString(4, post.getLink());
            ps.setString(5, post.getGuid());
            ps.setTimestamp(6, post.getPublishedAt() != null
                    ? Timestamp.valueOf(post.getPublishedAt())
                    : null);
            ps.setTimestamp(7, Timestamp.valueOf(post.getCreatedAt()));
            ps.setBoolean(8, post.isRead());
        });
    }

    public int deleteBySourceId(Long sourceId) {
        String sql = "DELETE FROM posts WHERE source_id = ?";
        return jdbcTemplate.update(sql, sourceId);
    }

    public void markAsRead(Long postId, Long userId) {
        String sql = "UPDATE posts SET is_read = true " +
                "WHERE id = ? AND source_id IN " +
                "(SELECT id FROM rss_sources WHERE user_id = ?)";
        jdbcTemplate.update(sql, postId, userId);
    }
}