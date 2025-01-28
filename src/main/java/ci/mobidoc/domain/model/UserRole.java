package ci.mobidoc.domain.model;

public enum UserRole {
    ADMIN("ADMIN"),
    AGENT("AGENT"),
    DOCTOR("DOCTOR"),
    PATIENT("PATIENT");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public static UserRole fromString(String role) {
        for (UserRole userRole : UserRole.values()) {
            if (userRole.role.equalsIgnoreCase(role)) {
                return userRole;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + role);
    }
}