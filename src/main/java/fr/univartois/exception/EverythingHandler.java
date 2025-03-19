package fr.univartois.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class EverythingHandler implements ExceptionMapper<Exception> {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public Response toResponse(Exception e) {
        System.err.println(e.getMessage());
        e.printStackTrace();
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
    }
}
