package fr.univartois.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IngredientRecipeQuantity {

  private int ingredientRecipeQuantityId;

  private Ingredient ingredient;

  private Recipe recipe;

  private double quantity;
}
