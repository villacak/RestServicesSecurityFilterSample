package au.com.rest.test.services.helper;

import au.com.rest.test.pojos.UserAccountDetails;

public class ValidateHelper {

    final int MINIMUM_LENGTH = 3;

    /**
     * Validate if the payload to create a new user has the minimum data
     * that's login, password, email and full name
     * All of them must have at least length of 3
     *
     * @param payload
     * @return
     */
    public boolean isValidateUserDetails(final UserAccountDetails payload) {
        boolean isValidData = false;
        if (hasMinimumData(payload.getUserLogin().getLogin()) &&
                hasMinimumData(payload.getUserLogin().getPassword()) &&
                hasMinimumData(payload.getEmail()) &&
                hasMinimumData(payload.getFullName())) {
            isValidData = true;
        }
        return isValidData;
    }


    /**
     * Check for the minimum length if not null
     *
     * @param data
     * @return
     */
    private boolean hasMinimumData(final String data) {
        boolean isValid = false;
        if (data != null && data.length() > MINIMUM_LENGTH) {
            isValid = true;
        }
        return isValid;
    }
}
