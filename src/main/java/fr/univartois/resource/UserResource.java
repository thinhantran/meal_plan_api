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
import fr.univartois.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

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

  @Inject
  UserService userService;

  @Inject
  JsonWebToken jwt;

  @Path("/restrictions")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<DietaryRestriction> getRestrictions() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Path("/restrictions")
  @PUT
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public List<DietaryRestriction> putRestrictions(@QueryParam("terms") List<String> terms) {
    throw new UnsupportedOperationException("Not supported yet.");
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
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public Family assignUserToFamily(@PathParam("familyId") String familyId) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Path("/families/{familyId}")
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  public Family reassignUserToFamily(@PathParam("familyId") String familyId) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
