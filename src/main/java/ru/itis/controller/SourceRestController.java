package ru.itis.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itis.dto.RssSourceForm;
import ru.itis.dto.RssSourceResponse;
import ru.itis.model.RssSource;
import ru.itis.model.User;
import ru.itis.service.RssService;
import ru.itis.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sources")
public class SourceRestController {

    private final RssService rssService;
    private final UserService userService;

    public SourceRestController(RssService rssService, UserService userService) {
        this.rssService = rssService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<RssSourceResponse>> getSources() {
        User user = userService.getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        List<RssSourceResponse> sources = rssService.getUserSources(user.getId())
                .stream()
                .map(RssSourceResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(sources);
    }

    @PostMapping
    public ResponseEntity<?> addSource(@RequestBody RssSourceForm form) {
        User user = userService.getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            RssSource source = rssService.addSource(form, user.getId());
            return ResponseEntity.ok(RssSourceResponse.from(source));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSource(@PathVariable Long id) {
        User user = userService.getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            rssService.deleteSource(id, user.getId());
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}