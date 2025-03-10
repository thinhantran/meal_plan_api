package fr.univartois.repository;

import fr.univartois.model.TokenAuth;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TokenAuthRepository implements PanacheRepository<TokenAuth> {
}
