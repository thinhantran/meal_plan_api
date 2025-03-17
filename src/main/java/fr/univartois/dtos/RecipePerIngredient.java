package fr.univartois.dtos;

import java.util.Optional;

import fr.univartois.model.Ingredient;
import fr.univartois.model.IngredientCategory;
import fr.univartois.model.Recipe;

public record RecipePerIngredient(String ingredientName, String recipeName, String thumbnailUrl, IngredientCategory category, String quantity) {

  public RecipePerIngredient(Recipe recipe, Ingredient ingredient) {
    this(
        Optional.of(ingredient).orElseThrow(NullPointerException::new).getName(),
        Optional.of(recipe).orElseThrow(NullPointerException::new).getName(),
        Optional.of(recipe).orElseThrow(NullPointerException::new).getThumbnailUrl(),
        Optional.of(recipe).orElseThrow(NullPointerException::new).getCategory(),
        Optional.of(Optional.of(recipe).orElseThrow(NullPointerException::new).getIngredients())
            .orElseThrow(NullPointerException::new)
            .stream()
            .filter(iRQ ->
                Optional.of(ingredient).orElseThrow(NullPointerException::new).equals(iRQ.getIngredient())
            )
            .findFirst().orElseThrow(() -> new IllegalArgumentException("The ingredient could not be found"))
            .getQuantity()
    );
  }
}
