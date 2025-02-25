package fr.univartois.service;

import fr.univartois.model.Family;
import fr.univartois.model.FamilyInvitation;
import fr.univartois.model.MemberRole;
import fr.univartois.model.User;
import fr.univartois.repository.FamilyInvitationRepository;
import fr.univartois.repository.FamilyRepository;
import fr.univartois.repository.MemberRoleRepository;
import fr.univartois.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import java.util.List;

@ApplicationScoped
public class FamilyService {

    @Inject
    private FamilyRepository familyRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private FamilyInvitationRepository familyInvitationRepository;

    @Inject
    private MemberRoleRepository memberRoleRepository;

    public Response createFamily(User user) {
        Family family = new Family();
        MemberRole memberRole = new MemberRole();
        memberRole.setUser(user);
        memberRole.setCategory(MemberRole.Role.MANAGER);
        family.addMember(memberRole);
        familyRepository.persist(family);
        return Response.status(Response.Status.CREATED).entity(family).build();
    }

    public List<User> findMembers(long id) {
        return userRepository.findAllByFamily(id);
    }

    public Family get(long id) {
        return familyRepository.findById(id);
    }

    public Response createInvitation(long id, User user) {
        FamilyInvitation invitation = new FamilyInvitation();
        invitation.setUser(user);
        invitation.setFamily(familyRepository.findById(id));
        familyInvitationRepository.persist(invitation);
        return Response.status(Response.Status.CREATED).entity(invitation).build();
    }

    public List<User> getInvitations(long id) {
        return familyInvitationRepository.findAllByFamily(id);
    }

    public MemberRole.Role getMemberRole(long familyId, long userId) {
        return memberRoleRepository.getRole(familyId, userId);
    }

}
