package fr.univartois.exception;

import fr.univartois.dtos.Message;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public class GlobalExceptionHandler implements ExceptionMapper<IllegalArgumentException> {

    @Override
    public Response toResponse(IllegalArgumentException exception) {
        return Response.status(Response.Status.EXPECTATION_FAILED)
                .entity(new Message(exception.getMessage()))
                .build();
    }
}
