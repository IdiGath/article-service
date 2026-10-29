package de.idigath.web;

import static io.restassured.RestAssured.when;

import de.idigath.service.ArticleService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.persistence.PersistenceException;
import java.sql.SQLTimeoutException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests for the {@link ArticleEndpoint} class using mocks to simulate
 * exceptional scenarios. This
 * class focuses on testing error handling and timeout conditions by mocking the
 * service layer.
 *
 * <p>
 * Unlike integration tests that use a real database, this class uses
 * {@link InjectMock} to
 * replace the {@link ArticleService} with a mock, allowing controlled
 * simulation of database
 * timeout conditions and other edge cases.
 *
 * @see ArticleEndpoint
 * @see SQLTimeoutExceptionMapper
 */
@QuarkusTest
@TestHTTPEndpoint(ArticleEndpoint.class)
public class ArticleEndpointMockTest {

  @InjectMock
  ArticleService articleService;

  @Test
  void getArticle_serviceThrowsTimeout_returns504() {

    PersistenceException persistenceException = new PersistenceException(new SQLTimeoutException("Query timeout"));

    Mockito.when(articleService.findArticleByTitle("NixOS for Java Developers"))
        .thenThrow(persistenceException);

    when().get("/{title}", "NixOS for Java Developers").then().statusCode(504);
  }

  @Test
  void deleteArticle_nonExistentTitle_returns404() {

    Mockito.when(articleService.deleteArticleByTitle("no article"))
        .thenReturn(0L);

    when().delete("/{title}", "no article").then().statusCode(404);
  }

  @Test
  void deleteArticle_existingTitle_returns204() {
    Mockito.when(articleService.deleteArticleByTitle("article title"))
        .thenReturn(1L);

    when().delete("/{title}", "article title").then().statusCode(204);
  }
}
