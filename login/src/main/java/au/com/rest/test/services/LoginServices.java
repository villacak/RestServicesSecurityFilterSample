package au.com.rest.test.services;

import au.com.rest.test.business.ForgotPassword;
import au.com.rest.test.business.LoginCheck;
import au.com.rest.test.pojos.FailResponse;
import au.com.rest.test.pojos.UserLogin;
import au.com.rest.test.pojos.UserAccountDetails;
import au.com.rest.test.services.helper.ServicesHelper;
import au.com.rest.test.services.validate.ValidateRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/v1/login")
public class LoginServices {


    /**
     * This service will check if the user exist
     * At moment I didn;t have time to crete the filter.
     * Should do it this weekend..
     * Then it will return a token within the header as well
     * if success validated
     *
     * URL example: http://localhost:8080/resttest/v1/login/check
     *
     * raw payload example
     * {"login": "userLogin", "password": "myTestPassword"}
     *
     * @param userLogin
     * @return
     */
    @POST
    @Path("login")
    @Consumes("application/json")
    @Produces({"text/plain", "application/json"})
    public Response login(final UserLogin userLogin) {
        Response response = null;
        if (userLogin == null) {
            final ServicesHelper helper = new ServicesHelper();
            response = helper.emptyPayload();
        } else {
            try {
                final LoginCheck loginCheck = new LoginCheck();
                response = loginCheck.checkLogin(userLogin);
            } catch (Exception e) {
                // Change e.getMessage for some message with more meaning.
                response = Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
            }
        }
        return response;
    }


    /**
     * Create a new user if it doesn't exist
     *
     *
     * URL example: http://localhost:8080/resttest/v1/login/create
     *
     * @param userDetails
     * @return
     */
    @PUT
    @Path("create")
    @Consumes("application/json")
    @Produces("text/plain")
    public Response createNewUser(final UserAccountDetails userDetails) {
        final Response response;
        if (userDetails == null) {
            final ServicesHelper helper = new ServicesHelper();
            response = helper.emptyPayload();
        } else {
            final LoginCheck loginCheck = new LoginCheck();
            response = loginCheck.createUpdateUser(userDetails, true);
        }
        return response;
    }



    @GET
    @Path("forgotPassword/{email}")
    @Consumes("application/json")
    @Produces({"text/plain", "application/json"})
    public Response forgotPassword(@PathParam("email") final String email) {
        final Response response;
        if (email == null) {
            final ServicesHelper helper = new ServicesHelper();
            response = helper.emptyPayload();
        } else {
            final ForgotPassword forgotPassword = new ForgotPassword();
            response = forgotPassword.sendEmail(email);
        }
        return response;
    }


    @GET
    @Path("resetPassword/{emailToken}")
    @Consumes("application/json")
    @Produces({"text/plain", "application/json"})
    public Response resetPassword(@PathParam("emailToken") final String emailToken) {
        final Response response;
        if (emailToken == null) {
            final ServicesHelper helper = new ServicesHelper();
            response = helper.emptyPayload();
        } else {
            final ForgotPassword forgotPassword = new ForgotPassword();
            response = forgotPassword.resetPassword(emailToken);
        }
        return response;
    }


    @GET
    @Path("questions")
    @Consumes("application/json")
    @Produces("application/json")
    public Response question() {
        final ForgotPassword forgotPassword = new ForgotPassword();
        final Response response = forgotPassword.questions();
        return response;
    }


    @GET
    @Path("userDetails/{userEmailOrLogin}")
    @Consumes("application/json")
    @Produces({"text/plain", "application/json"})
    public Response userDetails(@PathParam("userEmailOrLogin") final String userEmailOrLogin) {
        final LoginCheck checkDetails = new LoginCheck();
        final Response response = checkDetails.retrieveUserDetails(userEmailOrLogin, false);
        return response;
    }


    @GET
    @Path("verifyDetails/{userEmailOrLogin}")
    @Consumes("application/json")
    @Produces({"text/plain", "application/json"})
    public Response verifyDetails(@PathParam("userEmailOrLogin") final String userEmailOrLogin) {
        final LoginCheck checkDetails = new LoginCheck();
        final Response response = checkDetails.retrieveUserDetails(userEmailOrLogin, true);
        return response;
    }


    @PUT
    @Path("updateDetails/{userEmailOrLogin}")
    @Consumes("application/json")
    @Produces({"text/plain", "application/json"})
    public Response updateDetails(final UserAccountDetails userAccountDetails) {
        final Response response;
        final ServicesHelper helper = new ServicesHelper();
        if (userAccountDetails == null) {
            response = helper.emptyPayload();
        } else {
            final ValidateRequest validateRequest = new ValidateRequest();
            boolean isValidData = validateRequest.validateUserDetailsUpdate(userAccountDetails);
            if (isValidData) {
                final LoginCheck loginCheck = new LoginCheck();
                response = loginCheck.createUpdateUser(userAccountDetails, false);
            } else {
                response = helper.badLoginPayload();
            }
        }
        return response;
    }
}
