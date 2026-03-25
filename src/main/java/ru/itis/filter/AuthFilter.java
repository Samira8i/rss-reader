package ru.itis.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.itis.model.User;
import ru.itis.repository.SessionRepository;
import ru.itis.repository.UserRepository;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component("authFilter")
public class AuthFilter extends OncePerRequestFilter {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    private static final String[] PUBLIC_PATHS = {
            "/auth/login",
            "/auth/register",
            "/css/"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String sessionId = extractSessionId(request.getCookies());

        if (sessionId == null) {
            System.out.println("No session ID, redirecting to login");
            response.sendRedirect("/auth/login");
            return;
        }

        var sessionOpt = sessionRepository.findBySessionId(sessionId);
        if (sessionOpt.isEmpty()) {
            System.out.println("Session not found in DB");
            response.sendRedirect("/auth/login");
            return;
        }

        Optional<User> userOpt = userRepository.findById(sessionOpt.get().getUserId());
        if (userOpt.isEmpty()) {
            System.out.println("User not found");
            response.sendRedirect("/auth/login");
            return;
        }

        User user = userOpt.get();
        System.out.println("User loaded: " + user.getUsername());
        request.setAttribute("currentUser", user);

        filterChain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        return Arrays.stream(PUBLIC_PATHS).anyMatch(path::startsWith);
    }

    private String extractSessionId(Cookie[] cookies) {
        if (cookies == null) return null;
        return Arrays.stream(cookies)
                .filter(c -> "SESSION_ID".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}