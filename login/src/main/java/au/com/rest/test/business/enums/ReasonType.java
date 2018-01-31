package au.com.rest.test.business.enums;

public enum ReasonType {
    ALLOWED(0), TOO_MANY_TRYS(1), REVOKED(2), SUSPENDED(3), CANCELED(4);

    private int type;

    private ReasonType(final int type) {
        this.type = type;
    }

    public int getCode() {
        return type;
    }
}
