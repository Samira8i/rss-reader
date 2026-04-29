package ru.itis.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.itis.model.Post;;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p " +
            "WHERE p.source.user.id = :userId " +
            "ORDER BY p.publishedAt DESC NULLS LAST, p.createdAt DESC")
    List<Post> findByUserIdOrderByDate(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT p FROM Post p " +
            "WHERE p.source.user.id = :userId AND p.read = :read " +
            "ORDER BY p.publishedAt DESC NULLS LAST, p.createdAt DESC")
    List<Post> findByUserIdAndReadStatus(@Param("userId") Long userId,
                                         @Param("read") boolean read,
                                         Pageable pageable);

    long countBySourceUser_Id(Long userId);
    long countBySourceUser_IdAndRead(Long userId, boolean read);

    @Query("UPDATE Post p SET p.read = true WHERE p.id = :postId AND p.source.user.id = :userId")
    void markAsRead(@Param("postId") Long postId, @Param("userId") Long userId);
}