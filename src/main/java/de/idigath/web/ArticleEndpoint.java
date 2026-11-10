package de.idigath.web;

import de.idigath.model.ArticleDto;
import de.idigath.model.ArticleOverview;
import de.idigath.service.ArticleService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
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
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestResponse;

@Path("/api/articles")
@Tag(name = "Articles", description = "Article operations: read, search, create, update, delete.")
public class ArticleEndpoint {

    @Inject ArticleService articleService;

    @GET
    @Path("/{title}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Get an article by title",
            description = "Exact title match, case-insensitive.")
    @APIResponses({
        @APIResponse(
                responseCode = "200",
                description = "Article found",
                content = @Content(schema = @Schema(implementation = ArticleDto.class))),
        @APIResponse(responseCode = "404", description = "No article with the given title"),
        @APIResponse(
                responseCode = "504",
                description = "Database query timeout",
                content = @Content(mediaType = "text/plain"))
    })
    public RestResponse<ArticleDto> getArticle(
            @Parameter(description = "Exact article title, matched case-insensitively")
                    @PathParam("title")
                    String title) {
        var result = articleService.findArticleByTitle(title);
        return RestResponse.ok(result);
    }

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Search articles by text and/or tags",
            description =
                    "Text query is a case-insensitive substring match on the title; "
                            + "LIKE wildcards % and _ are treated literally. An article matches only if it has "
                            + "ALL given tags (case-insensitive). Null/blank tags are ignored. "
                            + "Page is 1-based, page size is 10; null or non-positive page returns the first page. "
                            + "Returns an empty list when nothing matches.")
    @APIResponses({
        @APIResponse(
                responseCode = "200",
                description = "Matching articles (possibly an empty list)",
                content = @Content(schema = @Schema(implementation = ArticleOverview.class))),
        @APIResponse(
                responseCode = "504",
                description = "Database query timeout",
                content = @Content(mediaType = "text/plain"))
    })
    public RestResponse<ArticleOverview> searchArticle(
            @Parameter(
                            description =
                                    "1-based page number; null or non-positive returns the first page")
                    @QueryParam("page")
                    Integer page,
            @Parameter(
                            description =
                                    "Case-insensitive substring to match in the title; LIKE wildcards are literal")
                    @QueryParam("text")
                    String text,
            @Parameter(
                            description =
                                    "Tags the article must have all of (AND logic); null/blank entries ignored")
                    @QueryParam("tag")
                    List<String> tags) {
        var result = articleService.searchArticleOverview(page, text, tags);
        return RestResponse.ok(result);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Create an article",
            description =
                    "Currently a stub: responds 201 with a hardcoded Location header; "
                            + "the article is not persisted yet.")
    @APIResponses({
        @APIResponse(
                responseCode = "201",
                description = "Article created; Location header points to it"),
        @APIResponse(
                responseCode = "400",
                description =
                        "Malformed request body or failed validation; response body lists the invalid fields"),
        @APIResponse(
                responseCode = "504",
                description = "Database query timeout",
                content = @Content(mediaType = "text/plain"))
    })
    public RestResponse<Void> addArticle(
            @Parameter(description = "New article payload") @Valid ArticleDto article,
            @Context UriInfo uriinfo) {
        // TODO: Implement full CRUD — save article to DB
        // TODO: AI integration — classify article (auto-tags/categories)
        // and spell-check without altering writing style before saving
        URI location = uriinfo.getAbsolutePathBuilder().path("42").build();
        return RestResponse.created(location);
    }

    @DELETE
    @Path("/{title}")
    @Operation(
            summary = "Delete an article by title",
            description = "Exact title match, case-insensitive.")
    @APIResponses({
        @APIResponse(responseCode = "204", description = "Article deleted"),
        @APIResponse(responseCode = "404", description = "No article with the given title"),
        @APIResponse(
                responseCode = "504",
                description = "Database query timeout",
                content = @Content(mediaType = "text/plain"))
    })
    public RestResponse<Void> deleteArticle(
            @Parameter(description = "Exact article title, matched case-insensitively")
                    @PathParam("title")
                    String title) {
        var rows = articleService.deleteArticleByTitle(title);
        return rows == 0 ? RestResponse.notFound() : RestResponse.noContent();
    }

    @PUT
    @Path("/{title}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Update an article",
            description =
                    "Updates an article with an exact title match, case insensitive. Responds with 200 and last-Modified header.")
    @APIResponses({
        @APIResponse(
                responseCode = "200",
                description = "Updated article with Last-Modified header",
                content = @Content(schema = @Schema(implementation = ArticleDto.class))),
        @APIResponse(
                responseCode = "400",
                description =
                        "Request body failed validation; response body lists the invalid fields"),
        @APIResponse(responseCode = "404", description = "No article with this title"),
        @APIResponse(
                responseCode = "504",
                description = "Database query timeout",
                content = @Content(mediaType = "text/plain"))
    })
    public RestResponse<ArticleDto> editArticle(
            @Parameter(description = "Exact article title, matched case-insensitively")
                    @PathParam("title")
                    String title,
            @Parameter(description = "New version of the article") @Valid ArticleDto article) {
        var result = articleService.editArticle(article, title);
        Date updatedAt = Date.from(result.updatedAt().atZone(ZoneId.systemDefault()).toInstant());
        return RestResponse.ResponseBuilder.ok(result).lastModified(updatedAt).build();
    }
}
