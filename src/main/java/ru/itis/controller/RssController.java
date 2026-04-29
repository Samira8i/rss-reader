package ru.itis.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.itis.dto.RssSourceForm;
import ru.itis.exception.InvalidRssUrlException;
import ru.itis.exception.SourceAlreadyExistsException;
import ru.itis.model.User;
import ru.itis.service.RssService;
import ru.itis.service.UserService;

@Controller
@RequestMapping("/sources")
public class RssController {

    private final RssService rssService;
    private final UserService userService;

    public RssController(RssService rssService, UserService userService) {
        this.rssService = rssService;
        this.userService = userService;
    }

    @GetMapping
    public String listSources(Model model) {
        User user = userService.getCurrentUser();

        if (user == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("sources", rssService.getUserSources(user.getId()));
        model.addAttribute("sourceForm", new RssSourceForm());
        model.addAttribute("user", user);

        return "sources";
    }

    @PostMapping("/add")
    public String addSource(@Valid @ModelAttribute("sourceForm") RssSourceForm form,
                            BindingResult bindingResult,
                            Model model) {
        User user = userService.getCurrentUser();

        if (user == null) {
            return "redirect:/auth/login";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("sources", rssService.getUserSources(user.getId()));
            model.addAttribute("user", user);
            return "sources";
        }

        try {
            rssService.addSource(form, user.getId());
            return "redirect:/sources?success=added";
        } catch (SourceAlreadyExistsException | InvalidRssUrlException e) {
            bindingResult.rejectValue("url", "error.url", e.getMessage());
            model.addAttribute("sources", rssService.getUserSources(user.getId()));
            model.addAttribute("user", user);
            return "sources";
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.rejectValue("url", "error.url", "Ошибка: " + e.getMessage());
            model.addAttribute("sources", rssService.getUserSources(user.getId()));
            model.addAttribute("user", user);
            return "sources";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteSource(@PathVariable Long id) {
        User user = userService.getCurrentUser();

        if (user == null) {
            return "redirect:/auth/login";
        }

        try {
            rssService.deleteSource(id, user.getId());
            return "redirect:/sources?success=deleted";
        } catch (Exception e) {
            return "redirect:/sources?error=delete_failed";
        }
    }
}