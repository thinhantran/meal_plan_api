package fr.univartois.resource;

import java.util.List;

import fr.univartois.model.Recipe;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/recipes")
public class RecipeResource {

  @GET
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  public List<Recipe> search(@QueryParam("terms") List<String> terms) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Recipe create(Recipe recipe) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Recipe update(Recipe recipe) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @DELETE
  @Consumes(MediaType.APPLICATION_JSON)
  public void delete(Recipe recipe) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
