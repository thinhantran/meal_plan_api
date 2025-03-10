package fr.univartois.repository;

import fr.univartois.model.MemberRole;
import fr.univartois.model.User;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class MemberRoleRepository implements PanacheRepository<MemberRole> {

    public List<User> findAllByFamily(long id) {
        return find("family.id", id).stream().map(MemberRole::getUser).collect(Collectors.toList());
    }

    public MemberRole.Role getRole(long familyId, long userId) {
        PanacheQuery<MemberRole> query = find("user.userId = ?1 and family.id = ?2", userId, familyId);
        if(query.count() == 0) {
            throw new BadRequestException();
        }
        return query.singleResult().getCategory();
    }

}
