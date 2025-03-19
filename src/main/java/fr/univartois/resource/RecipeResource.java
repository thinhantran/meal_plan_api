package fr.univartois.resource;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeIn;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;

import fr.univartois.dtos.RecipePerIngredient;
import fr.univartois.model.IngredientCategory;
import fr.univartois.model.Recipe;
import fr.univartois.services.RecipeService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/recipes")
@SecuritySchemes(value = {
    @SecurityScheme(
        bearerFormat = "JWT",
        scheme = "bearer",
        securitySchemeName = "AccessBearerAuthentication",
        apiKeyName = "Authroization",
        type = SecuritySchemeType.HTTP,
        description = "Uses the access token provided at authentication (Header \"Authentification\", Value \"Bearer xxx\")",
        in = SecuritySchemeIn.HEADER
    )
})
@RolesAllowed("access")
@SecurityRequirement(name = "AccessBearerAuthentication")
public class RecipeResource {

  @Inject
  RecipeService recipeService;

  /*
  @GET
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  public List<Recipe> search(@QueryParam("terms") List<String> terms) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  */

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<Recipe> search(@QueryParam("name") String name) {
    return recipeService.searchRecipesByName(name);
  }

  @Path("/all")
  @GET
  public List<Recipe> getAllIngredients(@QueryParam("offset") int offset, @QueryParam("limit") int limit) {
    return recipeService.getRecipes(offset, limit);
  }

  @Path("/byCategory")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<Recipe> searchByCategory(@QueryParam("category") IngredientCategory category) {
    return recipeService.searchRecipesByCategory(category);
  }

  @Path("/byIngredient")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<RecipePerIngredient> searchPerIngredient(@QueryParam("name") String name) {
    return recipeService.searchRecipesByIngredient(name);
  }
}
