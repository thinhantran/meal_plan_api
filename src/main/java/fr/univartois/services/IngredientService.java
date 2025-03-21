package fr.univartois.services;

import java.util.List;

import fr.univartois.model.Ingredient;
import fr.univartois.repository.IngredientRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class IngredientService {

  @Inject
  IngredientRepository ingredientRepository;

  public List<Ingredient> getAllIngredients() {
    return ingredientRepository.findAll(Sort.by("name")).list();
  }
}
