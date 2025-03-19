package fr.univartois.service;

import fr.univartois.model.Family;
import fr.univartois.model.MemberRole;
import fr.univartois.model.User;
import fr.univartois.repository.MemberRoleRepository;
import fr.univartois.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

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

    public Response leaveFamily(long familyId, long userId) {
        MemberRole memberRole = memberRoleRepository.findByUserAndFamily(familyId, userId);
        if(memberRole == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        memberRoleRepository.delete(memberRole);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

}
