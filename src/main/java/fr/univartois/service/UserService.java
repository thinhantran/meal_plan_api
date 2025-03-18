package fr.univartois.service;

import fr.univartois.model.Family;
import fr.univartois.model.User;
import fr.univartois.repository.MemberRoleRepository;
import fr.univartois.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    @Inject
    MemberRoleRepository memberRoleRepository;

    public Family getFamily(User user) {
        return memberRoleRepository.getFamily(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

}
