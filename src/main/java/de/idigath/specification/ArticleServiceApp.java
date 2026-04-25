package de.idigath.specification;

import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;

/**
 * Application class for the OpenApi specification. Quarkus does not create the class automaticly,
 * because of this this manual created class is settet under service specification package.
 */
@OpenAPIDefinition(
    info =
        @Info(
            title = "Article Service API",
            version = "1.0.0",
            contact =
                @Contact(
                    name = "API support",
                    url = "http://idiblog.com/contact",
                    email = "i.izrailtenko@mailbox.org"),
            license = @License(name = "MIT", url = "https://opensource.org/license/MIT")))
public class ArticleServiceApp extends Application {}
