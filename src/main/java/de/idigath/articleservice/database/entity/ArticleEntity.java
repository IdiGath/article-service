package de.idigath.articleservice.database.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Represents a blog article. The main content is stored in {@code textContent}
 * in Markdown format and may include URLs to images. Each article can have
 * multiple references represented by {@link SourceEntity} records.
 *
 * @see SourceEntity
 * @see jakarta.persistence.Entity
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "articles", schema = "public")
public class ArticleEntity {

    // id SERIAL PRIMARY KEY
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // title VARCHAR(255) NOT NULL
    @NotBlank
    @Column(length = 255, nullable = false)
    @Size(min = 3, max = 255)
    private String title;

    // logo_url VARCHAR(255) NOT NULL
    @NotBlank
    @Column(length = 255, nullable = false, name = "logo_url")
    @Size(min = 10, max = 255)
    private String logoUrl;

    // summary VARCHAR(500) NOT NULL
    @NotBlank
    @Column(length = 500, nullable = false)
    @Size(min = 10, max = 500)
    private String summary;

    // text_content TEXT NOT NULL
    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT", name = "text_content")
    private String textContent;

    // created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // FetchType.LAZY is used for performance, ensuring that associated scopes are
    // only loaded from
    // the database when explicitly accessed.
    @OneToMany(mappedBy = "article", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<SourceEntity> sources = new HashSet<>();

    @PrePersist
    private void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addSource(SourceEntity source) {
        sources.add(source);
        source.setArticle(this);
    }

    public void removeSource(SourceEntity source) {
        sources.remove(source);
        source.setArticle(null);
    }

}
