package de.idigath.web;

import de.idigath.entities.Article;
import de.idigath.mappers.ArticleMapper;
import de.idigath.model.ArticleDto;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.math.BigInteger;
import java.util.Set;

/**
 * RESTful endpoint for managing articles and related entities. Provides CRUD operations for
 * articles. Also supports operations with associated entities such as sources and tags.
 */
@Path("/api/articles")
public class ArticleEndpoint {

  /**
   * Retrieves the article by its unique identifier.
   *
   * @param id the unique identifier of the article
   * @return Article {@link ArticleDto} in json-format
   */
  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public ArticleDto getArticle(@PathParam("id") BigInteger id) {
    Article article = Article.findById(id);
    ArticleDto result = ArticleMapper.INSTANCE.articleToArticleDto(article);
    return result;
  }

  /**
   * Searches for articles based on the provided query and/or tag. The search can be performed using
   * a text query (including regex), a tag, or both. If no articles match the criteria, an empty set
   * is returned.
   *
   * @param query the text query (including regex) to search for in article titles. If {@code null}
   *     or empty, this parameter is ignored.
   * @param tag the tag to filter articles. If {@code null} or empty, this parameter is ignored.
   * @return a set of {@link ArticleDto} objects matching the search criteria. Returns an empty set
   *     if no articles are found.
   */
  @GET
  @Path("/search")
  @Produces(MediaType.APPLICATION_JSON)
  public Set<ArticleDto> searchArticle(
      @QueryParam("query") String query, @QueryParam("tag") String tag) {
    return null;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public void addArticle() {}

  /**
   * Deletes the article by its unique identifier.
   *
   * @param id the unique identifier of the article
   */
  @DELETE
  @Path("/{id}")
  public void deleteArticle(@PathParam("id") int id) {}

  @PUT
  @Path("/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  public void editArticle(@PathParam("id") int id) {}
}
