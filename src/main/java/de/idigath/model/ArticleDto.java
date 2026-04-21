package de.idigath.model;

import java.time.LocalDateTime;
import java.util.Set;

public record ArticleDto(
    String title,
    String logoUrl,
    String summary,
    String textContent,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Set<SourceDto> sources,
    Set<TagDto> tags) {}
