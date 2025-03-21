package fr.univartois.services;

import java.time.LocalDate;
import java.util.List;

import fr.univartois.model.PlannedMeal;
import fr.univartois.model.Recipe;
import fr.univartois.repository.MealRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class MealService {

  MealRepository mealRepository;

  RecipeService recipeService;

  public MealService(MealRepository mealRepository, RecipeService recipeService) {
    this.mealRepository = mealRepository;
    this.recipeService = recipeService;
  }

  public List<PlannedMeal> listAll() {
    return mealRepository.findAll().list();
  }

  public List<PlannedMeal> listAll(LocalDate firstDayOfWeek) {
    return mealRepository.list(
        "from PlannedMeal m where m.date >= ?1 and m.date < ?2",
        firstDayOfWeek,
        firstDayOfWeek.plusWeeks(1)
    );
  }

  public PlannedMeal getPlannedMeal(Long id) {
    return mealRepository.findById(id);
  }

  @Transactional
  public PlannedMeal planMealFromRecipe(Long recipeId, LocalDate date, boolean isLunch, int participants) {
    Recipe recipe = recipeService.getRecipe(recipeId);
    if (recipe == null) {
      throw new IllegalArgumentException("Recipe not found");
    }
    PlannedMeal meal = new PlannedMeal();
    meal.setDate(date);
    meal.setLunchOrDinnerOtherwise(isLunch);
    meal.setAssociatedRecipe(recipe);
    meal.setNumberOfParticipants(participants);
    mealRepository.persist(meal);
    return meal;
  }

  @Transactional
  public PlannedMeal changePlannedMealsRecipe(Long newRecipeId, LocalDate currentDate, boolean isLunch) {
    Recipe recipe = recipeService.getRecipe(newRecipeId);
    if (recipe == null) {
      throw new IllegalArgumentException("Recipe not found");
    }
    PlannedMeal meal = mealRepository.find("date = ?1 and isLunchOrDinnerOtherwise = ?2",
            currentDate, isLunch).singleResultOptional()
        .orElseThrow(() -> new IllegalArgumentException("No meal found at this date"));
    meal.setAssociatedRecipe(recipe);
    return meal;
  }
}
