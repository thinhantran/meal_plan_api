package fr.univartois.fixtures;

import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.univartois.model.Ingredient;
import fr.univartois.repository.IngredientRepository;
import fr.univartois.themealdb.api.TheMealDBApi;
import fr.univartois.themealdb.dto.IngredientsDTO.IngredientDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class IngredientFixture extends Fixture {

  Logger logger = LoggerFactory.getLogger(IngredientFixture.class);

  TheMealDBApi theMealDBApi;

  IngredientRepository ingredientRepository;

  public IngredientFixture(@RestClient TheMealDBApi theMealDBApi, IngredientRepository ingredientRepository) {
    this.theMealDBApi = theMealDBApi;
    this.ingredientRepository = ingredientRepository;
  }

  @Override
  public void generateRealData() {

  }

  @Override
  @Transactional
  public void generateFakeData() {
    List<IngredientDTO> list = theMealDBApi.getAllIngredients("list").getMeals();
    for (IngredientDTO dto : list) {
      Ingredient ingredient = new Ingredient();
      ingredient.setIngredientId(dto.getIdIngredient());
      ingredient.setName(dto.getStrIngredient());
      ingredientRepository.persist(ingredient);
    }
  }

  @Override
  public void generateSingleFakeData() {

  }
}
