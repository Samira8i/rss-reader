package ru.itis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.itis.model.RssSource;
import ru.itis.model.User;
import java.util.List;

public interface RssSourceRepository extends JpaRepository<RssSource, Long> {

    List<RssSource> findByUser(User user);

    @Query("SELECT DISTINCT s FROM RssSource s LEFT JOIN FETCH s.posts WHERE s.user.id = :userId")
    List<RssSource> findByUserIdWithPosts(@Param("userId") Long userId);

    boolean existsByUserAndUrl(User user, String url);

    void deleteByUser(User user);
}