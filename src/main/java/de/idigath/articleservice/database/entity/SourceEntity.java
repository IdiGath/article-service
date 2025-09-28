package de.idigath.articleservice.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Represents a reference (source) attached to an {@link ArticleEntity}.
 * A source has a title and a URL and belongs to exactly one article.
 * 
 * @see ArticleEntity
 * @see jakarta.persistence.Entity
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "sources", schema = "public")
public class SourceEntity {

	// id SERIAL PRIMARY KEY
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	// article_id INTEGER NOT NULL (FK to articles.id)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "article_id", nullable = false)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private ArticleEntity article;

	// title VARCHAR(255) NOT NULL
	@NotBlank
	@Column(length = 255, nullable = false)
	@Size(min = 3, max = 255)
	private String title;

	// url VARCHAR(255) NOT NULL
	@NotBlank
	@Column(length = 255, nullable = false)
	@Size(min = 3, max = 255)
	private String url;
}
