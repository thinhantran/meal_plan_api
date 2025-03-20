package fr.univartois.repository;

import fr.univartois.model.SuggestedMeal;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SuggestedMealRepository implements PanacheRepository<SuggestedMeal> {

}
