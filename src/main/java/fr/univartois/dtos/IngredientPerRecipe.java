package fr.univartois.dtos;

import java.util.Optional;

import fr.univartois.model.Ingredient;
import fr.univartois.model.Recipe;

public record IngredientPerRecipe(String recipeName, String ingredientName, String quantity, String description) {

  public IngredientPerRecipe(Ingredient ingredient, Recipe recipe) {
    this(
        Optional.of(recipe).orElseThrow(NullPointerException::new).getName(),
        Optional.of(ingredient).orElseThrow(NullPointerException::new).getName(),
        Optional.of(Optional.of(ingredient).orElseThrow(NullPointerException::new).getRecipes())
            .orElseThrow(() -> new IllegalArgumentException(recipe.getName() + " does not use " + ingredient.getName()))
            .stream()
            .filter(iQR ->
                Optional.of(recipe).orElseThrow(NullPointerException::new).equals(iQR.getRecipe())
            )
            .findFirst().orElseThrow(() -> new IllegalArgumentException(recipe.getName() + " does not use " + ingredient.getName()))
            .getQuantity(),
        Optional.of(recipe).orElseThrow(NullPointerException::new).getDescription()
    );
  }
}
