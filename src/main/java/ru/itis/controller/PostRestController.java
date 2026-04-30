package ru.itis.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itis.dto.PostResponse;
import ru.itis.model.User;
import ru.itis.service.RssService;
import ru.itis.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class PostRestController {

    private final RssService rssService;
    private final UserService userService;

    public PostRestController(RssService rssService, UserService userService) {
        this.rssService = rssService;
        this.userService = userService;
    }

    @GetMapping("/feed")
    public ResponseEntity<Map<String, Object>> getFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean read) {

        User user = userService.getCurrentUser();

        System.out.println("REST запрос /api/feed от пользователя: " + (user != null ? user.getUsername() : "null"));

        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        List<PostResponse> posts = rssService.getUserFeed(user.getId(), page, size, read)
                .stream()
                .map(PostResponse::from)
                .collect(Collectors.toList());

        int total = rssService.getUserFeedCount(user.getId(), read);

        Map<String, Object> response = new HashMap<>();
        response.put("posts", posts);
        response.put("hasMore", (page + 1) * size < total);
        response.put("currentPage", page);

        System.out.println("Отправлено постов: " + posts.size() + ", hasMore: " + ((page + 1) * size < total));

        return ResponseEntity.ok(response);
    }
}