package de.idigath.web;

import de.idigath.model.ArticleDto;
import de.idigath.model.ArticleOverview;
import de.idigath.service.ArticleService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestResponse;

/**
 * RESTful endpoint for managing articles and related entities. Provides CRUD
 * operations for
 * articles. Also supports operations with associated entities such as sources
 * and tags.
 */
@Path("/api/articles")
@Tag(name = "Articles", description = "Articles operations.")
public class ArticleEndpoint {

  @Inject
  ArticleService articleService;

  /**
   * Retrieves article overview from the database sorted by creation date. If page
   * is null, returns
   * the first page.
   *
   * @param page page number of selection (optional, defaults to first page)
   * @return ArticleOverview {@link ArticleOverview} in json-format
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public RestResponse<ArticleOverview> getArticleOverview(@QueryParam("page") Integer page) {
    var result = articleService.searchArticleOverview(page, null, null);
    return RestResponse.ok(result);
  }

  /**
   * Retrieves the article by its unique identifier.
   *
   * @param id the unique identifier of the article
   * @return Article {@link ArticleDto} in json-format
   */
  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public RestResponse<ArticleDto> getArticle(@PathParam("id") Long id) {
    var result = articleService.findArticleById(id);
    return RestResponse.ok(result);
  }

  /**
   * Searches for articles based on the provided query and/or tag. The search can
   * be performed using
   * a text query (including regex), a tag, or both. If no articles match the
   * criteria, an empty set
   * is returned.
   *
   * @param page current page, if null then return first page
   * @param text the text query (including regex) to search for in article titles.
   *             If {@code null}
   *             or empty, this parameter is ignored.
   * @param tag  the tag to filter articles. If {@code null} or empty, this
   *             parameter is ignored.
   * @return a list of {@link ArticleDto} objects matching the search criteria.
   *         Returns an empty set
   *         if no articles are found.
   */
  @GET
  @Path("/search")
  @Produces(MediaType.APPLICATION_JSON)
  public RestResponse<ArticleOverview> searchArticle(
      @QueryParam("page") Integer page,
      @QueryParam("text") String text,
      @QueryParam("tag") String tag) {
    // TODO: Implement
    return null;
  }

  /**
   * Creates a new article in the database. After resource creation responses with
   * the URI to the
   * new Resource.
   *
   * @param article new article from the api consumer
   * @param uriinfo information to the current URI
   * @return HTTP-Status 201 with location in the happy case
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public RestResponse<Void> addArticle(ArticleDto article, @Context UriInfo uriinfo) {
    // TODO: Implement
    URI location = uriinfo.getAbsolutePathBuilder().path("42").build();
    return RestResponse.created(location);
  }

  /**
   * Deletes the article by its unique identifier.
   *
   * @param id the unique identifier of the article
   * @return HTTP-Satus 204 in happy case
   */
  @DELETE
  @Path("/{id}")
  public RestResponse<Void> deleteArticle(@PathParam("id") int id) {
    // TODO: Implement
    return RestResponse.noContent();
  }

  /**
   * Updates current article with the new version from the body.
   *
   * @param id      the unique identifier of the article
   * @param article new version of the article
   * @return HTTP-Satus 200 with modified time and updated article version in body
   */
  @PUT
  @Path("/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  public RestResponse<ArticleDto> editArticle(@PathParam("id") int id, ArticleDto article) {
    // TODO: Implement
    Date lastModified = Date.from(Instant.now().truncatedTo(ChronoUnit.SECONDS));
    return RestResponse.ResponseBuilder.ok(article).lastModified(lastModified).build();
  }
}
