package au.com.rest.test.services.helper;

import javax.ws.rs.core.Response;

public class ServicesHelper {

    public Response emptyPayload() {
        final String message = "Missing login details.";
        final Response responseToReturn = Response.status(Response.Status.BAD_REQUEST).entity(message).build();
        return responseToReturn;
    }

    public Response badLoginPayload() {
        final String message = "Please check your login or password, one or both may be wrong.";
        final Response responseToReturn = Response.status(Response.Status.BAD_REQUEST).entity(message).build();
        return responseToReturn;
    }

    public Response existingUserLoginPayload() {
        final String message = "This login already exist.";
        final Response responseToReturn = Response.status(Response.Status.CONFLICT).entity(message).build();
        return responseToReturn;
    }
}
