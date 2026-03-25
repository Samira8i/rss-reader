package ru.itis.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.itis.exception.InvalidRssUrlException;
import ru.itis.exception.SourceAlreadyExistsException;
import ru.itis.exception.UserAlreadyExistsException;
import ru.itis.exception.InvalidCredentialsException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public String handleUserAlreadyExists(UserAlreadyExistsException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "register";
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public String handleInvalidCredentials(InvalidCredentialsException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "login";
    }

    @ExceptionHandler(SourceAlreadyExistsException.class)
    public String handleSourceAlreadyExists(SourceAlreadyExistsException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "sources";
    }

    @ExceptionHandler(InvalidRssUrlException.class)
    public String handleInvalidRssUrl(InvalidRssUrlException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "sources";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        ex.printStackTrace();
        model.addAttribute("error", "Произошла ошибка: " + ex.getMessage());
        return "error";
    }
}