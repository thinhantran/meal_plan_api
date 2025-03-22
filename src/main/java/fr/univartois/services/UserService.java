package fr.univartois.services;

import java.util.List;

import fr.univartois.model.DietaryRestriction;
import fr.univartois.model.Family;
import fr.univartois.model.MemberRole;
import fr.univartois.model.User;
import fr.univartois.repository.DietaryRestrictionRepository;
import fr.univartois.repository.MemberRoleRepository;
import fr.univartois.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class UserService {

    UserRepository userRepository;

    MemberRoleRepository memberRoleRepository;

    @Inject
    DietaryRestrictionRepository dietaryRestrictionRepository;

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

    public Response leaveFamily(long familyId, long userId) {
        MemberRole memberRole = memberRoleRepository.findByUserAndFamily(familyId, userId);
        if(memberRole == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        memberRoleRepository.delete(memberRole);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    public List<DietaryRestriction> getDietaryRestrictions(User user) {
        return dietaryRestrictionRepository.getByUser(user);
    }

    public List<DietaryRestriction> addDietaryRestriction(User user, List<String> terms) {
        for(String term : terms) {
            DietaryRestriction dietaryRestriction = new DietaryRestriction();
            dietaryRestriction.setUser(user);
            dietaryRestriction.setRestrictionName(term);
            user.addRestriction(dietaryRestriction);
            dietaryRestrictionRepository.persist(dietaryRestriction);
        }
        return getDietaryRestrictions(user);
    }

}
