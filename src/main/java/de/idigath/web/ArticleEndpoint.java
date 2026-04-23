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
import jakarta.ws.rs.core.MediaType;
import java.math.BigInteger;

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
   * @return Article in json-format
   */
  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public ArticleDto getArticle(@PathParam("id") BigInteger id) {
    Article article = Article.findById(id);
    ArticleDto result = ArticleMapper.INSTANCE.articleToArticleDto(article);
    return result;
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
