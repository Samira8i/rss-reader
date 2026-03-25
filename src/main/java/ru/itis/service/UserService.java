package ru.itis.service;

import org.springframework.stereotype.Service;
import ru.itis.dto.SignInForm;
import ru.itis.dto.SignUpForm;
import ru.itis.exception.InvalidCredentialsException;
import ru.itis.exception.UserAlreadyExistsException;
import ru.itis.model.Session;
import ru.itis.model.User;
import ru.itis.repository.SessionRepository;
import ru.itis.repository.UserRepository;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final PasswordService passwordService;

    public UserService(UserRepository userRepository,
                       SessionRepository sessionRepository,
                       PasswordService passwordService) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.passwordService = passwordService;
    }

    public User register(SignUpForm form) {
        if (userRepository.existsByUsername(form.getUsername())) {
            throw new UserAlreadyExistsException("Пользователь с таким именем уже существует");
        }

        User user = new User();
        user.setUsername(form.getUsername());

        String salt = passwordService.generateSalt();
        user.setSalt(salt);

        String hashedPassword = passwordService.hashPassword(form.getPassword(), salt);
        user.setPasswordHash(hashedPassword);

        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    public Session login(SignInForm form) {
        User user = userRepository.findByUsername(form.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Неверное имя пользователя или пароль"));

        if (!passwordService.checkPassword(form.getPassword(), user.getSalt(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Неверное имя пользователя или пароль");
        }

        // Удаляю старую сессию, если есть
        sessionRepository.findByUserId(user.getId()).ifPresent(s ->
                sessionRepository.deleteSession(s.getSessionId())
        );

        // Создаю новую сессию
        return sessionRepository.createSession(user.getId());
    }

    public void logout(String sessionId) {
        sessionRepository.deleteSession(sessionId);
    }
}