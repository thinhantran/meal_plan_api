package fr.univartois.repository;

import fr.univartois.model.FamilyInvitation;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FamilyInvitationRepository implements PanacheRepository<FamilyInvitation> {

}
