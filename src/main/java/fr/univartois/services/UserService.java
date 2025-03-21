package fr.univartois.services;

import fr.univartois.model.Family;
import fr.univartois.model.User;
import fr.univartois.repository.MemberRoleRepository;
import fr.univartois.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserService {

    UserRepository userRepository;

    MemberRoleRepository memberRoleRepository;

    public UserService(UserRepository userRepository, MemberRoleRepository memberRoleRepository) {
        this.userRepository = userRepository;
        this.memberRoleRepository = memberRoleRepository;
    }

    public Family getFamily(User user) {
        return memberRoleRepository.getFamily(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

}
