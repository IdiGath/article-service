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
import java.util.List;
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
   * Retrieves the article with the given title.
   *
   * @param title exact article title, matched case-insensitively
   * @return HTTP status 200 with the article as {@link ArticleDto} in JSON
   *         format; 404 if no article with the given title exists
   */
  @GET
  @Path("/{title}")
  @Produces(MediaType.APPLICATION_JSON)
  public RestResponse<ArticleDto> getArticle(@PathParam("title") String title) {
    var result = articleService.findArticleByTitle(title);
    return RestResponse.ok(result);
  }

  /**
   * Searches for articles based on the provided query and/or tags. The search can
   * be performed using
   * a text query, one or more tags, or both. An article matches
   * only if it has ALL of the given tags. If no articles match the criteria, an
   * empty set is returned.
   *
   * @param page current page, if null then return first page
   * @param text the text query to search for in article titles (case-insensitive
   *             substring match; LIKE wildcards {@code %} and {@code _} are
   *             treated
   *             literally). If {@code null}
   *             or empty, this parameter is ignored.
   * @param tags the list of tags to filter articles. An article must have all of
   *             them. If {@code null} or empty, this parameter is ignored.
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
      @QueryParam("tag") List<String> tags) {
    var result = articleService.searchArticleOverview(page, text, tags);
    return RestResponse.ok(result);
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
    // TODO: Implement full CRUD — save article to DB
    // TODO: AI integration (Vikunja #45) — classify article (auto-tags/categories)
    // and spell-check without altering writing style before saving
    URI location = uriinfo.getAbsolutePathBuilder().path("42").build();
    return RestResponse.created(location);
  }

  /**
   * Deletes the article with the given title.
   *
   * @param title exact article title, matched case-insensitively
   * @return HTTP status 204 if the article was deleted, 404 if no article
   *         with the given title exists
   */
  @DELETE
  @Path("/{title}")
  public RestResponse<Void> deleteArticle(@PathParam("title") String title) {
    var rows = articleService.deleteArticleByTitle(title);
    return rows == 0 ? RestResponse.notFound() : RestResponse.noContent();
  }

  /**
   * Updates current article with the new version from the body.
   *
   * @param id      the unique identifier of the article
   * @param article new version of the article
   * @return HTTP status 200 with modified time and updated article version in body
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
