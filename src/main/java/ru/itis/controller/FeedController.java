package ru.itis.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.itis.model.Post;
import ru.itis.model.User;
import ru.itis.service.RssService;

import java.util.List;

@Controller
@RequestMapping("/feed")
public class FeedController {

    private final RssService rssService;

    public FeedController(RssService rssService) {
        this.rssService = rssService;
    }

    @GetMapping
    public String getFeed(HttpServletRequest request,
                          @RequestParam(name = "page", defaultValue = "0") int page,
                          @RequestParam(name = "status", required = false) Boolean read,
                          Model model) {

        User user = (User) request.getAttribute("currentUser");

        if (user == null) {
            return "redirect:/auth/login";
        }

        int pageSize = 10;

        //вот короче как конвертер работает, то есть автоматом в бул переводит
        System.out.println("Получен параметр status: " + request.getParameter("status"));
        System.out.println("После конвертера read = " + read);

        // передаю read в сервис для фильтрации
        // получаю посты пользователя с пагинацией и фильтром по статусу
        List<Post> posts = rssService.getUserFeed(user.getId(), page, pageSize, read);
        // Считаю общее количество постов
        int totalPosts = rssService.getUserFeedCount(user.getId(), read);
        int totalPages = (int) Math.ceil((double) totalPosts / pageSize);

        model.addAttribute("posts", posts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("hasPrev", page > 0);
        model.addAttribute("hasNext", page < totalPages - 1);
        model.addAttribute("user", user);
        model.addAttribute("currentStatus", read);
        model.addAttribute("statusParam", request.getParameter("status"));

        return "feed";
    }

    @GetMapping("/post/{id}")
    public String getPost(@PathVariable("id") Long id,
                          HttpServletRequest request,
                          Model model) {

        User user = (User) request.getAttribute("currentUser");

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