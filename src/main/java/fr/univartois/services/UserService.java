package fr.univartois.services;

import java.util.List;

import fr.univartois.model.DietaryRestriction;
import fr.univartois.model.Family;
import fr.univartois.model.MemberRole;
import fr.univartois.model.User;
import fr.univartois.repository.DietaryRestrictionRepository;
import fr.univartois.repository.FamilyRepository;
import fr.univartois.repository.MemberRoleRepository;
import fr.univartois.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class UserService {

  UserRepository userRepository;

  MemberRoleRepository memberRoleRepository;

  DietaryRestrictionRepository dietaryRestrictionRepository;

  FamilyRepository familyRepository;

  public UserService(UserRepository userRepository, MemberRoleRepository memberRoleRepository,
      DietaryRestrictionRepository dietaryRestrictionRepository, FamilyRepository familyRepository) {
    this.userRepository = userRepository;
    this.memberRoleRepository = memberRoleRepository;
    this.dietaryRestrictionRepository = dietaryRestrictionRepository;
    this.familyRepository = familyRepository;
  }

  public Family getFamily(User user) {
    return memberRoleRepository.getFamily(user);
  }

  public User findByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  public Response leaveFamily(User user) {
    if (user.getMemberRole() == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    Family family = user.getMemberRole().getFamily();
    memberRoleRepository.delete(user.getMemberRole());
    family.getMemberRoles().remove(user.getMemberRole());
    user.setMemberRole(null);
    List<User> adminsLeft = family.getMemberRoles().stream()
        .filter(mr -> MemberRole.Role.ADMIN == mr.getCategory())
        .map(MemberRole::getUser).toList();
    List<User> managersLeft = family.getMemberRoles().stream()
        .filter(mr -> MemberRole.Role.MANAGER == mr.getCategory())
        .map(MemberRole::getUser).toList();
    List<User> proposersLeft = family.getMemberRoles().stream()
        .filter(mr -> MemberRole.Role.PROPOSER == mr.getCategory())
        .map(MemberRole::getUser).toList();
    List<User> usersLeft = family.getMemberRoles().stream()
        .filter(mr -> MemberRole.Role.MEMBER == mr.getCategory())
        .map(MemberRole::getUser).toList();
    if (adminsLeft.isEmpty() && managersLeft.isEmpty() && proposersLeft.isEmpty() && usersLeft.isEmpty()) {
      familyRepository.delete(family);
    } else if (adminsLeft.isEmpty() && managersLeft.isEmpty() && !proposersLeft.isEmpty()) {
      proposersLeft.getFirst().getMemberRole().setCategory(MemberRole.Role.MANAGER);
    } else if (adminsLeft.isEmpty() && managersLeft.isEmpty()) {
      usersLeft.getFirst().getMemberRole().setCategory(MemberRole.Role.MANAGER);
    }
    return Response.status(Response.Status.NO_CONTENT).build();
  }

  public List<DietaryRestriction> getDietaryRestrictions(User user) {
    return dietaryRestrictionRepository.getByUser(user);
  }

  public List<DietaryRestriction> addDietaryRestriction(User user, List<String> terms) {
    for (String term : terms) {
      List<DietaryRestriction> existingRestrictions = dietaryRestrictionRepository.getByUser(user)
          .stream().filter(r -> term.equalsIgnoreCase(r.getRestrictionName())).toList();
      if (!existingRestrictions.isEmpty()) {
        user.getDietaryRestrictions().remove(existingRestrictions.getFirst());
      } else {
        DietaryRestriction dietaryRestriction = new DietaryRestriction();
        dietaryRestriction.setUser(user);
        dietaryRestriction.setRestrictionName(term);
        user.addRestriction(dietaryRestriction);
        dietaryRestrictionRepository.persist(dietaryRestriction);
      }
    }
    return getDietaryRestrictions(user);
  }

}
