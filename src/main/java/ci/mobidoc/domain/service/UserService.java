package ci.mobidoc.domain.service;

import ci.mobidoc.domain.model.User;
import ci.mobidoc.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    
    @Transactional
    public User getCurrentUser(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String keycloakId = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        
        return userRepository.findById(keycloakId)
                .orElseGet(() -> createUserFromKeycloak(keycloakId, email));
    }
    
    private User createUserFromKeycloak(String keycloakId, String email) {
        User user = new User();
        user.setKeycloakId(keycloakId);
        user.setEmail(email);
        return userRepository.save(user);
    }
    
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}