package de.idigath.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a tag that can be associated with multiple articles.
 *
 * <p>Each tag can belong to many articles, and each article can have many tags. This is the inverse
 * side of the bidirectional {@link ManyToMany} relationship with {@link Article}. The owning side
 * is defined in the {@link Article#tags} field.
 *
 * <p>Extends {@link PanacheEntity} for automatic ID managemeint and CRUD operations.
 *
 * <p>Used for categorizing and filtering articles by topics.
 */
@Entity
@Table(name = "tags")
public class Tag extends PanacheEntity {

  /** Unique name of the each tag. */
  @Column(length = 100, nullable = false, unique = true)
  public String name;

  /**
   * Articles associated with this tag. This is the inverse side of the bidirectional relationship.
   * Changes to this set are not persisted unless the owning side ({@link Article#tags}) is updated.
   *
   * @see Article#tags
   */
  @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
  @JsonIgnore
  public Set<Article> articles = new HashSet<>();
}
