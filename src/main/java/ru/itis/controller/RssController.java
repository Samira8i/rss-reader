package ru.itis.controller;

import jakarta.servlet.http.HttpServletRequest;
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

@Controller
@RequestMapping("/sources")
public class RssController {

    private final RssService rssService;

    public RssController(RssService rssService) {
        this.rssService = rssService;
    }

    @GetMapping
    public String listSources(HttpServletRequest request, Model model) {
        User user = (User) request.getAttribute("currentUser");

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
                            HttpServletRequest request,
                            Model model) {
        User user = (User) request.getAttribute("currentUser");

        if (user == null) {
            return "redirect:/auth/login";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("sources", rssService.getUserSources(user.getId()));
            model.addAttribute("user", user);
            return "sources";
        }

        try {
            var source = rssService.addSource(form, user.getId());
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
    public String deleteSource(@PathVariable("id") Long id, HttpServletRequest request) {
        User user = (User) request.getAttribute("currentUser");

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