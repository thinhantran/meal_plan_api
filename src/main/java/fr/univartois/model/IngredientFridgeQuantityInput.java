package fr.univartois.model;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IngredientFridgeQuantityInput {

  private String ingredientName;

  private LocalDate date;

  private double quantity;

  private IngredientUnit measurementUnit;

  private IngredientCategory ingredientCategory;
}
