package fr.univartois.services;

import java.util.List;

import org.eclipse.microprofile.jwt.JsonWebToken;

import fr.univartois.dtos.Message;
import fr.univartois.model.Family;
import fr.univartois.model.MemberRole;
import fr.univartois.model.User;
import fr.univartois.repository.FamilyRepository;
import fr.univartois.repository.MemberRoleRepository;
import fr.univartois.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class FamilyService {

    FamilyRepository familyRepository;

    MemberRoleRepository memberRoleRepository;

    UserRepository userRepository;

    FridgeService fridgeService;

    public FamilyService(FamilyRepository familyRepository, MemberRoleRepository memberRoleRepository, UserRepository userRepository, FridgeService fridgeService) {
        this.familyRepository = familyRepository;
        this.memberRoleRepository = memberRoleRepository;
        this.userRepository = userRepository;
        this.fridgeService = fridgeService;
    }

    public Response createFamily(JsonWebToken jwt, String name) {
        User user = userRepository.findByUsername(jwt.getSubject());
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("You're not an user").build();
        }
        if (user.getMemberRole() != null) {
            return Response.status(Response.Status.CONFLICT).entity(new Message("You're already part of a family")).build();
        }
        Family family = new Family();
        MemberRole memberRole = new MemberRole();
        memberRole.setUser(user);
        memberRole.setFamily(family);
        memberRole.setCategory(MemberRole.Role.MANAGER);
        family.addMember(memberRole);
        family.setName(name);
        memberRoleRepository.persist(memberRole);
        familyRepository.persist(family);
        user.setMemberRole(memberRole);
        fridgeService.createFridge(jwt);
        return Response.status(Response.Status.CREATED).entity(family).build();
    }

    public List<User> findMembers(long id) {
        return memberRoleRepository.findAllByFamily(id);
    }

    public Response get(JsonWebToken jsonWebToken) {
        User user = userRepository.findByUsername(jsonWebToken.getSubject());
        if(user == null || user.getMemberRole() == null || user.getMemberRole().getFamily() == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        return Response.status(Response.Status.OK).entity(user.getMemberRole().getFamily()).build();
    }

    public Response joinFamily(String code, User user) {
        Family family = familyRepository.findFamilyByCode(code);
        if(memberRoleRepository.findByUserAndFamily(family.getId(), user.getUserId()) != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("L'utilisateur fait déjà parti du groupe").build();
        }
        MemberRole memberRole = new MemberRole(null, user, family, MemberRole.Role.MEMBER);
        memberRoleRepository.persist(memberRole);
        return Response.status(Response.Status.OK).build();
    }

    public MemberRole.Role getMemberRole(long familyId, long userId) {
        return memberRoleRepository.getRole(familyId, userId);
    }

}
