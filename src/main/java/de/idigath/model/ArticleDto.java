package de.idigath.model;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Immutable data transfer object representing an article for API consumption.
 *
 * <p>This record decouples the API contract from the persistence model, preventing
 * database-specific concerns (e.g., Hibernate lazy loading, entity relationships) from affecting
 * clients. All temporal fields use the server's local time zone.
 */
public record ArticleDto(
    String title,
    String logoUrl,
    String summary,
    String textContent,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Set<SourceDto> sources,
    Set<TagDto> tags) {}
