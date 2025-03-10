package fr.univartois.repository;

import fr.univartois.model.Family;
import fr.univartois.model.FamilyInvitation;
import fr.univartois.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class FamilyInvitationRepository implements PanacheRepository<FamilyInvitation> {

    public List<User> findAllByFamily(long id) {
        return find("family.id", id).stream().map(FamilyInvitation::getUser).collect(Collectors.toList());
    }

    public FamilyInvitation findInvitation(long userId, long familyId) {
        return find("user.userId = ?1 and family.id = ?2", userId, familyId).singleResult();
    }

}
