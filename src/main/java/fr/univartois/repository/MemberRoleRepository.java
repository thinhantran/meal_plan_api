package fr.univartois.repository;

import fr.univartois.model.Family;
import fr.univartois.model.MemberRole;
import fr.univartois.model.User;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;

import java.util.List;

@ApplicationScoped
public class MemberRoleRepository implements PanacheRepository<MemberRole> {

    public List<User> findAllByFamily(long id) {
        return find("SELECT m.user FROM MemberRole m WHERE m.family.id = ?1", id).project(User.class).list();
    }

    public MemberRole.Role getRole(long familyId, long userId) {
        PanacheQuery<MemberRole> query = find("user.userId = ?1 and family.id = ?2", userId, familyId);
        if(query.count() == 0) {
            throw new BadRequestException();
        }
        return query.singleResult().getCategory();
    }

    public Family getFamily(User user) {
        return find("SELECT m.family FROM MemberRole m WHERE m.user.id = ?1", user.getUserId()).project(Family.class).singleResultOptional().orElse(null);
    }

}
