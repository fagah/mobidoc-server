package ci.mobidoc.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@TestConfiguration
public class TestSecurityConfig {
    
    @Bean
    public JwtDecoder jwtDecoder() {
        // Providing a dummy decoder for tests
        return token -> null;
    }
}
