package au.com.rest.test.services;

import au.com.rest.test.services.security.TokenHandling;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.io.IOException;


@Provider
public class BasicFilter implements ContainerRequestFilter, ContainerResponseFilter {


    private final String HEALTH_CHECK = "/health/check";
    private final String LOGIN = "/login/login";
    private final String CREATE_USER = "/login/create";
    private final String RESET_PASSWORD = "/login/resetPassword";
    private final String TOKEN = "token";

    /**
     * After a request this is the first method the will be called, before than any service
     * <p>
     * The best place to check tokens, cancel request, check URLs and header
     * This idea is pretty much a in-house OAuth, where it can be customized with higher or lesser security.
     * The example bellow isn't even considered as security, it's just a very basic example of how it would work
     *
     * @param containerRequestContext
     * @throws IOException
     */
    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        final UriInfo info = containerRequestContext.getUriInfo();
        final String urlPath = info.getPath();
        containerRequestContext.getHeaders().add("Request", "Adding a value in the request");
        containerRequestContext.getHeaders().add("URL called", urlPath);
        containerRequestContext.getHeaders().add("By", "Klaus Villaca");
        final String token = containerRequestContext.getHeaders().containsKey(TOKEN)? containerRequestContext.getHeaders().getFirst(TOKEN): null;

        // If the request is not health check or login then it's necessary to check for the token
        boolean isValid = true;
        if (!urlPath.endsWith(HEALTH_CHECK) &&
                !urlPath.endsWith(LOGIN) &&
                !urlPath.endsWith(CREATE_USER) &&
                !urlPath.contains(RESET_PASSWORD)) {
            if (token != null) {
                final TokenHandling tokenHandling = new TokenHandling();
                isValid = tokenHandling.checkToken(token);
                if (!isValid) {
                    final Response response = Response.status(Response.Status.GATEWAY_TIMEOUT).entity("Access expired").build();
                    containerRequestContext.abortWith(response);
                }
            } else {
                final Response response = Response.status(Response.Status.FORBIDDEN).entity("Forbidden").build();
                containerRequestContext.abortWith(response);
            }
        }
    }


    /**
     * Response after the service has run, perfect place to add the just created token
     *
     * @param containerRequestContext
     * @param containerResponseContext
     * @throws IOException
     */
    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) throws IOException {
        final TokenHandling tokenHandling = new TokenHandling();
        final MultivaluedMap<String, Object> responseHeaderAsMap = containerResponseContext.getHeaders();
        final int statusResponse = containerResponseContext.getStatus();
        if (isValidStatus(statusResponse)) {
            responseHeaderAsMap.add(TOKEN, tokenHandling.newToken());
        }
    }


    private boolean isValidStatus(final int statusCode) {
        boolean isValid = true;
        if (statusCode != 200 && statusCode != 201) {
            isValid = false;
        }
        return isValid;
    }
}
