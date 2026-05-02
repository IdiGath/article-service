package de.idigath.web;

import static io.restassured.RestAssured.when;

import de.idigath.server.PostgresContainer;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(PostgresContainer.class)
@TestHTTPEndpoint(ArticleEndpoint.class)
public class ArticleEndpointTest {

  @Test
  public void testGetArticleHappyCase() {
    when().get("/" + "151").then().statusCode(200);
  }
}
