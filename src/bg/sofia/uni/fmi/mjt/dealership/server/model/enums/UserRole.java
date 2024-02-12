package bg.sofia.uni.fmi.mjt.dealership.server.model.enums;

/**
 * The UserRole enum represents the role of a user in the dealership system.
 * It can be CUSTOMER, STAFF, or UNAUTHORIZED.
 */
public enum UserRole {
    CUSTOMER("customer"),
    STAFF("staff"),
    UNAUTHORIZED("unauthorized");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
