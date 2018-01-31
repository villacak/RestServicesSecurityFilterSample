package au.com.rest.test.services.validate;

import au.com.persistence.test.dao.PersistenceDAO;
import au.com.persistence.test.entities.user.UserDetailsEntity;
import au.com.persistence.test.entities.user.UserEntity;
import au.com.persistence.test.enums.KeyValueForSearch;
import au.com.rest.test.pojos.*;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateRequest {

    /**
     * Validate UserAccountDetails, all fields except for Id fields.
     *
     * @param userAccountDetails
     * @return
     */
    public boolean validateUserDetails(final UserAccountDetails userAccountDetails) {
        boolean isValidLogin = validateUserLogin(userAccountDetails.getUserLogin());
        boolean isValidAnswers = validateAnswers(userAccountDetails.getAnswerList());
        boolean isValidEmail = validateEmail(userAccountDetails.getEmail());
        boolean isValidFullName = (userAccountDetails.getFullName() == null ||
                userAccountDetails.getFullName().length() == 0)? false: true;
        boolean isValidPhone = (userAccountDetails.getPhone() == null ||
                userAccountDetails.getPhone().length() == 0)? false: true;
        boolean isValid = isValidLogin && isValidAnswers && isValidEmail && isValidFullName && isValidPhone;
        return isValid;
    }


    /**
     * Validate the extra bit for when updating details
     * UserAccountDetails id, UserLogin id and login cannot change.
     *
     * @param userAccountDetails
     * @return
     */
    public boolean validateUserDetailsUpdate(final UserAccountDetails userAccountDetails) {
        final UserLogin login = userAccountDetails.getUserLogin();
        boolean areValidIds = (userAccountDetails.getId() != 0 && login.getId() != 0)? true: false;
        boolean isValid = validateUserDetails(userAccountDetails) && areValidIds;

        if (isValid) {
            final PersistenceDAO dao = new PersistenceDAO();
            final UserDetailsEntity userLoginDetails = dao.getUserDetails(login.getLogin(), KeyValueForSearch.LOGIN);
            final UserEntity userEntity = userLoginDetails.getUserEntity();

            if (userLoginDetails != null && userEntity != null &&
                    login.getId() == userEntity.getId() &&
                    login.getLogin().equals(userEntity.getLogin()) &&
                    userAccountDetails.getId() == userLoginDetails.getId()) {
                isValid = true;
            } else {
                isValid = false;
            }
        }

        return isValid;
    }



    /**
     * Validate UserLogin
     * It cannot contain null
     *
     * @param userLogin
     * @return
     */
    private boolean validateUserLogin(final UserLogin userLogin) {
        boolean isValid = true;
        if (userLogin == null ||
                userLogin.getLogin() == null || userLogin.getLogin().length() == 0 ||
                userLogin.getPassword() == null || userLogin.getPassword().length() == 0) {
            isValid = false;
        }
        return isValid;
    }


    /**
     * Validate Answers
     * It cannot contain any field with null
     *
     * @param answers
     * @return
     */
    private boolean validateAnswers(final List<Answer> answers) {
        boolean isValid = true;
        if (answers == null || answers.size() == 0) {
            isValid = false;
        } else {
            for (Answer tempAnswer : answers) {
                boolean isValidQuestion = true;
                final Question question = tempAnswer.getQuestion();
                if (validateQuestion(tempAnswer.getQuestion())) {
                    if (tempAnswer.getAnswer() == null || tempAnswer.getAnswer().length() == 0) {
                        isValid = false;
                        break;
                    }
                } else {
                    isValid = false;
                    break;
                }
            }
        }
        return isValid;
    }


    /**
     * Validate question
     * Cannot contain null
     *
     * @param question
     * @return
     */
    private boolean validateQuestion(final Question question) {
        boolean isValid = true;
        if (question == null ||
                question.getId() == 0 ||
                question.getQuestion() == null || question.getQuestion().length() == 0) {
            isValid = false;
        }
        return isValid;
    }


    /**
     * Validate email
     *
     * @param email
     * @return
     */
    public boolean validateEmail(final String email) {
        boolean isValid = true;
        try {
            final InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
        } catch (AddressException e) {
            isValid = false;
            e.printStackTrace();
        }
        return isValid;
    }


    /**
     * Validate password
     *
     * REGEX:
     * ^                # start-of-string
     *(?=.*[0-9])       # a digit must occur at least once
     *(?=.*[a-z])       # a lower case letter must occur at least once
     *(?=.*[A-Z])       # an upper case letter must occur at least once
     *(?=.*[@#$%^&+=])  # a special character must occur at least once
     *$                 # end-of-string
     *
     * Rules from doc at:
     * https://archtis.atlassian.net/wiki/spaces/TIS1/pages/169443362/Password+and+Challenging+Questions+Policy
     *
     * @param password
     * @return
     */
    public boolean validatePassword(final String password) {
        final int MINIMAL_LENGTH = 10;
        final int OPTIMAL_LENGTH = 13;
        final int passwordLength = password.length();
        boolean isValid = false;

        final String PASSWORD_REGEX;
        final Pattern pattern;
        final Matcher matcher;
        if (passwordLength >= OPTIMAL_LENGTH) {
            PASSWORD_REGEX = "^[0-9a-zA-Z~!$%^&*_=+}{\\'?].{13,}$";
            pattern = Pattern.compile(PASSWORD_REGEX);
            matcher = pattern.matcher(password);
            isValid = matcher.find();
        } else if (passwordLength >= MINIMAL_LENGTH && passwordLength < OPTIMAL_LENGTH) {
            PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[~!$%^&*_=+}{\\'?]).{10,}$";
            pattern = Pattern.compile(PASSWORD_REGEX);
            matcher = pattern.matcher(password);
            isValid = matcher.find();
        } else {
            isValid = false;
        }
        return isValid;
    }
}
