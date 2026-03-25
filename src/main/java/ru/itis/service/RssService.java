package ru.itis.service;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.springframework.stereotype.Service;
import ru.itis.dto.RssSourceForm;
import ru.itis.exception.SourceAlreadyExistsException;
import ru.itis.exception.InvalidRssUrlException;
import ru.itis.model.Post;
import ru.itis.model.RssSource;
import ru.itis.repository.PostRepository;
import ru.itis.repository.RssSourceRepository;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RssService {

    private final RssSourceRepository rssSourceRepository;
    private final PostRepository postRepository;

    public RssService(RssSourceRepository rssSourceRepository, PostRepository postRepository) {
        this.rssSourceRepository = rssSourceRepository;
        this.postRepository = postRepository;
    }

    public RssSource addSource(RssSourceForm form, Long userId) {
        if (rssSourceRepository.existsByUserIdAndUrl(userId, form.getUrl())) {
            throw new SourceAlreadyExistsException("Такой RSS источник уже добавлен");
        }

        validateRssUrl(form.getUrl());

        RssSource source = new RssSource();
        source.setUserId(userId);
        source.setName(form.getName());
        source.setUrl(form.getUrl());
        source.setCreatedAt(LocalDateTime.now());
        source.setLastCheckedAt(null);

        return rssSourceRepository.save(source);
    }

    public List<RssSource> getUserSources(Long userId) {
        return rssSourceRepository.findByUserId(userId);
    }

    public void deleteSource(Long sourceId, Long userId) {
        RssSource source = rssSourceRepository.findById(sourceId)
                .orElseThrow(() -> new RuntimeException("Источник не найден"));

        if (!source.getUserId().equals(userId)) {
            throw new RuntimeException("Нет доступа к этому источнику");
        }

        int deletedPosts = postRepository.deleteBySourceId(sourceId);
        rssSourceRepository.deleteById(sourceId);
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

    public List<Post> getUserFeed(Long userId, int page, int pageSize, Boolean read) {
        // Обновляю источники
        List<RssSource> sources = rssSourceRepository.findByUserId(userId);
        for (RssSource source : sources) {
            try {
                fetchAndSaveNewPosts(source.getId());
            } catch (Exception e) {
                System.err.println("Ошибка при обновлении: " + e.getMessage());
            }
        }

        int offset = page * pageSize;

        // Если указан статус (read = true/false), фильтрую
        if (read != null) {
            return postRepository.findByUserIdAndReadStatus(userId, pageSize, offset, read);
        }

        return postRepository.findByUserId(userId, pageSize, offset);
    }

    public int getUserFeedCount(Long userId, Boolean read) {
        if (read != null) {
            return postRepository.getCountByUserIdAndReadStatus(userId, read);
        }
        return postRepository.getCountByUserId(userId);
    }

    public Post getPostById(Long postId, Long userId) {
        Post post = postRepository.findById(postId);
        if (post == null) {
            throw new RuntimeException("Пост не найден");
        }

        RssSource source = rssSourceRepository.findById(post.getSourceId())
                .orElseThrow(() -> new RuntimeException("Источник не найден"));

        if (!source.getUserId().equals(userId)) {
            throw new RuntimeException("Нет доступа к этому посту");
        }

        return post;
    }

    public void markPostAsRead(Long postId, Long userId) {
        postRepository.markAsRead(postId, userId);
    }

    public List<Post> fetchAndSaveNewPosts(Long sourceId) {
        RssSource source = rssSourceRepository.findById(sourceId)
                .orElseThrow(() -> new RuntimeException("Источник не найден"));

        try {
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(new URL(source.getUrl())));

            List<Post> newPosts = new ArrayList<>();

            for (SyndEntry entry : feed.getEntries()) {
                if (postRepository.existsBySourceIdAndGuid(sourceId, entry.getUri())) {
                    continue;
                }

                Post post = new Post();
                post.setSourceId(sourceId);
                post.setTitle(entry.getTitle());
                post.setDescription(entry.getDescription() != null ? entry.getDescription().getValue() : null);
                post.setLink(entry.getLink());
                post.setGuid(entry.getUri());
                post.setPublishedAt(entry.getPublishedDate() != null
                        ? entry.getPublishedDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()
                        : null);
                post.setCreatedAt(LocalDateTime.now());
                post.setRead(false); // новые посты по умолчанию непрочитанные

                newPosts.add(post);
            }

            if (!newPosts.isEmpty()) {
                postRepository.saveAll(newPosts);
            }

            rssSourceRepository.updateLastCheckedAt(sourceId, LocalDateTime.now());
            return newPosts;

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при парсинге RSS: " + e.getMessage(), e);
        }
    }
}