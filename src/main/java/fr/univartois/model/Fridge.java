package fr.univartois.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Fridge {

  private int fridgeId;

  private Family family;

  private List<IngredientFridgeQuantity> ingredients;

  private List<Utensil> ustensils;
}
