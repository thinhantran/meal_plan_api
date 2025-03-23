package fr.univartois.model;

import java.time.LocalDate;

public abstract class AbstractMeal {

  public abstract Long getId();

  public abstract void setId(Long id);

  public abstract LocalDate getDate();

  public abstract void setDate(LocalDate date);

  public abstract boolean isLunchOrDinnerOtherwise();

  public abstract void setLunchOrDinnerOtherwise(boolean lunchOrDinnerOtherwise);

  public abstract int getNumberOfParticipants();

  public abstract void setNumberOfParticipants(int numberOfParticipants);

  public abstract Recipe getAssociatedRecipe();

  public abstract void setAssociatedRecipe(Recipe associatedRecipe);

  public abstract Family getAssociatedFamily();

  public abstract void setAssociatedFamily(Family associatedFamily);
}
