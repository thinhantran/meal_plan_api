package fr.univartois.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.eclipse.microprofile.jwt.JsonWebToken;

import fr.univartois.dtos.Message;
import fr.univartois.model.Family;
import fr.univartois.model.MemberRole;
import fr.univartois.model.PlannedMeal;
import fr.univartois.model.Recipe;
import fr.univartois.model.User;
import fr.univartois.repository.MealRepository;
import fr.univartois.repository.SuggestedMealRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class MealService {

  UserService userService;

  MealRepository mealRepository;

  RecipeService recipeService;

  SuggestedMealRepository suggestedMealRepository;

  SuggestedMealService suggestedMealService;

  public MealService(UserService userService, MealRepository mealRepository, RecipeService recipeService,
      SuggestedMealRepository suggestedMealRepository, SuggestedMealService suggestedMealService) {
    this.userService = userService;
    this.mealRepository = mealRepository;
    this.recipeService = recipeService;
    this.suggestedMealRepository = suggestedMealRepository;
    this.suggestedMealService = suggestedMealService;
  }

  public List<PlannedMeal> listAll(JsonWebToken jwt) {
    Family family = userService.findByUsername(jwt.getSubject()).getMemberRole().getFamily();
    return mealRepository.find("associatedFamily", Sort.ascending("date", "isLunchOrDinnerOtherwise"), family).list();
  }

  public List<PlannedMeal> listAll(JsonWebToken jwt, LocalDate firstDayOfWeek) {
    Family family = userService.findByUsername(jwt.getSubject()).getMemberRole().getFamily();
    return mealRepository.list(
        "from PlannedMeal m where m.date >= ?1 and m.date < ?2 and m.associatedFamily = ?3",
        Sort.ascending("date", "isLunchOrDinnerOtherwise"),
        firstDayOfWeek,
        firstDayOfWeek.plusWeeks(1),
        family
    );
  }

  public PlannedMeal getPlannedMeal(Long id) {
    return mealRepository.findById(id);
  }

  @Transactional
  public Response planMealFromRecipe(JsonWebToken jsonWebToken, Long recipeId, LocalDate date, boolean isLunch,
      int participants) {
    User user = userService.findByUsername(jsonWebToken.getSubject());
    if (user == null || user.getMemberRole() == null || user.getMemberRole().getFamily() == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    Response check = suggestedMealService.checkForRightToPlanMeal(user);
    if (check != null) {
      return check;
    }
    if (!List.of(MemberRole.Role.ADMIN, MemberRole.Role.MANAGER).contains(user.getMemberRole().getCategory())) {
      return Response.status(Response.Status.FORBIDDEN).build();
    }
    Recipe recipe = recipeService.getRecipe(recipeId);
    if (recipe == null) {
      throw new IllegalArgumentException("Recipe not found");
    }
    PlannedMeal meal = new PlannedMeal();
    meal.setDate(date);
    meal.setLunchOrDinnerOtherwise(isLunch);
    meal.setAssociatedRecipe(recipe);
    meal.setAssociatedFamily(user.getMemberRole().getFamily());
    meal.setNumberOfParticipants(participants);
    mealRepository.persist(meal);
    suggestedMealRepository.delete(
        "DELETE FROM SuggestedMeal s WHERE s.date = ?1 AND s.isLunchOrDinnerOtherwise = ?2 AND s.associatedFamily = ?3",
        meal.getDate(), meal.isLunchOrDinnerOtherwise(), meal.getAssociatedFamily()
    );
    return Response.status(Response.Status.CREATED).entity(meal).build();
  }

  @Transactional
  public Response changePlannedMealsRecipe(JsonWebToken jsonWebToken, Long mealId, Long newRecipeId,
      LocalDate newDate, Boolean newIsLunch, Integer participants) {
    User user = userService.findByUsername(jsonWebToken.getSubject());
    PlannedMeal meal = getPlannedMeal(mealId);
    Response check = suggestedMealService.checksToPlanMeal(user, meal);
    if (check != null) {
      return check;
    }
    Recipe recipe = recipeService.getRecipe(newRecipeId);
    if (recipe == null) {
      return Response.status(Response.Status.NOT_FOUND).entity(new Message("New Recipe not found")).build();
    }
    if (newDate != null) {
      meal.setDate(newDate);
    }
    if (newIsLunch != null) {
      meal.setLunchOrDinnerOtherwise(newIsLunch);
    }
    meal.setAssociatedRecipe(recipe);
    if (participants != null) {
      meal.setNumberOfParticipants(participants);
    }
    return Response.status(Response.Status.ACCEPTED).entity(meal).build();
  }

  @Transactional
  public Response cancelPlannedMeal(JsonWebToken jsonWebToken, Long id) {
    User user = userService.findByUsername(jsonWebToken.getSubject());
    if (user == null || user.getMemberRole() == null || user.getMemberRole().getFamily() == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    PlannedMeal meal = mealRepository.findById(id);
    Response check = suggestedMealService.checksToPlanMeal(user, meal);
    if (check != null) {
      return check;
    }
    mealRepository.delete(meal);
    return Response.status(Response.Status.NO_CONTENT).build();
  }
}
