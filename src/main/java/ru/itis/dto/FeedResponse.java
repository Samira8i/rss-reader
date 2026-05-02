package ru.itis.dto;

import java.util.List;

public record FeedResponse(
        List<PostResponse> posts,
        boolean hasMore,
        int currentPage,
        int total
) {}