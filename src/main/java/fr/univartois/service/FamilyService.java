package fr.univartois.service;

import java.util.List;

import fr.univartois.model.Family;
import fr.univartois.model.MemberRole;
import fr.univartois.model.User;
import fr.univartois.repository.FamilyRepository;
import fr.univartois.repository.MemberRoleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class FamilyService {

    @Inject
    FamilyRepository familyRepository;

//    @Inject
//    FamilyInvitationRepository familyInvitationRepository;

    @Inject
    MemberRoleRepository memberRoleRepository;

    public Response createFamily(User user, String name) {
        Family family = new Family();
        MemberRole memberRole = new MemberRole();
        memberRole.setUser(user);
        memberRole.setFamily(family);
        memberRole.setCategory(MemberRole.Role.MANAGER);
        family.addMember(memberRole);
        family.setName(name);
        memberRoleRepository.persist(memberRole);
        familyRepository.persist(family);
        return Response.status(Response.Status.CREATED).entity(family).build();
    }

    public List<User> findMembers(long id) {
        return memberRoleRepository.findAllByFamily(id);
    }

    public Family get(long id) {
        return familyRepository.findById(id);
    }

//    public Response createInvitation(long id, User user) {
//        FamilyInvitation invitation = new FamilyInvitation();
//        invitation.setUser(user);
//        invitation.setFamily(familyRepository.findById(id));
//        familyInvitationRepository.persist(invitation);
//        return Response.status(Response.Status.CREATED).entity(invitation).build();
//    }
//
//    public List<User> getInvitations(long id) {
//        return familyInvitationRepository.findAllByFamily(id);
//    }

    public Response joinFamily(String code, User user) {
        Family family = familyRepository.findFamilyByCode(code);
        if(memberRoleRepository.findByUserAndFamily(family.getId(), user.getUserId()) != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("L'utilisateur fait déjà parti du groupe").build();
        }
//        FamilyInvitation invitation = familyInvitationRepository.findInvitation(user.getUserId(), family.getId());
//        if(invitation != null) {
//            familyInvitationRepository.delete(invitation);
//        }
        MemberRole memberRole = new MemberRole(null, user, family, MemberRole.Role.MEMBER);
        memberRoleRepository.persist(memberRole);
        return Response.status(Response.Status.OK).build();
    }

    public MemberRole.Role getMemberRole(long familyId, long userId) {
        return memberRoleRepository.getRole(familyId, userId);
    }

}
