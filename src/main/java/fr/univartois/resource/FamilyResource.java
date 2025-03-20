package fr.univartois.resource;

import java.util.List;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeIn;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;

import fr.univartois.model.Family;
import fr.univartois.model.MemberRole;
import fr.univartois.model.User;
import fr.univartois.repository.MemberRoleRepository;
import fr.univartois.repository.UserRepository;
import fr.univartois.services.FamilyService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/families/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@SecuritySchemes(value = {
        @SecurityScheme(
                bearerFormat = "JWT",
                scheme = "bearer",
                securitySchemeName = "AccessBearerAuthentication",
                apiKeyName = "Authorization",
                type = SecuritySchemeType.HTTP,
                description = "Uses the access token provided at authentication (Header \"Authentification\", Value \"Bearer xxx\")",
                in = SecuritySchemeIn.HEADER
        )
})
@RolesAllowed("access")
@SecurityRequirement(name = "AccessBearerAuthentication")
public class FamilyResource {

  @Inject
  FamilyService familyService;

  @Inject
  UserRepository userRepository;

  @Inject
  JsonWebToken jwt;

  @Inject
  MemberRoleRepository memberRoleRepository;

  @Path("/{familyId}")
  @GET
  public Family getFamilyDetail(@PathParam("familyId") int familyId) {
    User user = userRepository.findByUsername(jwt.getSubject());
    if(memberRoleRepository.findByUserAndFamily(familyId, user.getUserId()) == null) {
      throw new ForbiddenException();
    }
    return familyService.get(familyId);
  }

  @Path("/{familyId}/members")
  @GET
  public List<User> getUsers(@PathParam("familyId") long familyId) {
    User user = userRepository.findByUsername(jwt.getSubject());
    if(memberRoleRepository.findByUserAndFamily(familyId, user.getUserId()) == null) {
      throw new ForbiddenException();
    }
    return familyService.findMembers(familyId);
  }

//  @Path("/{familyId}/invitations")
//  @Transactional
//  @POST
//  public Response createInvitation(@PathParam("familyId") long familyId, User user) {
//    return familyService.createInvitation(familyId, user);
//  }
//
//  @Path("/{familyId}/invitations")
//  @GET
//  public List<User> getInvitations(@PathParam("familyId") long familyId) {
//    return familyService.getInvitations(familyId);
//  }

  @Path("/{familyCode}")
  @Transactional
  @POST
  public Response joinFamily(@PathParam("familyCode") String familyCode) {
    User user = userRepository.findByUsername(jwt.getSubject());
    return familyService.joinFamily(familyCode, user);
  }

  @Path("/{familyId}/members/{userId}/roles")
  @GET
  public MemberRole.Role getRole(@PathParam("familyId") int familyId, @PathParam("userId") long userId) {
    User user = userRepository.findByUsername(jwt.getSubject());
    if(memberRoleRepository.findByUserAndFamily(familyId, user.getUserId()) == null) {
      throw new ForbiddenException();
    }
    return familyService.getMemberRole(familyId, userId);
  }

  @Path("/")
  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Transactional
  public Response createFamilyForUser(@FormParam("name") String name) {
    User user = userRepository.findByUsername(jwt.getSubject());
    return familyService.createFamily(user, name);
  }
}
