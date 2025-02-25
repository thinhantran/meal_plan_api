package fr.univartois.repository;

import fr.univartois.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    public List<User> findAllByFamily(long id) {
        return list("user.memberRole.family.id", id);
    }

}
