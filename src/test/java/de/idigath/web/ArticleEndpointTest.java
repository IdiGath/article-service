package de.idigath.web;

import static io.restassured.RestAssured.when;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link ArticleEndpoint} class. Each test scenario verifies the
 * expected HTTP status
 * code for a specific case. This class focuses solely on HTTP response
 * validation and does not
 * check the response content.
 *
 * <p>
 * The correctness of data mapping and database content is verified in the
 * Mapper and Entity test
 * classes. All test classes use a dedicated test container to ensure isolated
 * test data for each
 * test execution.
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
  public void getArticleOverview_noParameters_returns200() {
    when().get("/").then().statusCode(200);
  }

  @Test
  public void getArticleOverview_withPageParameter_returns200() {
    when().get("?page=1").then().statusCode(200);
  }

}
