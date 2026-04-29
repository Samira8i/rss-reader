package ru.itis.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.itis.dto.SignInForm;
import ru.itis.dto.SignUpForm;
import ru.itis.exception.InvalidCredentialsException;
import ru.itis.exception.UserAlreadyExistsException;
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
}