package de.idigath.project;

import static io.restassured.RestAssured.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import de.idigath.entity.Article;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Boots the application with Hibernate schema generation set to {@code drop-and-create} on a
 * dedicated Dev Services database, so Postgres executes the DDL generated from the entities at
 * startup. An invalid {@code columnDefinition} in an entity (e.g. a typo like {@code "TEST"}) makes
 * the generated DDL fail — the table or column is then missing or mistyped in {@code
 * information_schema}, which fails the assertions below. Note that Hibernate only logs DDL errors
 * during drop-and-create, so the boot alone is not a sufficient check.
 */
@QuarkusTest
@TestProfile(SchemaGenerationTest.DropAndCreateProfile.class)
public class SchemaGenerationTest {

    @Test
    void dropAndCreate_generatedEntityDdlAcceptedByPostgres() {
        when().get("/api/articles/search").then().statusCode(200).body("totalArticles", equalTo(0));

        String citationType =
                QuarkusTransaction.requiringNew()
                        .call(() ->
                                (String)
                                        Article.getEntityManager()
                                                .createNativeQuery(
                                                        "SELECT data_type FROM information_schema.columns WHERE table_name = 'sources' AND column_name = 'citation'")
                                                .getSingleResult());
        assertThat(citationType).isEqualTo("text");
    }

    public static class DropAndCreateProfile implements QuarkusTestProfile {
        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of(
                    "quarkus.hibernate-orm.database.generation", "drop-and-create",
                    "quarkus.liquibase.migrate-at-start", "false",
                    "quarkus.datasource.devservices.db-name", "article-ddl-check");
        }
    }
}
