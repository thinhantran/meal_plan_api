package fr.univartois.resource;

import java.util.List;

import fr.univartois.model.DietaryRestriction;
import fr.univartois.model.Family;
import fr.univartois.model.User;
import fr.univartois.service.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/users/{username}")
public class UserResource {

  @Inject
  UserService userService;

  @Path("/restrictions")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<DietaryRestriction> getRestrictions(@PathParam("username") String username) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Path("/restrictions")
  @PUT
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public List<DietaryRestriction> putRestrictions(@PathParam("username") String username, @QueryParam("terms") List<String> terms) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Path("/families/")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Family getUserFamily(@PathParam("username") String username) {
    User user = userService.findByUsername(username);
    if(user == null) {
      throw new NotFoundException("User not found");
    }
    return userService.getFamily(user);
  }

  @Path("/families/{familyId}")
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public Family assignUserToFamily(@PathParam("username") String username, @PathParam("familyId") String familyId) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Path("/families/{familyId}")
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  public Family reassignUserToFamily(@PathParam("username") String username, @PathParam("familyId") String familyId) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
