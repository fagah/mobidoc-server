package ci.mobidoc.domain.service;

import ci.mobidoc.domain.model.User;
import ci.mobidoc.domain.model.UserRole;
import ci.mobidoc.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
    }

    private Jwt createMockJwt(String subject, String email, String firstName, String lastName, String... roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("given_name", firstName);
        claims.put("family_name", lastName);

        Map<String, Object> resourceAccess = new HashMap<>();
        Map<String, Object> mobidocServer = new HashMap<>();
        mobidocServer.put("roles", Arrays.asList(roles));
        resourceAccess.put("mobidoc-server", mobidocServer);
        claims.put("resource_access", resourceAccess);

        return Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject(subject)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60))
                .claims(existingClaims -> existingClaims.putAll(claims))
                .build();
    }

    @Test
    void shouldCreateNewUserFromKeycloak() {
        // Given
        String keycloakId = "test-id";
        String email = "test@example.com";
        String firstName = "John";
        String lastName = "Doe";
        Jwt jwt = createMockJwt(keycloakId, email, firstName, lastName, "DOCTOR");
        
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(userRepository.findById(keycloakId)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        User result = userService.getCurrentUser(authentication);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getKeycloakId()).isEqualTo(keycloakId);
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getFirstName()).isEqualTo(firstName);
        assertThat(result.getLastName()).isEqualTo(lastName);
        assertThat(result.getRole()).isEqualTo(UserRole.DOCTOR);
        
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldUpdateExistingUser() {
        // Given
        String keycloakId = "test-id";
        String email = "test@example.com";
        String firstName = "John";
        String lastName = "Doe";
        
        User existingUser = new User();
        existingUser.setKeycloakId(keycloakId);
        existingUser.setEmail("old@example.com");
        existingUser.setRole(UserRole.PATIENT);

        Jwt jwt = createMockJwt(keycloakId, email, firstName, lastName, "DOCTOR");
        
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(userRepository.findById(keycloakId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        User result = userService.getCurrentUser(authentication);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getRole()).isEqualTo(UserRole.DOCTOR);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenNoValidRoleFound() {
        // Given
        String keycloakId = "test-id";
        Jwt jwt = createMockJwt(keycloakId, "test@example.com", "John", "Doe", "INVALID_ROLE");
        when(authentication.getPrincipal()).thenReturn(jwt);

        // When/Then
        assertThrows(RuntimeException.class, () -> userService.getCurrentUser(authentication));
    }

    @Test
    void shouldFindUserByEmail() {
        // Given
        String email = "test@example.com";
        User expectedUser = new User();
        expectedUser.setEmail(email);
        
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(expectedUser));

        // When
        User result = userService.findByEmail(email);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundByEmail() {
        // Given
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(RuntimeException.class, () -> userService.findByEmail(email));
    }
}