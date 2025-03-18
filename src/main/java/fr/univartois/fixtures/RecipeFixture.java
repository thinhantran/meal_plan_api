package fr.univartois.fixtures;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import fr.univartois.model.Ingredient;
import fr.univartois.model.IngredientRecipeQuantity;
import fr.univartois.model.Recipe;
import fr.univartois.repository.IngredientRepository;
import fr.univartois.repository.RecipeRepository;
import fr.univartois.themealdb.api.TheMealDBApi;
import fr.univartois.themealdb.dto.CategoriesDTO.CategoryDTO;
import fr.univartois.themealdb.dto.RecipesDTO;
import fr.univartois.themealdb.dto.RecipesDTO.RecipeDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class RecipeFixture extends Fixture {

  public static final char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();

  TheMealDBApi theMealDBApi;

  IngredientRepository ingredientRepository;

  RecipeRepository recipeRepository;

  public RecipeFixture(@RestClient TheMealDBApi theMealDBApi, IngredientRepository ingredientRepository, RecipeRepository recipeRepository) {
    this.theMealDBApi = theMealDBApi;
    this.ingredientRepository = ingredientRepository;
    this.recipeRepository = recipeRepository;
  }

  @Override
  public void generateRealData() {

  }

  @Override
  public void generateSingleFakeData() {

  }

  @Transactional
  @Override
  public void generateFakeData() {
    List<RecipeDTO> recipes = new ArrayList<>();
    for (Character c : alphabet) {
      List<RecipeDTO> recipeDTOs = theMealDBApi.getRecipesByFirstLetter(c.toString()).getMeals();
      if (recipeDTOs != null && !recipeDTOs.isEmpty()) {
        recipes.addAll(recipeDTOs);
      }
    }
    for (RecipeDTO recipe : recipes) {
      Recipe newRecipe = new Recipe();
      newRecipe.setRecipeId(recipe.getIdMeal());
      newRecipe.setName(recipe.getStrMeal());
      newRecipe.setThumbnailUrl(recipe.getStrMealThumb());
      newRecipe.setCategory(recipe.getStrCategory());
      List<IngredientRecipeQuantity> ingredientRecipeQuantities = new ArrayList<>();
      for (Map.Entry<String, String> entry : recipe.getIngredientAndMeasures().entrySet()) {
        IngredientRecipeQuantity ingredientRecipeQuantity = new IngredientRecipeQuantity();
        Ingredient ingredient = ingredientRepository.findByName(entry.getKey());
        ingredientRecipeQuantity.setIngredient(ingredient);
        ingredientRecipeQuantity.setQuantity(entry.getValue());
        ingredientRecipeQuantity.setRecipe(newRecipe);
        ingredientRecipeQuantity.setIngredient(ingredient);
        ingredientRecipeQuantities.add(ingredientRecipeQuantity);
      }
      newRecipe.setIngredients(ingredientRecipeQuantities);
      recipeRepository.persist(newRecipe);
    }
  }
}
