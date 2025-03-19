package fr.univartois.repository;

import fr.univartois.model.Family;
import fr.univartois.model.IngredientFridgeQuantity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class IngredientFridgeQuantityRepository implements PanacheRepository<IngredientFridgeQuantity> {

    public List<IngredientFridgeQuantity> findByFamily(Family family) {
        return find("fridge.family", family).list();
    }
}
