package fr.univartois.resource;

import fr.univartois.model.Token;
import fr.univartois.model.User;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestResponse;

@Path("/auth")
public class AuthResource {

    @POST
    @Path("/users")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse<User> signup(User user) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @POST
    @Path("/login/user")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse<Token> login(User user) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @POST
    @Path("/login/token")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Token login(Token token) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @POST
    @Path("/logout")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void logout(Token token) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
