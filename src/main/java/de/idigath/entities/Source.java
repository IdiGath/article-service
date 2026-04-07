package de.idigath.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Source of information referenced in an article, including URL and citation details.
 *
 * <p>Extends {@link PanacheEntity} for automatic ID management and CRUD operations.
 *
 * <p>Each source belongs to exactly one article. This design assumes that the same source (with
 * identical URL and citation) is unlikely to be reused across multiple articles, simplifying the
 * data model and avoiding unnecessary duplication.
 */
@Entity
@Table(name = "sources")
public class Source extends PanacheEntity {

  /**
   * The article this source belongs to. Defines the {@link ManyToOne} relationship. Cascades
   * deletion: if the article is deleted, all its sources are removed automatically.
   */
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "article_id", nullable = false)
  public Article article;

  /** Source title to desplay at the sources list in the end of the article. */
  @Column(length = 255, nullable = false)
  public String title;

  /** Url to the online Source. */
  @Column(length = 255, nullable = false)
  public String url;

  /** Citation information like chapter or line. */
  @Column(nullable = false, columnDefinition = "TEST")
  public String citation;
}
