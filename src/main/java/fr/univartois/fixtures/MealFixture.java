package fr.univartois.fixtures;

import java.time.LocalDate;

import fr.univartois.model.PlannedMeal;
import fr.univartois.repository.MealRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class MealFixture extends Fixture {

  @Inject
  MealRepository mealRepository;

  @Transactional
  @Override
  public void generateRealData() {

  }

  @Transactional
  @Override
  public void generateSingleFakeData() {

  }

  @Transactional
  @Override
  public void generateFakeData() {
    PlannedMeal plannedMeal = new PlannedMeal();
    plannedMeal.setDate(LocalDate.now());
    plannedMeal.setLunchOrDinnerOtherwise(true);
    mealRepository.persist(plannedMeal);
  }
}
