package ru.itis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.itis.model.Post;
import ru.itis.model.User;
import ru.itis.service.RssService;
import ru.itis.service.UserService;

@Controller
public class FeedController {

    private final UserService userService;
    private final RssService rssService;

    public FeedController(UserService userService, RssService rssService) {
        this.userService = userService;
        this.rssService = rssService;
    }

    @GetMapping("/feed")
    public String getFeed(Model model) {
        User user = userService.getCurrentUser();

        if (user == null) {
            return "redirect:/auth/login";
        }
        rssService.checkForUpdates(user.getId());

        model.addAttribute("user", user);

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
            e.printStackTrace();
            model.addAttribute("error", "Пост не найден или у вас нет доступа");
            return "redirect:/feed";
        }
    }
}