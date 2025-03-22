package fr.univartois.resource;

import java.util.List;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeIn;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;

import fr.univartois.model.DietaryRestriction;
import fr.univartois.model.Family;
import fr.univartois.model.User;
import fr.univartois.services.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users")
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
public class UserResource {

  private static final String NOT_SUPPORTED_YET = "Not supported yet.";

  UserService userService;

  JsonWebToken jwt;

  public UserResource(UserService userService, JsonWebToken jwt) {
    this.userService = userService;
    this.jwt = jwt;
  }

  @Path("/restrictions")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<DietaryRestriction> getRestrictions() {
    User user = userService.findByUsername(jwt.getSubject());
    if(user == null) {
      throw new NotFoundException("User not found");
    }
    return userService.getDietaryRestrictions(user);
  }

  @Path("/restrictions")
  @PUT
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Transactional
  public List<DietaryRestriction> putRestrictions(@QueryParam("terms") List<String> terms) {
    User user = userService.findByUsername(jwt.getSubject());
    if(user == null) {
      throw new NotFoundException("User not found");
    }
    return userService.addDietaryRestriction(user, terms);
  }

  @Path("/families/")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Family getUserFamily() {
    User user = userService.findByUsername(jwt.getSubject());
    if(user == null) {
      throw new NotFoundException("User not found");
    }
    return userService.getFamily(user);
  }

  @Path("/families/{familyId}")
  @DELETE
  @Transactional
  public Response leaveFamily(@PathParam("familyId") long familyId) {
    User user = userService.findByUsername(jwt.getSubject());
    if(user == null) {
      throw new NotFoundException("User not found");
    }
    return userService.leaveFamily(familyId, user.getUserId());
  }

  @Path("/families/{familyId}")
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public Family assignUserToFamily(@PathParam("familyId") String familyId) {
    throw new UnsupportedOperationException(NOT_SUPPORTED_YET);
  }

  @Path("/families/{familyId}")
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  public Family reassignUserToFamily(@PathParam("familyId") String familyId) {
    throw new UnsupportedOperationException(NOT_SUPPORTED_YET);
  }
}
