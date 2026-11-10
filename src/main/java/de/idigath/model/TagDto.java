package de.idigath.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Immutable data transfer object representing an article's tag for API consumption.
 *
 * <p>This record decouples the API contract from the persistence model, preventing
 * database-specific concerns (e.g., Hibernate lazy loading, entity relationships) from affecting
 * clients. All temporal fields use the server's local time zone.
 */
public record TagDto(@NotBlank @Size(max = 100) String name) {}
