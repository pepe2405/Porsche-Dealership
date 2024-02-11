package bg.sofia.uni.fmi.mjt.dealership.server.users;

public enum UserRole {
    CUSTOMER("customer"),
    STAFF("staff"),
    UNAUTHORIZED("unauthorized");

    private String value;

    UserRole(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
