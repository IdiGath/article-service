package de.idigath.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Blog article entity with Markdown content and metadata.
 *
 * <p>Extends {@link PanacheEntity} for automatic ID management and CRUD operations. Timestamps are
 * auto-filled by Hibernate.
 */
@Entity
@Table(name = "articles")
public class Article extends PanacheEntity {

  /** Article title to display at the beginning of the article and in the article list. */
  public String title;

  /** Each article has a logo picture. This argument contains the path to the image service */
  @Column(name = "logo_url")
  public String logoUrl;

  /** Each article has a preview and it contains a short summary of the content. */
  public String summary;

  /** Content auf the article in markdown format.. */
  @Column(name = "text_content")
  public String textContent;

  /** Automaticly generated creation date. */
  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  public LocalDateTime createdAt;

  /** Automaticly generated update date. */
  @UpdateTimestamp
  @Column(name = "updated_at")
  public LocalDateTime updatedAt;
}
