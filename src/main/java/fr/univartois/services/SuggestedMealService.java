package fr.univartois.services;

import static fr.univartois.model.MemberRole.Role.ADMIN;
import static fr.univartois.model.MemberRole.Role.MANAGER;
import static fr.univartois.model.MemberRole.Role.PROPOSER;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.eclipse.microprofile.jwt.JsonWebToken;

import fr.univartois.model.Family;
import fr.univartois.model.PlannedMeal;
import fr.univartois.model.Recipe;
import fr.univartois.model.SuggestedMeal;
import fr.univartois.model.User;
import fr.univartois.repository.FamilyRepository;
import fr.univartois.repository.MealRepository;
import fr.univartois.repository.SuggestedMealRepository;
import fr.univartois.repository.UserRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class SuggestedMealService {

  SuggestedMealRepository suggestedMealRepository;

  MealRepository mealRepository;

  FamilyRepository familyRepository;

  UserRepository userRepository;

  RecipeService recipeService;

  public SuggestedMealService(SuggestedMealRepository suggestedMealRepository, FamilyRepository familyRepository,
      UserRepository userRepository, RecipeService recipeService) {
    this.suggestedMealRepository = suggestedMealRepository;
    this.familyRepository = familyRepository;
    this.userRepository = userRepository;
    this.recipeService = recipeService;
  }

  public List<SuggestedMeal> getSuggestedMeals(JsonWebToken jwt) {
    User user = Objects.requireNonNull(userRepository.findByUsername(Objects.requireNonNull(jwt.getSubject())));
    Family family = Objects.requireNonNull(familyRepository.findByUser(user));
    return suggestedMealRepository.list("family = ?1", Sort.ascending("date"), family);
  }

  public List<SuggestedMeal> getSuggestedMealsStartingToday(JsonWebToken jwt) {
    User user = Objects.requireNonNull(userRepository.findByUsername(Objects.requireNonNull(jwt.getSubject())));
    Family family = Objects.requireNonNull(familyRepository.findByUser(user));
    return suggestedMealRepository.list("family = ?1 AND date >= current_date()", Sort.ascending("date"), family);
  }

  @Transactional
  public Response suggestMeal(JsonWebToken jwt, String recipeName, LocalDate date, boolean isLunch) {
    User user = Objects.requireNonNull(userRepository.findByUsername(Objects.requireNonNull(jwt.getSubject())));
    Response response = checksToSuggestMeal(user);
    if (response != null) return response;
    Recipe recipe = Objects.requireNonNull(recipeService.getRecipeByName(Objects.requireNonNull(recipeName)));
    SuggestedMeal suggestedMeal = new SuggestedMeal();
    suggestedMeal.setAssociatedRecipe(recipe);
    suggestedMeal.setAssociatedFamily(user.getMemberRole().getFamily());
    suggestedMeal.setDate(date);
    suggestedMeal.setLunchOrDinnerOtherwise(isLunch);
    suggestedMealRepository.persist(suggestedMeal);
    return Response.status(Response.Status.CREATED).entity(suggestedMeal).build();
  }

  public Response getVoteCount(JsonWebToken jwt, long suggestedMealId) {
    User user = userRepository.findByUsername(Objects.requireNonNull(jwt.getSubject()));
    SuggestedMeal suggestedMeal = suggestedMealRepository.findById(suggestedMealId);
    Response response = getResponseOfCheckOnUserAndMeal(user, suggestedMeal);
    if (response != null) return response;
    return Response.status(Response.Status.OK).entity(suggestedMeal.getVotes().size()).build();
  }

  public Response hasVoteOnSuggestion(JsonWebToken jwt, long suggestedMealId, String username) {
    User targetUser = userRepository.findByUsername(Objects.requireNonNull(username));
    User user = userRepository.findByUsername(Objects.requireNonNull(jwt.getSubject()));
    SuggestedMeal suggestedMeal = suggestedMealRepository.findById(suggestedMealId);
    Response response = getResponseOfCheckOnUserAndMeal(user, suggestedMeal);
    if (response != null) return response;
    if (targetUser == null || !Objects.equals(targetUser.getMemberRole().getFamily(),
        user.getMemberRole().getFamily())) {
      return Response.status(Response.Status.OK).entity(false).build();
    }
    return Response.status(Response.Status.OK).entity(suggestedMeal.getVotes().contains(targetUser)).build();
  }

  public Response voteOnSuggestion(JsonWebToken jwt, long suggestedMealId) {
    User user = userRepository.findByUsername(Objects.requireNonNull(jwt.getSubject()));
    SuggestedMeal suggestedMeal = suggestedMealRepository.findById(suggestedMealId);
    Response response = getResponseOfCheckOnUserAndMeal(user, suggestedMeal);
    if (response != null) return response;
    suggestedMeal.getVotes().add(user);
    suggestedMealRepository.persist(suggestedMeal);
    return Response.status(Response.Status.NO_CONTENT).build();
  }

  public Response unvoteOnSuggestion(JsonWebToken jwt, long suggestedMealId) {
    User user = userRepository.findByUsername(Objects.requireNonNull(jwt.getSubject()));
    SuggestedMeal suggestedMeal = suggestedMealRepository.findById(suggestedMealId);
    Response response = getResponseOfCheckOnUserAndMeal(user, suggestedMeal);
    if (response != null) return response;
    suggestedMeal.getVotes().remove(user);
    suggestedMealRepository.persist(suggestedMeal);
    return Response.status(Response.Status.NO_CONTENT).build();
  }

  public Response planMealFromSuggestion(JsonWebToken jwt, long suggestedMealId) {
    User user = userRepository.findByUsername(Objects.requireNonNull(jwt.getSubject()));
    SuggestedMeal suggestedMeal = suggestedMealRepository.findById(suggestedMealId);
    Response response = checksToPlanMeal(user, suggestedMeal);
    if (response != null) return response;
    PlannedMeal plannedMeal = new PlannedMeal(suggestedMeal);
    suggestedMealRepository.deleteById(suggestedMealId);
    mealRepository.persist(plannedMeal);
    suggestedMealRepository.delete(
        "DELETE FROM SuggestedMeal s WHERE s.date = ?1 AND s.isLunchOrDinnerOtherwise = ?2",
        plannedMeal.getDate(), plannedMeal.isLunchOrDinnerOtherwise()
    );
    return Response.status(Response.Status.CREATED).entity(plannedMeal).build();
  }

  public Response checkForUser(User user) {
    return user == null ? Response.status(Response.Status.UNAUTHORIZED).build() : null;
  }

  public Response checkForSuggestedMeal(SuggestedMeal suggestedMeal) {
    return suggestedMeal == null ? Response.status(Response.Status.NOT_FOUND).build() : null;
  }

  public Response checkForRightsOnSuggestedMeal(User user, SuggestedMeal suggestedMeal) {
    return !Objects.equals(Objects.requireNonNull(suggestedMeal.getAssociatedFamily()),
        Objects.requireNonNull(user.getMemberRole().getFamily()))
        ? Response.status(Response.Status.FORBIDDEN).build() : null;
  }

  public Response checkForRightToSuggestMeal(User user) {
    return List.of(PROPOSER, MANAGER, ADMIN).contains(user.getMemberRole().getCategory())
        ? Response.status(Response.Status.FORBIDDEN).build() : null;
  }

  public Response checkForRightToPlanMeal(User user) {
    return List.of(MANAGER, ADMIN).contains(user.getMemberRole().getCategory())
        ? Response.status(Response.Status.FORBIDDEN).build() : null;
  }

  public Response checksToSuggestMeal(User user) {
    Response responseForUser = checkForUser(user);
    if (responseForUser != null) return responseForUser;
    return checkForRightToSuggestMeal(user);
  }

  public Response checksToPlanMeal(User user, SuggestedMeal suggestedMeal) {
    Response responseForUser = getResponseOfCheckOnUserAndMeal(user, suggestedMeal);
    if (responseForUser != null) return responseForUser;
    return checkForRightToPlanMeal(user);
  }

  private Response getResponseOfCheckOnUserAndMeal(User user, SuggestedMeal suggestedMeal) {
    Response responseForUser = checkForUser(user);
    if (responseForUser != null) {
      return responseForUser;
    }
    Response responseForMeal = checkForSuggestedMeal(suggestedMeal);
    if (responseForMeal != null) {
      return responseForMeal;
    }
    Response responseForUserOnMeal = checkForRightsOnSuggestedMeal(user, suggestedMeal);
    if (responseForUserOnMeal != null) {
      return responseForUserOnMeal;
    }
    return null;
  }
}
