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
    // UNUSED
  }
}
