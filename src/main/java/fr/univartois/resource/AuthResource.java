package fr.univartois.resource;

import fr.univartois.model.TokenAuth;
import fr.univartois.model.User;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/auth")
public class AuthResource {

  @POST
  @Path("/users")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public User signup(User user) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @POST
  @Path("/login/user")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public TokenAuth login(User user) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @POST
  @Path("/login/token")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public void login(TokenAuth token) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @POST
  @Path("/logout")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public void logout(TokenAuth token) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
