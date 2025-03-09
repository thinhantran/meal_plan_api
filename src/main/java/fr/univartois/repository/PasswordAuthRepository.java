package fr.univartois.repository;

import fr.univartois.model.PasswordAuth;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PasswordAuthRepository implements PanacheRepository<PasswordAuth> {
}
