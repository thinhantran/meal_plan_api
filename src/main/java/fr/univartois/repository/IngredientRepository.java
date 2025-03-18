package fr.univartois.repository;

import fr.univartois.model.Ingredient;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class IngredientRepository implements PanacheRepository<Ingredient> {

  public Ingredient findByName(String name) {
    return find("lower(name)", name.toLowerCase()).firstResult();
  }
}
