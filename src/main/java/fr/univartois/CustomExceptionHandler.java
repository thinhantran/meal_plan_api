package fr.univartois;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

public class CustomExceptionHandler implements ExceptionMapper<UnsupportedOperationException> {

  @Override
  public Response toResponse(UnsupportedOperationException e) {
    return Response.status(Response.Status.NOT_IMPLEMENTED).entity(e.getMessage()).build();
  }
}
