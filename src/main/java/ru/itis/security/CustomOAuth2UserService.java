package ru.itis.security;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import ru.itis.model.User;
import ru.itis.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Map;


//кастомная регистрация и получается юзернжймы связаны с обычным входом-то есть если человек с таким юзернеймом уже есть, то просто идет обновление
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String githubId = String.valueOf(attributes.get("id"));
        String username = (String) attributes.get("login");
        String email = (String) attributes.get("email");

        if (email == null) {
            email = username + "@github.user";
        }

        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setGithubId(githubId);
            user.setCreatedAt(LocalDateTime.now());
            user.setPasswordHash("");
            userRepository.save(user);
        } else if (user.getGithubId() == null) {
            user.setGithubId(githubId);
            if (user.getEmail() == null) {
                user.setEmail(email);
            }
            userRepository.save(user);
        }

        return oAuth2User;
    }
}