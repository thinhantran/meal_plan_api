package fr.univartois.repository;

import java.util.List;

import fr.univartois.model.Family;
import fr.univartois.model.IngredientFridgeQuantity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class IngredientFridgeQuantityRepository implements PanacheRepository<IngredientFridgeQuantity> {

    public List<IngredientFridgeQuantity> findByFamily(Family family) {
        return find("fridge.family", Sort.descending("quantity").and("ingredient.name", Sort.Direction.Ascending), family).list();
    }
}
