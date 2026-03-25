package ru.itis.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.itis.dto.SignInForm;
import ru.itis.dto.SignUpForm;
import ru.itis.exception.InvalidCredentialsException;
import ru.itis.exception.UserAlreadyExistsException;
import ru.itis.model.Session;
import ru.itis.service.UserService;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("signUpForm", new SignUpForm());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("signUpForm") SignUpForm form,
                           BindingResult bindingResult,
                           Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            userService.register(form);
            return "redirect:/auth/login?registered=true";
        } catch (UserAlreadyExistsException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("signInForm", new SignInForm());
        return "login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("signInForm") SignInForm form,
                        BindingResult bindingResult,
                        HttpServletResponse response,
                        Model model) {
        if (bindingResult.hasErrors()) {
            return "login";
        }

        try {
            Session session = userService.login(form);

            Cookie cookie = new Cookie("SESSION_ID", session.getSessionId());
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60);
            response.addCookie(cookie);
            return "redirect:/";
        } catch (InvalidCredentialsException e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("SESSION_ID".equals(cookie.getName())) {
                    userService.logout(cookie.getValue());
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                    break;
                }
            }
        }
        return "redirect:/";
    }
}