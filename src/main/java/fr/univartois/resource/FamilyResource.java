package fr.univartois.resource;

import java.util.List;

import fr.univartois.model.Family;
import fr.univartois.model.FamilyInvitation;
import fr.univartois.model.MemberRole;
import fr.univartois.model.User;
import fr.univartois.service.FamilyService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/families/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FamilyResource {

  @Inject
  FamilyService familyService;

  @Path("/{familyId}")
  @GET
  public Family getFamilyDetail(@PathParam("familyId") int familyId) {
    return familyService.get(familyId);
  }

  @Path("/{familyId}/members")
  @GET
  public List<User> getUsers(@PathParam("familyId") long familyId) {
    return familyService.findMembers(familyId);
  }

  @Path("/{familyId}/invitations")
  @POST
  public Response createInvitation(@PathParam("familyId") long familyId, User user) {
    return familyService.createInvitation(familyId, user);
  }

  @Path("/{familyId}/invitations")
  @GET
  public List<User> getInvitations(@PathParam("familyId") long familyId) {
    return familyService.getInvitations(familyId);
  }

  @Path("/{familyId}/{role}")
  @POST
  public Response joinFamily(@PathParam("familyId") int familyId,
                         @PathParam("role") MemberRole.Role role,  User user) {
    return familyService.joinFamily(familyId, role, user);
  }

  @Path("/{familyId}/members/{userId}/roles")
  @GET
  public MemberRole.Role getRole(@PathParam("familyId") int familyId, @PathParam("userId") long userId) {
    return familyService.getMemberRole(familyId, userId);
  }

  @Path("/")
  @POST
  public Response createFamilyForUser(User user) {
    return familyService.createFamily(user);
  }
}
