package fr.univartois.repository;

import fr.univartois.model.Family;
import fr.univartois.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FamilyRepository implements PanacheRepository<Family> {

    public Family findFamilyByCode(String familyCode) {
        return find("code", familyCode).firstResult();
    }

    public Family findByUser(User user) {
        return find("from Family f join f.memberRoles member where member.user = ?1", user).firstResult();
    }
}
