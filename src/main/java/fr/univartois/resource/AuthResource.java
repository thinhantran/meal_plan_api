package fr.univartois.resource;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeIn;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;

import fr.univartois.dtos.CustomJwtAccess;
import fr.univartois.dtos.CustomJwtPair;
import fr.univartois.dtos.Message;
import fr.univartois.model.PasswordAuth;
import fr.univartois.model.User;
import fr.univartois.services.AuthService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@SecuritySchemes(value = {
    @SecurityScheme(
        bearerFormat = "JWT",
        scheme = "bearer",
        securitySchemeName = "RefreshBearerAuthentication",
        apiKeyName = "Authroization",
        type = SecuritySchemeType.HTTP,
        description = "Uses the refresh token provided at authentication (Header \"Authentification\", Value \"Bearer xxx\")",
        in = SecuritySchemeIn.HEADER
    ),
    @SecurityScheme(
        bearerFormat = "JWT",
        scheme = "bearer",
        securitySchemeName = "AccessBearerAuthentication",
        apiKeyName = "Authroization",
        type = SecuritySchemeType.HTTP,
        description = "Uses the access token provided at authentication (Header \"Authentification\", Value \"Bearer xxx\")",
        in = SecuritySchemeIn.HEADER
    )
})
public class AuthResource {

  AuthService authService;

  JsonWebToken jwt;

  public AuthResource(JsonWebToken jwt, AuthService authService) {
    this.jwt = jwt;
    this.authService = authService;
  }

  @GET
  @Path("/users")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("access")
  @SecurityRequirement(name = "AccessBearerAuthentication")
  public Response getUser() {
    User user = authService.hasAssociatedUser(jwt);
    if (user == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    return Response.status(Response.Status.OK).build();
  }

  @POST
  @Path("/users")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response signup(@FormParam("username") String username, @FormParam("password") String password) {
    if (authService.findUser(username) != null) {
      return Response.status(Response.Status.CONFLICT).build();
    }
    User user = authService.createUserWithPassword(username, password);
    CustomJwtPair customJwtPair = authService.getAccessAndRefreshToken(user);

    return Response.status(Response.Status.CREATED).entity(customJwtPair).build();
  }

  @PUT
  @Path("/users")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("access")
  @SecurityRequirement(name = "AccessBearerAuthentication")
  public Response changePassword(@FormParam("oldPassword") String oldPassword, @FormParam("newPassword") String newPassword) {
    PasswordAuth targetedUser = authService.findPasswordAuth(jwt.getSubject());
    if (targetedUser == null) {
      return Response.status(Response.Status.PRECONDITION_FAILED).build();
    }
    if (!authService.comparePassword(targetedUser, oldPassword)) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    authService.updateUserWithNewPassword(targetedUser, newPassword);
    return Response.status(Response.Status.NO_CONTENT).build();
  }

  @POST
  @Path("/login/user")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  public Response login(@FormParam("username") String username, @FormParam("password") String password) {
    PasswordAuth auth = authService.findPasswordAuth(username);
    if (auth == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    boolean samePassword = authService.comparePassword(auth, password);
    if (!samePassword) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    CustomJwtPair customJwt = authService.getAccessAndRefreshToken(auth.getUser());
    return Response.status(Response.Status.OK).entity(customJwt).build();
  }

  @RolesAllowed("refresh")
  @POST
  @Path("/login/token")
  @Produces(MediaType.APPLICATION_JSON)
  @SecurityRequirement(name = "RefreshBearerAuthentication")
  public Response loginWithToken() {
    User user = authService.hasAssociatedUser(jwt);
    if (user == null) {
      return Response.status(Response.Status.UNAUTHORIZED).entity(new Message("Invalid token")).build();
    }
    CustomJwtAccess customJwt = authService.getAccessToken(user);
    return Response.status(Response.Status.OK).entity(customJwt).build();
  }

  @RolesAllowed("refresh")
  @POST
  @Path("/logout")
  @Produces(MediaType.APPLICATION_JSON)
  @SecurityRequirement(name = "RefreshBearerAuthentication")
  public Response logout() {
    User user = authService.hasAssociatedUser(jwt);
    if (user != null) {
      authService.deleteRefreshToken(jwt);
      return Response.noContent().build();
    }
    return Response.status(Response.Status.UNAUTHORIZED).entity(new Message("Not connected")).build();
  }
}
