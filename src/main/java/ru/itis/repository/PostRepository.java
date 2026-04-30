package ru.itis.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.model.Post;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p " +
            "JOIN FETCH p.source s " +
            "WHERE s.user.id = :userId " +
            "ORDER BY p.publishedAt DESC NULLS LAST, p.createdAt DESC")
    List<Post> findByUserIdOrderByDate(@Param("userId") Long userId, Pageable pageable);

    // ✅ ИСПРАВЛЕННЫЙ метод подсчета
    @Query("SELECT COUNT(p) FROM Post p WHERE p.source.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE Post p SET p.read = true WHERE p.id = :postId AND p.source.user.id = :userId")
    void markAsRead(@Param("postId") Long postId, @Param("userId") Long userId);

    @Query("SELECT p FROM Post p " +
            "JOIN FETCH p.source s " +
            "JOIN FETCH s.user u " +
            "WHERE p.id = :postId")
    Optional<Post> findByIdWithSourceAndUser(@Param("postId") Long postId);

    boolean existsByGuidAndSourceId(String guid, Long sourceId);
}