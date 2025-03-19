package fr.univartois.repository;

import fr.univartois.model.Family;
import fr.univartois.model.Fridge;
import fr.univartois.model.Utensil;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UtensilRepository implements PanacheRepository<Utensil> {

    public Utensil findByNameAndFamily(String name, Family family) {
        return find("name = ?1 and family = ?2", name, family).firstResult();
    }

    public Utensil findByNameAndFridge(String name, Fridge fridge) {
        return find("name = ?1 and fridge = ?2", name, fridge).firstResult();
    }
}
