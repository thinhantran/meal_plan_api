package fr.univartois.resource;

import java.time.LocalDate;
import java.util.List;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeIn;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;

import fr.univartois.model.PlannedMeal;
import fr.univartois.model.SuggestedMeal;
import fr.univartois.repository.MealRepository;
import fr.univartois.services.MealService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/meals")
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
public class MealResource {

  @Inject
  JsonWebToken jwt;

  @Inject
  MealService mealService;
  @Inject
  MealRepository mealRepository;

  @Path("/suggestions")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<SuggestedMeal> getSuggestions() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Path("/suggestions/{recipeId}")
  @POST
  public SuggestedMeal suggestMeal(@PathParam("recipeId") int recipeId) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Path("/suggestions/{suggestedMealId}/votes")
  @GET
  public int getVotesCount(@PathParam("suggestedMealId") int suggestedMealId) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Path("/suggestions/{suggestedMealId}/votes/{username}")
  @GET
  public boolean hasVoteOnSuggestion(@PathParam("suggestedMealId") int suggestedMealId,
      @PathParam("username") String username) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Path("/suggestions/{suggestedMealId}/votes/{username}")
  @POST
  public void voteOnSuggestion(@PathParam("suggestedMealId") int suggestedMealId,
      @PathParam("username") String username) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Path("/suggestions/{suggestedMealId}/votes/{username}")
  @DELETE
  public void unvoteOnSuggestion(@PathParam("suggestedMealId") int suggestedMealId,
      @PathParam("username") String username) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Path("/plans/suggestions/{suggestedMealId}")
  @POST
  public PlannedMeal planMealFromSuggestion(@PathParam("suggestedMealId") int suggestedMealId) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Path("/plans/{plannedMealId}")
  @GET
  public PlannedMeal getPlannedMeal(@PathParam("plannedMealId") Long plannedMealId) {
    return mealService.getPlannedMeal(plannedMealId);
  }

  @Path("/plans/{plannedMealId}")
  @DELETE
  public PlannedMeal cancelPlannedMeal(@PathParam("plannedMealId") int plannedMealId) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Path("/plans")
  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public PlannedMeal planMealFromRecipe(@FormParam("recipeId") long recipeId, @FormParam("date") LocalDate date,
      @FormParam("isLunch") boolean isLunch) {
    return mealService.planMealFromRecipe(recipeId, date, isLunch);
  }

  @Path("/plans")
  @PUT
  public PlannedMeal changeMealBasedFromRecipe(@QueryParam("newRecipeId") Long newRecipeId,
      @QueryParam("date") LocalDate date, @QueryParam("isLunch") boolean isLunch) {
    return mealService.changePlannedMealsRecipe(newRecipeId, date, isLunch);
  }

  @GET
  public List<PlannedMeal> getMeals() {
    return mealService.listAll();
  }

  @GET
  @Path("/weekly")
  public List<PlannedMeal> getMeals(LocalDate firstDayOfWeek) {
    return mealService.listAll(firstDayOfWeek);
  }
}
