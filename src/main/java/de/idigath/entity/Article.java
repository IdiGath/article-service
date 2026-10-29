package de.idigath.entity;

import de.idigath.model.ArticlePreviewDto;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Blog article entity with Markdown content and metadata.
 *
 * <p>
 * Extends {@link PanacheEntity} for automatic ID management and CRUD
 * operations. Timestamps are
 * auto-filled by Hibernate.
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "Article.findByTitleAndTags", query = "select a from Article a join a.tags t"
        + " where lower(a.title) like lower(concat('%', :title, '%')) escape '!'"
        + " and lower(t.name) in :tags group by a having count(distinct t) = :tagCount")
})
@Table(name = "articles")
public class Article extends PanacheEntity {

  /**
   * Searches for articles by title and a list of tags with wildcard matching.
   *
   * <p>
   * The query performs a LIKE search on the title with wildcards (%)
   * automatically
   * appended, allowing partial matches. For tags, an article matches only if it
   * has ALL of the given tags (case-insensitive, supports partial matches).
   *
   * <p>
   * If the tag list is null or empty, the tag filter is not applied and all
   * articles matching the title are returned.
   *
   * <p>
   * The title is matched as a literal substring: LIKE wildcard characters
   * ({@code %}, {@code _}) in the input are escaped and treated as ordinary
   * characters.
   *
   * @param title text to search for in article titles (case-insensitive, supports
   *              partial matches)
   * @param tags  list of tag names the article must have (case-insensitive,
   *              supports partial matches). Duplicates are ignored.
   * @return a PanacheQuery containing articles that match both the title and all
   *         tag criteria
   */
  public static PanacheQuery<Article> findByTitleAndTags(String title, List<String> tags) {
    String escapedTitle = escapeLikePattern(title);
    if (Objects.isNull(tags) || tags.isEmpty()) {
      return find("lower(title) like lower(concat('%', ?1, '%')) escape '!'", escapedTitle);
    }
    var distinctTags = tags.stream().distinct().toList();
    return find("#Article.findByTitleAndTags", Map.of(
        "title", escapedTitle,
        "tags", distinctTags,
        "tagCount", distinctTags.size()));
  }

  /**
   * Finds an article with the exact title match (case-insensitive).
   *
   * @param title title of the Article
   * @return an Optional containing the article with the given title, or empty
   *         if no article exists
   */
  public static Optional<Article> findByTitleOptional(String title) {
    return find("lower(title) = lower(?1)", title).firstResultOptional();
  }

  /**
   * Escapes the LIKE escape character and wildcards ({@code %}, {@code _}) in
   * user input so they are matched literally instead of acting as pattern
   * wildcards. Uses {@code !} as the escape character.
   *
   * @param value raw user input, may be {@code null}
   * @return the value safe to embed into a LIKE pattern with {@code !} as the
   *         escape character
   */
  private static String escapeLikePattern(String value) {
    if (Objects.isNull(value)) {
      return null;
    }
    return value.replace("!", "!!").replace("%", "!%").replace("_", "!_");
  }

  /**
   * Article title to display at the beginning of the article and in the article
   * list.
   */
  @Column(length = 255, nullable = false, unique = true)
  public String title;

  /**
   * Each article has a logo picture. This argument contains the path to the image
   * service
   */
  @Column(length = 255, nullable = false, name = "logo_url")
  public String logoUrl;

  /**
   * Each article has a preview and it contains a short summary of the content.
   */
  @Column(length = 500, nullable = false)
  public String summary;

  /**
   * Content auf the article in markdown format. The text is loaded lazy to save
   * resoruces for
   * article previews.
   *
   * @see ArticlePreviewDto
   */
  @Basic(fetch = FetchType.LAZY)
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
   * The source set of the article. Defines the {@link OneToMany} relationship.
   * Cascades deletion:
   * if the article is deleted, all its sources are removed automatically.
   */
  @OneToMany(mappedBy = "article", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  public Set<Source> sources = new HashSet<>();

  /** The tag set of the article. Defines the {@link ManyToMany} relationship. */
  @ManyToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST })
  @JoinTable(name = "article_tags", joinColumns = @JoinColumn(name = "article_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
  public Set<Tag> tags = new HashSet<>();
}
