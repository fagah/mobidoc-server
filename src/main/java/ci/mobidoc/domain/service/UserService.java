package ci.mobidoc.domain.service;

import ci.mobidoc.domain.model.User;
import ci.mobidoc.domain.model.UserRole;
import ci.mobidoc.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    
    @Transactional
    public User getCurrentUser(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String keycloakId = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        String firstName = jwt.getClaimAsString("given_name");
        String lastName = jwt.getClaimAsString("family_name");
        UserRole role = extractUserRole(jwt);
        
        return userRepository.findById(keycloakId)
                .map(user -> updateUserFromKeycloak(user, email, firstName, lastName, role))
                .orElseGet(() -> createUserFromKeycloak(keycloakId, email, firstName, lastName, role));
    }
    
    private User createUserFromKeycloak(String keycloakId, String email, String firstName, String lastName, UserRole role) {
        User user = new User();
        user.setKeycloakId(keycloakId);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(role);
        return userRepository.save(user);
    }
    
    private User updateUserFromKeycloak(User user, String email, String firstName, String lastName, UserRole role) {
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(role);
        return userRepository.save(user);
    }

    private UserRole extractUserRole(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
        
        if (resourceAccess != null && resourceAccess.containsKey("mobidoc-server")) {
            Map<String, Object> resource = (Map<String, Object>) resourceAccess.get("mobidoc-server");
            if (resource != null && resource.containsKey("roles")) {
                Set<String> roles = ((java.util.List<String>) resource.get("roles"))
                        .stream()
                        .map(String::toUpperCase)
                        .collect(Collectors.toSet());
                
                // Check roles in priority order
                if (roles.contains("ADMIN")) return UserRole.ADMIN;
                if (roles.contains("DOCTOR")) return UserRole.DOCTOR;
                if (roles.contains("AGENT")) return UserRole.AGENT;
                if (roles.contains("PATIENT")) return UserRole.PATIENT;
            }
        }
        
        // Default role or throw exception based on your requirements
        throw new RuntimeException("No valid role found for user");
    }
    
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}