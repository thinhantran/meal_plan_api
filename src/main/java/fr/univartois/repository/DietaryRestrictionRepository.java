package fr.univartois.repository;

import fr.univartois.model.DietaryRestriction;
import fr.univartois.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class DietaryRestrictionRepository implements PanacheRepository<DietaryRestriction> {

    public List<DietaryRestriction> getByUser(User user) {
        return find("user.userId", user.getUserId()).list();
    }

}
