package ci.mobidoc.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserRoleTest {

    @Test
    void shouldGetCorrectRoleValue() {
        assertThat(UserRole.ADMIN.getRole()).isEqualTo("ADMIN");
        assertThat(UserRole.DOCTOR.getRole()).isEqualTo("DOCTOR");
        assertThat(UserRole.AGENT.getRole()).isEqualTo("AGENT");
        assertThat(UserRole.PATIENT.getRole()).isEqualTo("PATIENT");
    }

    @ParameterizedTest
    @ValueSource(strings = {"admin", "ADMIN", "Admin"})
    void shouldConvertStringToRoleIgnoringCase(String roleString) {
        UserRole role = UserRole.fromString(roleString);
        assertThat(role).isEqualTo(UserRole.ADMIN);
    }

    @Test
    void shouldThrowExceptionForInvalidRole() {
        assertThrows(IllegalArgumentException.class, 
            () -> UserRole.fromString("INVALID_ROLE")
        );
    }

    @Test
    void shouldConvertAllValidRoles() {
        assertThat(UserRole.fromString("ADMIN")).isEqualTo(UserRole.ADMIN);
        assertThat(UserRole.fromString("DOCTOR")).isEqualTo(UserRole.DOCTOR);
        assertThat(UserRole.fromString("AGENT")).isEqualTo(UserRole.AGENT);
        assertThat(UserRole.fromString("PATIENT")).isEqualTo(UserRole.PATIENT);
    }
}