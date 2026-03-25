package ru.itis.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(HttpServletRequest request, Model model) {
        Object user = request.getAttribute("currentUser");

        if (user != null) {
            model.addAttribute("user", user);
        }

        return "home";
    }
}