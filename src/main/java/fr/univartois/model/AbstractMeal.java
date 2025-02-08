package fr.univartois.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractMeal {

  private int id;

  private LocalDate date;

  private boolean isLunchOrDinnerOtherwise;

  private Recipe associatedRecipe;

  private Family associatedFamily;

  private List<User> expectedPeople;

  private Map<User, Integer> expectedPeopleForLeftovers;
}
