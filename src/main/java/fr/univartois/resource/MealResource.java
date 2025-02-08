package fr.univartois.resource;

import java.util.List;

import fr.univartois.model.PlannedMeal;
import fr.univartois.model.SuggestedMeal;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/meals/{familyId}")
public class MealResource {

  @Path("/suggestions")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<SuggestedMeal> getSuggestions(@PathParam("familyId") int familyId) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Path("/suggestions/{recipeId}")
  @POST
  public SuggestedMeal suggestMeal(@PathParam("familyId") int familyId, @PathParam("recipeId") int recipeId) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Path("/suggestions/{suggestedMealId}/votes")
  @GET
  public int getVotesCount(@PathParam("familyId") int familyId, @PathParam("suggestedMealId") int suggestedMealId) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Path("/suggestions/{suggestedMealId}/votes/{username}")
  @GET
  public boolean hasVoteOnSuggestion(@PathParam("familyId") int familyId,
      @PathParam("suggestedMealId") int suggestedMealId,
      @PathParam("username") String username) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Path("/suggestions/{suggestedMealId}/votes/{username}")
  @POST
  public void voteOnSuggestion(@PathParam("familyId") int familyId, @PathParam("suggestedMealId") int suggestedMealId,
      @PathParam("username") String username) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Path("/suggestions/{suggestedMealId}/votes/{username}")
  @DELETE
  public void unvoteOnSuggestion(@PathParam("familyId") int familyId, @PathParam("suggestedMealId") int suggestedMealId,
      @PathParam("username") String username) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Path("/plans/suggestions/{suggestedMealId}")
  @POST
  public PlannedMeal planMealFromSuggestion(@PathParam("familyId") int familyId,
      @PathParam("suggestedMealId") int suggestedMealId) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Path("/plans/{plannedMealId}")
  @GET
  public PlannedMeal getPlannedMeal(@PathParam("familyId") int familyId,
      @PathParam("plannedMealId") int plannedMealId) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Path("/plans/{plannedMealId}")
  @DELETE
  public PlannedMeal cancelPlannedMeal(@PathParam("familyId") int familyId,
      @PathParam("plannedMealId") int plannedMealId) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Path("/plans/{recipeId}")
  @POST
  public PlannedMeal planMealFromRecipe(@PathParam("familyId") int familyId,
      @PathParam("recipeId") int recipeId) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Path("/plans/{plannedMealId}/{recipeId}")
  @PUT
  public PlannedMeal changeMealBasedFromRecipe(@PathParam("familyId") int familyId,
      @PathParam("plannedMealId") int plannedMealId, @PathParam("recipeId") int recipeId) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
