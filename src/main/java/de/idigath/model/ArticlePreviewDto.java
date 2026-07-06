package de.idigath.model;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Immutable data transfer object representing an article preview without text content for API
 * consumption.
 *
 * <p>This record decouples the API contract from the persistence model, preventing
 * database-specific concerns (e.g., Hibernate lazy loading, entity relationships) from affecting
 * clients. All temporal fields use the server's local time zone.
 */
public record ArticlePreviewDto(
    String title, String logoUrl, String summary, LocalDateTime createdAt, Set<TagDto> tags) {}
