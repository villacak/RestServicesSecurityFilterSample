package au.com.rest.test.business;

import au.com.persistence.test.dao.PersistenceDAO;
import au.com.persistence.test.entities.user.QuestionsEntity;
import au.com.persistence.test.entities.user.UserDetailsEntity;
import au.com.persistence.test.enums.KeyValueForSearch;
import au.com.rest.test.business.mappers.AppMapper;
import au.com.rest.test.pojos.Question;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class ForgotPassword {

    /**
     * From the email received it search for the email and send it if found or not
     *
     * Please update the details from FROM, STANDARD_PASSWORD_FOR_THE_APPLICATION, HOST
     *
     * @param emailTo
     * @return
     */
    public Response sendEmail(final String emailTo) {
        final String FROM = "<senderEmail>"; // Your email
        final String FROM_DESCRIPTION_NAME = "Some Email"; // Need to ask to Aruna the user and password.
        final String STANDARD_PASSWORD_FOR_THE_APPLICATION = "<senderPassword>";
        final String HOST = "<serverName>"; // mail.archtis.com
        final String PORT = "25";
        final String SMTP_HOST = "mail.smtp.host";
        final String SMTP_PORT = "mail.smtp.port";
        final String UTF_8 = "UTF-8";
        final String HTML = "html";

        Response response = null;

        final Properties properties = System.getProperties();
        properties.setProperty(SMTP_HOST, HOST);
        properties.setProperty(SMTP_PORT, PORT);

        final Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                final PasswordAuthentication passwordAuthentication = new PasswordAuthentication(FROM, STANDARD_PASSWORD_FOR_THE_APPLICATION);
                return passwordAuthentication;
            }
        };

        final Session session = Session.getDefaultInstance(properties, auth);

        try {
            final String token = retrieveUserToken(emailTo);
            if (token != null) {
                final StringBuffer link = new StringBuffer("http://localhost:8080/resttest/v1/resetPassword/")
                        .append(token);
                final StringBuffer content = new StringBuffer("<html><body><a href='")
                        .append(link).append("'>").append(link).append("</a> </body></html>");
                final StringBuffer textMessage = new StringBuffer("Click on the link bellow to reset your password.")
                        .append("\n")
                        .append(content);
                final InternetAddress internetAddressFrom = new InternetAddress(FROM, FROM_DESCRIPTION_NAME);
                final InternetAddress internetAddressTo = new InternetAddress(emailTo);
                final MimeMessage mimeMessage = new MimeMessage(session);
                mimeMessage.setFrom(internetAddressFrom);
                mimeMessage.addRecipient(Message.RecipientType.TO, internetAddressTo);
                mimeMessage.setSubject("ArchTIS Password Reset");
                mimeMessage.setText(textMessage.toString(), UTF_8, HTML);

                Transport.send(mimeMessage);
                response = Response.ok("Email sent, you should receive it within minutes.").build();
            } else {
                response = Response.status(Response.Status.BAD_REQUEST).entity("Doesn't exist any user with this email.").build();
            }
        } catch (Exception e) {
            response = Response.status(Response.Status.BAD_REQUEST).entity("Fail to send email").build();
            e.printStackTrace();
        }
        return response;
    }


    /**
     * Search for the user email, if exist generate a token and save it, then return the token to be part of the url
     *
     * @param email
     * @return
     */
    private String retrieveUserToken(final String email) {
        final String token;
        final PersistenceDAO dao = new PersistenceDAO();
        final UserDetailsEntity detailsEntities = dao.getUserDetails(email, KeyValueForSearch.EMAIL);
        if (detailsEntities != null) {
            token = UUID.randomUUID().toString();
            detailsEntities.setEmailToken(token);
            dao.saveData(detailsEntities);
        } else {
            token = null;
        }
        return token;
    }


    private String removeUserToken(final String email) {
        final String token;
        final PersistenceDAO dao = new PersistenceDAO();
        final UserDetailsEntity detailsEntities = dao.getUserDetails(email, KeyValueForSearch.EMAIL);
        if (detailsEntities != null) {
            token = UUID.randomUUID().toString();
            detailsEntities.setEmailToken(null);
            dao.saveData(detailsEntities);
        } else {
            token = null;
        }
        return token;
    }




    /**
     * Link should redirect the user for an page where the user will be able to reset the password
     * If the user has already reset it, an unauthorized error will be displayed.
     *
     * @param emailToken
     * @return
     */
    public Response resetPassword(final String emailToken) {
        final Response response;
        final PersistenceDAO dao = new PersistenceDAO();
        final UserDetailsEntity detailsEntities = dao.getUserDetails(emailToken, KeyValueForSearch.EMAIL_TOKEN);
        if (detailsEntities != null) {
            detailsEntities.setEmailToken(null); // This set to null should occur after the user has updated data
            final UserDetailsEntity detailsEntitiesCheck = dao.saveData(detailsEntities);
            response = Response.ok("We should return an URL that the UI would redirect to the resetPassword page").build();
        } else {
            response= Response.status(Response.Status.UNAUTHORIZED).build();
        }
        return response;
    }


    /**
     * Retrieve all questions
     *
     * @return
     */
    public Response questions() {
        final PersistenceDAO dao = new PersistenceDAO();
        final List<QuestionsEntity> questionsEntities = dao.getQuestions(null, KeyValueForSearch.QUESTION);
        final AppMapper mapper = new AppMapper();
        final List<Question> questionList = new ArrayList<>(questionsEntities.size());
        for(QuestionsEntity tempQuestion: questionsEntities) {
            final Question temp = mapper.toQuestion(tempQuestion);
            questionList.add(temp);
        }
        final Response response = Response.ok(questionList).build();
        return response;
    }
}
