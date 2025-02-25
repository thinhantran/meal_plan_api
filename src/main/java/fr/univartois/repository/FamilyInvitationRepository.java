package fr.univartois.repository;

import fr.univartois.model.FamilyInvitation;
import fr.univartois.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.SessionScoped;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class FamilyInvitationRepository implements PanacheRepository<FamilyInvitation> {

    public List<User> findAllByFamily(long id) {
        return findAll().stream().map(FamilyInvitation::getUser).collect(Collectors.toList());
    }

}
