package fr.univartois.fixtures;

import java.time.LocalDate;

import fr.univartois.model.IngredientCategory;
import fr.univartois.model.PlannedMeal;
import fr.univartois.model.Recipe;
import fr.univartois.repository.MealRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class MealFixture extends Fixture {

  MealRepository mealRepository;

  public MealFixture(MealRepository mealRepository) {
    this.mealRepository = mealRepository;
  }

  @Transactional
  @Override
  public void generateRealData() {
    // UNUSED
  }

  @Transactional
  @Override
  public void generateSingleFakeData() {
    // UNUSED
  }

  @Transactional
  @Override
  public void generateFakeData() {
    Recipe recipe = new Recipe();
    recipe.setRecipeId(999999L);
    recipe.setName("Mon repas");
    recipe.setCategory(IngredientCategory.MISCELLANEOUS);
    recipe.setThumbnailUrl("https://static.wikia.nocookie.net/mario/images/3/38/Wario_illustration_SMP.png/revision/latest/scale-to-width-down/1000?cb=20240204215639&path-prefix=fr");
    PlannedMeal plannedMeal = new PlannedMeal();
    plannedMeal.setDate(LocalDate.now());
    plannedMeal.setLunchOrDinnerOtherwise(true);
    plannedMeal.setAssociatedRecipe(recipe);
    mealRepository.persist(plannedMeal);
  }
}
