package fr.univartois.services;

import java.util.List;

import fr.univartois.dtos.RecipePerIngredient;
import fr.univartois.model.IngredientCategory;
import fr.univartois.model.Recipe;
import fr.univartois.repository.IngredientRecipeQuantityRepository;
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

  @Inject
  IngredientRecipeQuantityRepository ingredientRecipeQuantityRepository;

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

  public List<RecipePerIngredient> searchRecipesByIngredient(@Nonnull String ingredientName, int offset, int limit) {
    return ingredientRecipeQuantityRepository.find("SELECT irq.recipe, irq.ingredient FROM IngredientRecipeQuantity irq " +
            "WHERE irq.ingredient.name LIKE CONCAT('%',?1,'%')", Sort.ascending("irq.ingredient.name", "irq.recipe.name"), ingredientName)
        .page(offset, limit)
        .project(RecipePerIngredient.class)
        .list();
  }

  public Recipe getRecipeByName(String name) {
    return recipeRepository.find("lower(name)", name.toLowerCase()).firstResult();
  }

  public Recipe getRecipeById(Long id) {
    return recipeRepository.findById(id);
  }
}
