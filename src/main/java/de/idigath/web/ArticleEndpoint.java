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

@Path("/api/articles")
public class ArticleEndpoint {

  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public ArticleDto getArticle(@PathParam("id") BigInteger id) {
    System.out.println(Article.findById(id).toString());
    Article article = Article.findById(id);
    ArticleDto result = ArticleMapper.INSTANCE.ArticleToArticleDto(article);
    return result;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public void addArticle() {}

  @DELETE
  @Path("/{id}")
  public void deleteArticle(@PathParam("id") int id) {}

  @PUT
  @Path("/{id}")
  public void editArticle(@PathParam("id") int id) {}
}
