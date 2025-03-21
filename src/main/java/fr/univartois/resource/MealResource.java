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
import fr.univartois.services.MealService;
import fr.univartois.services.SuggestedMealService;
import jakarta.annotation.security.RolesAllowed;
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
import jakarta.ws.rs.core.Response;

@Path("/meals")
@SecuritySchemes(value = {
    @SecurityScheme(
        bearerFormat = "JWT",
        scheme = "bearer",
        securitySchemeName = "AccessBearerAuthentication",
        apiKeyName = "Authroization",
        type = SecuritySchemeType.HTTP,
        description = "Uses the access token provided at authentication (Header \"Authentification\", Value \"Bearer " +
            "xxx\")",
        in = SecuritySchemeIn.HEADER
    )
})
@RolesAllowed("access")
@SecurityRequirement(name = "AccessBearerAuthentication")
public class MealResource {

  JsonWebToken jwt;

  MealService mealService;

  SuggestedMealService suggestedMealService;

  public MealResource(JsonWebToken jwt, MealService mealService, SuggestedMealService suggestedMealService) {
    this.jwt = jwt;
    this.mealService = mealService;
    this.suggestedMealService = suggestedMealService;
  }

  @Path("/suggestions")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<SuggestedMeal> getSuggestions() {
    return suggestedMealService.getSuggestedMeals(jwt);
  }

  @Path("/suggestions")
  @POST
  public Response suggestMeal(@FormParam("recipeName") String recipeName, @FormParam("date") LocalDate date,
      @FormParam("isLunch") boolean isLunch, @FormParam("participants") int participants) {
    return suggestedMealService.suggestMeal(jwt, recipeName, date, isLunch, participants);
  }

  @Path("/suggestions/{suggestedMealId}/votes")
  @GET
  public Response getVotesCount(@PathParam("suggestedMealId") int suggestedMealId) {
    return suggestedMealService.getVoteCount(jwt, suggestedMealId);
  }

  @Path("/suggestions/{suggestedMealId}/votes/{username}")
  @GET
  public Response hasVoteOnSuggestion(@PathParam("suggestedMealId") int suggestedMealId,
      @PathParam("username") String username) {
    return suggestedMealService.hasVoteOnSuggestion(jwt, suggestedMealId, username);
  }

  @Path("/suggestions/{suggestedMealId}/votes/{username}")
  @POST
  public Response voteOnSuggestion(@PathParam("suggestedMealId") int suggestedMealId,
      @PathParam("username") String username) {
    return suggestedMealService.voteOnSuggestion(jwt, suggestedMealId);
  }

  @Path("/suggestions/{suggestedMealId}/votes/{username}")
  @DELETE
  public Response unvoteOnSuggestion(@PathParam("suggestedMealId") int suggestedMealId,
      @PathParam("username") String username) {
    return suggestedMealService.unvoteOnSuggestion(jwt, suggestedMealId);
  }

  @Path("/plans/suggestions/{suggestedMealId}")
  @POST
  public Response planMealFromSuggestion(@PathParam("suggestedMealId") int suggestedMealId) {
    return suggestedMealService.planMealFromSuggestion(jwt, suggestedMealId);
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
      @FormParam("isLunch") boolean isLunch, @FormParam("participants") int participants) {
    return mealService.planMealFromRecipe(recipeId, date, isLunch, participants);
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
  public List<PlannedMeal> getMeals(@QueryParam("firstDayOfWeek") LocalDate firstDayOfWeek) {
    return mealService.listAll(firstDayOfWeek);
  }
}
