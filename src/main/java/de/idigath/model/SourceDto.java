package de.idigath.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Immutable data transfer object representing an article-source for API consumption.
 *
 * <p>This record decouples the API contract from the persistence model, preventing
 * database-specific concerns (e.g., Hibernate, lazy loading, entity relationships) from affecting
 * clients. Alle temporal fields use the server's local time zone.
 */
public record SourceDto(
        @NotNull @Size(max = 255) String title,
        @NotBlank @Size(max = 255) String url,
        @NotNull String citation) {}
