package fr.univartois.services;

import java.util.ArrayList;
import java.util.List;

import fr.univartois.dtos.RecipePerIngredient;
import fr.univartois.model.Ingredient;
import fr.univartois.model.IngredientCategory;
import fr.univartois.model.Recipe;
import fr.univartois.repository.IngredientRepository;
import fr.univartois.repository.RecipeRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RecipeService {

  @Inject
  RecipeRepository recipeRepository;

  @Inject
  IngredientRepository ingredientRepository;

  public Recipe getRecipe(Long id) {
    return recipeRepository.findById(id);
  }

  public List<Recipe> getRecipes(int offset, int limit) {
    return recipeRepository.findAll(Sort.ascending("recipeId")).page(Page.of(offset, limit)).list();
  }

  public List<Recipe> searchRecipesByName(String name) {
    return recipeRepository.searchRecipesByName(name);
  }

  public List<Recipe> searchRecipesByCategory(IngredientCategory category) {
    return recipeRepository.list("category", category);
  }

  public List<RecipePerIngredient> searchRecipesByIngredient(@Nonnull String ingredientName) {
    Ingredient persistentIngredient = ingredientRepository.findByName(ingredientName);
    List<Recipe> recipes = recipeRepository.list("lower(element(ingredients).ingredient.name) = ?1", ingredientName.toLowerCase());
    List<RecipePerIngredient> recipesPerIngredient = new ArrayList<>();
    for (Recipe recipe : recipes) {
      recipesPerIngredient.add(new RecipePerIngredient(recipe, persistentIngredient));
    }
    return recipesPerIngredient;
  }
}
