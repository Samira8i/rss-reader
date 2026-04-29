package ru.itis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.itis.model.Post;
import ru.itis.model.User;
import ru.itis.service.RssService;
import ru.itis.service.UserService;

@Controller
public class FeedController {

    private final RssService rssService;
    private final UserService userService;

    public FeedController(RssService rssService, UserService userService) {
        this.rssService = rssService;
        this.userService = userService;
    }

    @GetMapping("/feed")
    public String getFeed(@RequestParam(defaultValue = "0") int page,
                          @RequestParam(required = false) Boolean read,
                          Model model) {
        User user = userService.getCurrentUser();

        if (user == null) {
            return "redirect:/auth/login";
        }

        int pageSize = 10;
        rssService.updateAllUserSources(user.getId()); // обновляем посты из источников

        java.util.List<Post> posts = rssService.getUserFeed(user.getId(), page, pageSize, read);
        int totalPosts = rssService.getUserFeedCount(user.getId(), read);
        int totalPages = (int) Math.ceil((double) totalPosts / pageSize);

        model.addAttribute("posts", posts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("hasPrev", page > 0);
        model.addAttribute("hasNext", page < totalPages - 1);
        model.addAttribute("user", user);
        model.addAttribute("currentStatus", read);
        model.addAttribute("statusParam", read == null ? "" : (read ? "read" : "unread"));

        return "feed";
    }

    @GetMapping("/feed/post/{id}")
    public String getPost(@PathVariable Long id, Model model) {
        User user = userService.getCurrentUser();

        if (user == null) {
            return "redirect:/auth/login";
        }

        try {
            Post post = rssService.getPostById(id, user.getId());
            rssService.markPostAsRead(id, user.getId());
            model.addAttribute("post", post);
            model.addAttribute("user", user);
            return "post";
        } catch (Exception e) {
            model.addAttribute("error", "Пост не найден или у вас нет доступа");
            return "redirect:/feed";
        }
    }
}