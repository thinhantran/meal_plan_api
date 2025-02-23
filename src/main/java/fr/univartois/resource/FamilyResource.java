package fr.univartois.resource;

import java.util.List;

import fr.univartois.model.Family;
import fr.univartois.model.FamilyInvitation;
import fr.univartois.model.MemberRole;
import fr.univartois.model.User;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/families/")
public class FamilyResource {

  @Path("/{familyId}")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Family getFamilyDetail(@PathParam("familyId") int familyId) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Path("/{familyId}/members")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<User> getUsers(@PathParam("familyId") int familyId) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Path("/{familyId}")
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public FamilyInvitation createInvitation(@PathParam("familyId") int familyId) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Path("/{familyId}/{username}/{code}")
  public void joinFamily(@PathParam("familyId") int familyId, @PathParam("username") String username,
      @PathParam("code") String code) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Path("/{familyId}/members/{username}/roles")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public MemberRole.Role getRole(@PathParam("familyId") int familyId, @PathParam("username") String username) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Path("/{username}")
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public Response createFamilyForUser(@PathParam("username") String username) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
