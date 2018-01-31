package au.com.rest.test.services.security;

import java.time.Duration;
import java.time.ZonedDateTime;

// This class would be the right place to query LDAP for the user.
public class TokenHandling {

    /**
     * If the token, that's a string date is 'older' than 10 minutes then it's not valid
     *
     * @param tokenDate
     * @return
     */
    public boolean checkToken(final String tokenDate) {
        // As token is just a timestamp for demo puposes I will simply check if the token is refreshed at pre-defined interval
        final ZonedDateTime dateTime = ZonedDateTime.parse(tokenDate);
        final ZonedDateTime now = ZonedDateTime.now();
        boolean isValidToken = false;
        if (!dateTime.isAfter(now) && dateTime.isAfter(now.minus(Duration.ofMinutes(10L)))) {
            isValidToken = true;
        }
        return isValidToken;
    }

    public String newToken() {
        final ZonedDateTime now = ZonedDateTime.now();
        final String nowAsString = now.toString();
        return nowAsString;
    }
}
