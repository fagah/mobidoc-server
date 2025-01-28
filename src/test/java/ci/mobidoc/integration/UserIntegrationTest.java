package ci.mobidoc.integration;

import ci.mobidoc.BaseIntegrationTest;
import ci.mobidoc.domain.model.User;
import ci.mobidoc.domain.model.UserRole;
import ci.mobidoc.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

class UserIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSynchronizeUserOnFirstAuthentication() throws Exception {
        // Given
        String keycloakId = "test-user-id";
        String token = getAuthorizationHeader("test-user-id", "DOCTOR");

        // When
        mockMvc.perform(get("/api/v1/users/me")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("DOCTOR"));

        // Then
        User user = userRepository.findById(keycloakId).orElseThrow();
        assertThat(user.getRole()).isEqualTo(UserRole.DOCTOR);
    }

    @Test
    @Sql("/sql/insert_test_user.sql")
    void shouldUpdateExistingUserRole() throws Exception {
        // Given
        String keycloakId = "existing-user-id";
        String token = getAuthorizationHeader(keycloakId, "ADMIN");

        // When
        mockMvc.perform(get("/api/v1/users/me")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"));

        // Then
        User user = userRepository.findById(keycloakId).orElseThrow();
        assertThat(user.getRole()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    void shouldRejectInvalidRole() throws Exception {
        // Given
        String token = getAuthorizationHeader("test-user-id", "INVALID_ROLE");

        // When/Then
        mockMvc.perform(get("/api/v1/users/me")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError);
    }
}