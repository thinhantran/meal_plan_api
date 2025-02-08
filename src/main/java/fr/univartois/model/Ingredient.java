package fr.univartois.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Ingredient {

  private int ingredientId;

  private String name;

  private String measurementUnit;

  private List<DietaryRestriction> associatedDietaryRestrictions;
}
