package ru.itis.dto;

import ru.itis.model.RssSource;
import java.time.LocalDateTime;

public class RssSourceResponse {
    private Long id;
    private String name;
    private String url;
    private LocalDateTime createdAt;
    private LocalDateTime lastCheckedAt;

    public RssSourceResponse() {}

    public static RssSourceResponse from(RssSource source) {
        RssSourceResponse dto = new RssSourceResponse();
        dto.setId(source.getId());
        dto.setName(source.getName());
        dto.setUrl(source.getUrl());
        dto.setCreatedAt(source.getCreatedAt());
        dto.setLastCheckedAt(source.getLastCheckedAt());
        return dto;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getUrl() { return url; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastCheckedAt() { return lastCheckedAt; }
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setUrl(String url) { this.url = url; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setLastCheckedAt(LocalDateTime lastCheckedAt) { this.lastCheckedAt = lastCheckedAt; }
}