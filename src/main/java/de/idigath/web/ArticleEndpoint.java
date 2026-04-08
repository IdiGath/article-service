package de.idigath.web;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/articles")
public class ArticleEndpoint {

  @GET
  @Path("/{id}")
  @Produces(MediaType.TEXT_PLAIN)
  public String hello(@PathParam("id") int id) {
    return "Hello from Quarkus REST that is my ID: " + id;
  }
}
