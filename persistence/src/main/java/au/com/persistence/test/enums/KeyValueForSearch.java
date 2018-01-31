package au.com.persistence.test.enums;

public enum KeyValueForSearch {

    FULL_NAME("fullName"),
    EMAIL("email"),
    LOGIN("login"),
    ACCOUNT_STATE("accountState"),
    ID("id"),
    QUESTION("question"),
    ANSWER("answer"),
    EMAIL_TOKEN("emailToken");

    private String type;

    private KeyValueForSearch(final String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
