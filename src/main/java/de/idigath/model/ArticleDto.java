package de.idigath.model;

import java.time.LocalDateTime;

public record ArticleDto(
    String title,
    String logoUrl,
    String summary,
    String textContent,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
