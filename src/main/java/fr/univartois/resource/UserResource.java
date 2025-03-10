package fr.univartois.resource;

import java.util.List;

import fr.univartois.model.DietaryRestriction;
import fr.univartois.model.Family;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/users/{username}")
public class UserResource {

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
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Path("/families/{familyId}")
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  public Family reassignUserToFamily(@PathParam("username") String username, @PathParam("familyId") String familyId) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
