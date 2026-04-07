package de.idigath.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
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
  @Column(length = 255, nullable = false)
  public String title;

  /** Each article has a logo picture. This argument contains the path to the image service */
  @Column(length = 255, nullable = false, name = "logo_url")
  public String logoUrl;

  /** Each article has a preview and it contains a short summary of the content. */
  @Column(length = 500, nullable = false)
  public String summary;

  /** Content auf the article in markdown format.. */
  @Column(nullable = false, columnDefinition = "TEXT", name = "text_content")
  public String textContent;

  /** Automaticly generated creation date. */
  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  public LocalDateTime createdAt;

  /** Automaticly generated update date. */
  @UpdateTimestamp
  @Column(name = "updated_at")
  public LocalDateTime updatedAt;

  /**
   * The source set of the article. Defines the {@link OneToMany} relationship. Cascades deletion:
   * if the article is deleted, all its sources are removed automatically.
   */
  @OneToMany(
      mappedBy = "article",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  public Set<Source> sources = new HashSet<>();

  /** The tag set of the article. Defines the {@link ManyToMany} relationship. */
  @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
  @JoinTable(
      name = "article_tags",
      joinColumns = @JoinColumn(name = "article_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id"))
  public Set<Tag> tags = new HashSet<>();
}
