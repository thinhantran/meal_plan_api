package fr.univartois.repository;

import fr.univartois.model.Family;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FamilyRepository implements PanacheRepository<Family> {
}
