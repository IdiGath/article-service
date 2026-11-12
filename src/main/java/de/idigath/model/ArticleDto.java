package de.idigath.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
        @NotBlank @Size(max = 255) String title,
        @JsonProperty(access = Access.READ_ONLY) String slug,
        @NotNull @Size(max = 255) String logoUrl,
        @NotNull @Size(max = 500) String summary,
        @NotNull String textContent,
        @JsonProperty(access = Access.READ_ONLY) LocalDateTime createdAt,
        @JsonProperty(access = Access.READ_ONLY) LocalDateTime updatedAt,
        @NotNull @Valid Set<SourceDto> sources,
        @NotNull @Valid Set<TagDto> tags) {}
