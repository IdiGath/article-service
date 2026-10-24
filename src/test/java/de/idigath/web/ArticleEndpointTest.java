package de.idigath.web;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for the {@link ArticleEndpoint} class. Verifies HTTP status
 * codes and response content for article search scenarios.
 *
 * <p>
 * The correctness of data mapping and database content is verified in the
 * Mapper and Entity test classes. All test classes use a dedicated test
 * container to ensure isolated test data for each test execution.
 */
@QuarkusTest
@TestHTTPEndpoint(ArticleEndpoint.class)
public class ArticleEndpointTest {

  @Test
  public void getArticle_existingId_returns200() {
    when().get("/" + "151").then().statusCode(200);
  }

  @Test
  public void getArticle_nonExistentId_returns404() {
    when().get("/" + "9999999").then().statusCode(404);
  }

  @Test
  public void getArticle_nonNumericId_returns400() {
    when().get("/" + "123A").then().statusCode(400);
  }

  @Test
  public void searchArticle_noParameters_returnsAllArticles() {
    when()
        .get("/search")
        .then()
        .statusCode(200)
        .body("totalArticles", equalTo(5))
        .body("articleList.size()", equalTo(5));
  }

  @Test
  public void searchArticle_withPageParameter_returnsFirstPage() {
    when()
        .get("/search?page=1")
        .then()
        .statusCode(200)
        .body("totalArticles", equalTo(5))
        .body("articleList.size()", equalTo(5));
  }

  @Test
  public void searchArticle_pageTwo_returnsEmptyListButTotalFive() {
    when()
        .get("/search?page=2")
        .then()
        .statusCode(200)
        .body("totalArticles", equalTo(5))
        .body("articleList.size()", equalTo(0));
  }

  @Test
  public void searchArticle_singleTagJava_returnsThreeArticles() {
    when()
        .get("/search?tag=Java")
        .then()
        .statusCode(200)
        .body("totalArticles", equalTo(3))
        .body("articleList.size()", equalTo(3));
  }

  @Test
  public void searchArticle_twoTagsJavaAndPerformance_returnsOneMatchingArticle() {
    when()
        .get("/search?page=1&tag=Java&tag=Performance")
        .then()
        .statusCode(200)
        .body("totalArticles", equalTo(1))
        .body("articleList.size()", equalTo(1))
        .body("articleList[0].title", equalTo("Java 21: New Features"));
  }

}
