package fr.univartois.repository;

import java.util.List;

import fr.univartois.model.Recipe;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RecipeRepository implements PanacheRepository<Recipe> {

  public List<Recipe> searchRecipesByName(String name) {
    return list("#Recipe.searchByName", name);
  }
}
