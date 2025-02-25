package fr.univartois.repository;

import fr.univartois.model.MemberRole;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MemberRoleRepository implements PanacheRepository<MemberRole> {

    public MemberRole.Role getRole(long familyId, long userId) {
        return find("user.userId = ?1 and family.id = ?2", userId, familyId).singleResult().getCategory();
    }

}
