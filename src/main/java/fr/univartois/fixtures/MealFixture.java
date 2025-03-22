package fr.univartois.fixtures;

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
  public void generateDataFromOutsideSource() {
    // UNUSED
  }
}
