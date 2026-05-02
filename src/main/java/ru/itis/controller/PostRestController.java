package ru.itis.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itis.dto.FeedResponse;
import ru.itis.dto.PostResponse;
import ru.itis.model.User;
import ru.itis.service.RssService;
import ru.itis.service.UserService;
import java.util.List;
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
    public ResponseEntity<FeedResponse> getFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        User user = userService.getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        List<PostResponse> posts = rssService.getUserFeed(user.getId(), page, size)
                .stream()
                .map(PostResponse::from)
                .collect(Collectors.toList());

        int total = rssService.getUserFeedCount(user.getId());
        boolean hasMore = (page + 1) * size < total;

        return ResponseEntity.ok(new FeedResponse(posts, hasMore, page, total));
    }
}