package fr.univartois.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Recipe {

  private int recipeId;

  private String name;

  private String description;

  private List<IngredientRecipeQuantity> ingredients;
}
