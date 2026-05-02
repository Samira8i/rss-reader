package ru.itis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.model.RssSource;
import ru.itis.model.User;
import java.util.List;

public interface RssSourceRepository extends JpaRepository<RssSource, Long> {

    List<RssSource> findByUser(User user);

    boolean existsByUserAndUrl(User user, String url);
}