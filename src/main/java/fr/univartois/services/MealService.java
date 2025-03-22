package fr.univartois.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.eclipse.microprofile.jwt.JsonWebToken;

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

  public MealService(UserService userService, MealRepository mealRepository, RecipeService recipeService, SuggestedMealRepository suggestedMealRepository) {
    this.userService = userService;
    this.mealRepository = mealRepository;
    this.recipeService = recipeService;
    this.suggestedMealRepository = suggestedMealRepository;
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
  public Response planMealFromRecipe(JsonWebToken jsonWebToken, Long recipeId, LocalDate date, boolean isLunch, int participants) {
    User user = userService.findByUsername(jsonWebToken.getSubject());
    if (user == null || user.getMemberRole() == null || user.getMemberRole().getFamily() == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
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
  public PlannedMeal changePlannedMealsRecipe(Long newRecipeId, LocalDate currentDate, boolean isLunch, int participants) {
    Recipe recipe = recipeService.getRecipe(newRecipeId);
    if (recipe == null) {
      throw new IllegalArgumentException("Recipe not found");
    }
    PlannedMeal meal = mealRepository.find("date = ?1 and isLunchOrDinnerOtherwise = ?2",
            currentDate, isLunch).singleResultOptional()
        .orElseThrow(() -> new IllegalArgumentException("No meal found at this date"));
    meal.setAssociatedRecipe(recipe);
    meal.setNumberOfParticipants(participants);
    return meal;
  }

  @Transactional
  public Response cancelPlannedMeal(JsonWebToken jsonWebToken, Long id) {
    User user = userService.findByUsername(jsonWebToken.getSubject());
    if (user == null || user.getMemberRole() == null || user.getMemberRole().getFamily() == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    Family family = userService.findByUsername(jsonWebToken.getSubject()).getMemberRole().getFamily();
    PlannedMeal meal = mealRepository.findById(id);
    if (meal == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    if (!Objects.equals(meal.getAssociatedFamily(), family)) {
      return Response.status(Response.Status.FORBIDDEN).build();
    }
    mealRepository.delete(meal);
    return Response.status(Response.Status.NO_CONTENT).build();
  }
}
