package fr.univartois.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IngredientFridgeQuantity {

  private int ingredientFridgeQuantityId;

  private Ingredient ingredient;

  private Fridge fridge;

  private double quantity;
}
