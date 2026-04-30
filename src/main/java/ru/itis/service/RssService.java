package ru.itis.service;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.dto.RssSourceForm;
import ru.itis.exception.SourceAlreadyExistsException;
import ru.itis.exception.InvalidRssUrlException;
import ru.itis.model.Post;
import ru.itis.model.RssSource;
import ru.itis.model.User;
import ru.itis.repository.PostRepository;
import ru.itis.repository.RssSourceRepository;
import ru.itis.repository.UserRepository;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RssService {

    private final RssSourceRepository rssSourceRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public RssService(RssSourceRepository rssSourceRepository,
                      PostRepository postRepository,
                      UserRepository userRepository) {
        this.rssSourceRepository = rssSourceRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public RssSource addSource(RssSourceForm form, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (rssSourceRepository.existsByUserAndUrl(user, form.getUrl())) {
            throw new SourceAlreadyExistsException("Такой RSS источник уже добавлен");
        }

        validateRssUrl(form.getUrl());

        RssSource source = new RssSource();
        source.setUser(user);
        source.setName(form.getName());
        source.setUrl(form.getUrl());
        source.setCreatedAt(LocalDateTime.now());
        source.setLastCheckedAt(null);

        return rssSourceRepository.save(source);
    }

    @Transactional(readOnly = true)
    public List<RssSource> getUserSources(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return rssSourceRepository.findByUser(user);
    }

    @Transactional
    public void deleteSource(Long sourceId, Long userId) {
        RssSource source = rssSourceRepository.findById(sourceId)
                .orElseThrow(() -> new RuntimeException("Источник не найден"));

        if (!source.getUser().getId().equals(userId)) {
            throw new RuntimeException("Нет доступа к этому источнику");
        }

        rssSourceRepository.delete(source);
    }

    private void validateRssUrl(String url) {
        try {
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(new URL(url)));

            if (feed.getEntries() == null) {
                throw new InvalidRssUrlException("Не удалось прочитать RSS ленту");
            }
        } catch (Exception e) {
            throw new InvalidRssUrlException("Некорректный RSS URL: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<Post> getUserFeed(Long userId, int page, int pageSize, Boolean read) {
        // Обновляем источники (получаем новые посты)
        updateAllUserSources(userId);

        org.springframework.data.domain.Pageable pageable =
                org.springframework.data.domain.PageRequest.of(page, pageSize);

        if (read != null) {
            return postRepository.findByUserIdAndReadStatus(userId, read, pageable);
        }
        return postRepository.findByUserIdOrderByDate(userId, pageable);
    }

    @Transactional(readOnly = true)
    public int getUserFeedCount(Long userId, Boolean read) {
        if (read != null) {
            return (int) postRepository.countBySourceUser_IdAndRead(userId, read);
        }
        return (int) postRepository.countBySourceUser_Id(userId);
    }

    @Transactional
    public void updateAllUserSources(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        List<RssSource> sources = rssSourceRepository.findByUser(user);
        for (RssSource source : sources) {
            try {
                fetchAndSaveNewPosts(source.getId());
            } catch (Exception e) {
                System.err.println("Ошибка при обновлении источника " + source.getId() + ": " + e.getMessage());
            }
        }
    }

    @Transactional(readOnly = true)
    public Post getPostById(Long postId, Long userId) {
        Post post = postRepository.findByIdWithSourceAndUser(postId)
                .orElseThrow(() -> new RuntimeException("Пост не найден"));

        if (!post.getSource().getUser().getId().equals(userId)) {
            throw new RuntimeException("Нет доступа к этому посту");
        }

        return post;
    }

    @Transactional
    public void markPostAsRead(Long postId, Long userId) {
        postRepository.markAsRead(postId, userId);
    }

    @Transactional
    public List<Post> fetchAndSaveNewPosts(Long sourceId) {
        RssSource source = rssSourceRepository.findById(sourceId)
                .orElseThrow(() -> new RuntimeException("Источник не найден"));

        try {
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(new URL(source.getUrl())));

            List<Post> newPosts = new ArrayList<>();

            for (SyndEntry entry : feed.getEntries()) {
                // Проверяем, есть ли уже такой пост по GUID
                boolean exists = postRepository.findAll().stream()
                        .anyMatch(p -> p.getGuid().equals(entry.getUri()) &&
                                p.getSource().getId().equals(sourceId));

                if (exists) {
                    continue;
                }

                Post post = new Post();
                post.setSource(source);
                post.setTitle(entry.getTitle());
                post.setDescription(entry.getDescription() != null ? entry.getDescription().getValue() : null);
                post.setLink(entry.getLink());
                post.setGuid(entry.getUri());
                post.setPublishedAt(entry.getPublishedDate() != null
                        ? entry.getPublishedDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()
                        : null);
                post.setCreatedAt(LocalDateTime.now());
                post.setRead(false);

                newPosts.add(post);
            }

            if (!newPosts.isEmpty()) {
                postRepository.saveAll(newPosts);
            }

            // Обновляем время последней проверки
            source.setLastCheckedAt(LocalDateTime.now());
            rssSourceRepository.save(source);

            return newPosts;

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при парсинге RSS: " + e.getMessage(), e);
        }
    }
}