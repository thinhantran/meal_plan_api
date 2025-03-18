package fr.univartois.repository;

import fr.univartois.model.PlannedMeal;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MealRepository implements PanacheRepository<PlannedMeal> {
}
