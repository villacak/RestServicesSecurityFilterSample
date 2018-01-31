package au.com.rest.test.business;

import au.com.persistence.test.dao.PersistenceDAO;
import au.com.persistence.test.entities.user.AnswersEntity;
import au.com.persistence.test.entities.user.UserDetailsEntity;
import au.com.persistence.test.entities.user.UserEntity;
import au.com.persistence.test.entities.user.UserSecurityEntity;
import au.com.persistence.test.enums.KeyValueForSearch;
import au.com.rest.test.business.enums.ReasonType;
import au.com.rest.test.business.mappers.AppMapper;
import au.com.rest.test.pojos.UserLogin;
import au.com.rest.test.pojos.UserAccountDetails;
import au.com.rest.test.services.helper.ServicesHelper;
import au.com.rest.test.services.validate.ValidateRequest;

import javax.ws.rs.core.Response;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

public class LoginCheck {

    private final int MAX_TRYS = 3;
    private final String TOKEN = "token";

    /**
     * Check if the credentials exist or not,
     * Here we may query LDAP or at TokenHandling class. So when the code reach here we already know
     * if the user exist and is valid or not
     *
     * @param userLogin
     * @return
     */
    public Response checkLogin(final UserLogin userLogin) {
        final Response response;
        if (userLogin.getLogin() == null || userLogin.getPassword() == null) {
            final ServicesHelper helper = new ServicesHelper();
            response = helper.badLoginPayload();
        } else {
            // Here the right thing to do is have a rest call to a rest inner service, in this case
            // the persistence service layer.
            // But here we will just call the service.
            final PersistenceDAO daoUser = new PersistenceDAO();
            final UserSecurityEntity security = daoUser.getUserSecurity(userLogin.getLogin(), KeyValueForSearch.LOGIN);
            final Timestamp timestampNow = new Timestamp(System.currentTimeMillis());
            if (security.getId() > 0 &&
                    security.getUserEntity().getPassword() != null &&
                    security.getReason() == ReasonType.ALLOWED.getCode() &&
                    security.getUserEntity().getPassword().equals(userLogin.getPassword())) {
                userLogin.setId(security.getId());
                security.setLastAccess(timestampNow);
                security.setReason(0);
                security.setFailedTrys(0);
                daoUser.saveData(security);
                userLogin.setPassword(null);
                response = Response.ok(userLogin).build();
            } else {
                if (security != null) {
                    final Timestamp ts = new Timestamp(System.currentTimeMillis());
                    int trys = security.getFailedTrys();
                    if (trys < MAX_TRYS) {
                        security.setFailedTrys(++trys);
                    } else {
                        security.setReason(ReasonType.TOO_MANY_TRYS.getCode());
                    }
                    security.setLastAccess(ts);
                }
                security.setLastAccess(timestampNow);
                daoUser.saveData(security);
                daoUser.closeEntityManager();
                response = Response.status(Response.Status.FORBIDDEN).entity("Login and/or password are wrong or user doesn't exist.").build();
            }
        }

        return response;
    }


    /**
     * Create a new user if doesn't exist
     * <p>
     * Everywhere you see PersistenceDAO will be replaced by a rest client call
     *
     * @param userAccountDetails
     * @return
     */
    public Response createUpdateUser(final UserAccountDetails userAccountDetails, final boolean isCreate) {
        final Response response;
        final UserLogin tempUserLogin = userAccountDetails.getUserLogin();
        if (tempUserLogin != null && tempUserLogin.getLogin() == null || tempUserLogin.getPassword() == null) {
            final ServicesHelper helper = new ServicesHelper();
            response = helper.badLoginPayload();
        } else if (tempUserLogin != null) {
            final ValidateRequest validate = new ValidateRequest();
            final boolean isValidPassword = validate.validatePassword(tempUserLogin.getPassword());
            if (isValidPassword) {
                final PersistenceDAO daoUser = new PersistenceDAO();
                final UserDetailsEntity tempUserEntity = daoUser.getUserDetails(tempUserLogin.getLogin(), KeyValueForSearch.LOGIN);
                UserDetailsEntity entityDetails = null;
                final AppMapper mapper = new AppMapper();
                try {
                    if (tempUserEntity != null) {
                        // Updating details
                        entityDetails = mapper.toUserDetailsToEntity(userAccountDetails);
                        entityDetails.setEmailToken(null);
                        entityDetails = daoUser.saveData(entityDetails);
                        final List<AnswersEntity> entities = mapper.toAnswersEntity(userAccountDetails.getAnswerList());

                        // Maybe add two way JPA relationship just for UserDetailsEntity to avoid this.
                        if (entityDetails != null) {
                            for (AnswersEntity answersEntity : entities) {
                                answersEntity.setUserDetailsEntity(entityDetails);
                                daoUser.saveData(answersEntity);
                            }
                        }
                        response = Response.ok("Details updated with success.").build();
                    } else {
                        // New User
                        UserEntity entity = daoUser.getUser(tempUserLogin.getLogin());
                        if (entity != null) {
                            response = Response.status(Response.Status.CONFLICT).entity("User already exist.").build();
                        } else {
                            entityDetails = mapper.toUserDetailsToEntity(userAccountDetails);
                            entity = entityDetails.getUserEntity();
                            entityDetails = daoUser.saveData(entityDetails);

                            if (entityDetails.getUserEntity() != null &&
                                    entityDetails.getUserEntity().getPassword().equals(entity.getPassword())) {
                                createUpdateUserSecurityWhenCreatingOrUpdatingDetails(daoUser, entityDetails);
                            }
                            final String message = "User created with success.";
                            response = Response.ok(message).build();
                        }
                        entity = null;
                    }

                } finally {
                    entityDetails = null;
                    daoUser.closeEntityManager();
                }
            } else {
                response = Response.status(Response.Status.EXPECTATION_FAILED).entity("Password doesn't have the minimum to be secure.").build();
            }
        } else {
            response = Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized").build();
        }
        return response;
    }


    /**
     * Create or update user securityu details
     *
     * @param dao
     * @param entityDetails
     */
    private void createUpdateUserSecurityWhenCreatingOrUpdatingDetails(final PersistenceDAO dao, final UserDetailsEntity entityDetails) {
        final UserSecurityEntity security = new UserSecurityEntity();
        security.setLastAccess(Timestamp.from(Instant.now()));
        security.setFailedTrys(0);
        security.setReason(ReasonType.ALLOWED.getCode());
        security.setUserEntity(entityDetails.getUserEntity());
        security.setAccountState(ReasonType.ALLOWED.name());
        dao.saveData(security);
    }


    /**
     * Retrieve user details without the answer, mean that the user will need to put the answers for
     * verification
     *
     * @param userOrEmail
     * @return
     */
    public Response retrieveUserDetails(final String userOrEmail, final boolean hasAnswers) {
        boolean isEmail = false;
        Response response = null;
        UserAccountDetails accountDetails = null;
        UserDetailsEntity detailsEntity = null;

        final PersistenceDAO dao = new PersistenceDAO();
        final AppMapper mapper = new AppMapper();

        final ValidateRequest validate = new ValidateRequest();
        isEmail = validate.validateEmail(userOrEmail);
        if (isEmail) {
            detailsEntity = dao.getUserDetails(userOrEmail, KeyValueForSearch.EMAIL);
            accountDetails = mapper.toUserDetails(detailsEntity, hasAnswers);
        } else {
            detailsEntity = dao.getUserDetails(userOrEmail, KeyValueForSearch.LOGIN);
        }

        if (!hasAnswers) {
            accountDetails.getUserLogin().setPassword(null);
        }

        if (detailsEntity != null) {
            response = Response.ok(accountDetails).build();
        } else {
            response = Response.status(Response.Status.NO_CONTENT).build();
        }

        detailsEntity = null;
        accountDetails = null;
        return response;
    }
}
