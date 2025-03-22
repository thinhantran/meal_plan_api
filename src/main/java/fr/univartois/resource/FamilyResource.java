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

  FamilyService familyService;

  UserRepository userRepository;

  JsonWebToken jwt;

  MemberRoleRepository memberRoleRepository;

  public FamilyResource(FamilyService familyService, UserRepository userRepository, JsonWebToken jwt,
      MemberRoleRepository memberRoleRepository) {
    this.familyService = familyService;
    this.userRepository = userRepository;
    this.jwt = jwt;
    this.memberRoleRepository = memberRoleRepository;
  }

  @GET
  public Response getFamilyDetail() {
    return familyService.get(jwt);
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
    return familyService.createFamily(jwt, name);
  }
}
