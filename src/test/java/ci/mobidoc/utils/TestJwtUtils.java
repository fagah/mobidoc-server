package ci.mobidoc.utils;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TestJwtUtils {

    public static Jwt createJwt(String subject, String... roles) {
        Map<String, Object> claims = new HashMap<>();
        Map<String, Object> resourceAccess = new HashMap<>();
        Map<String, Object> mobidocServer = new HashMap<>();
        
        mobidocServer.put("roles", Arrays.asList(roles));
        resourceAccess.put("mobidoc-server", mobidocServer);
        claims.put("resource_access", resourceAccess);
        claims.put("preferred_username", subject);

        return Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject(subject)
                .claim("scope", "openid profile email")
                .claims(existingClaims -> existingClaims.putAll(claims))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .build();
    }

    public static String createAuthorizationHeader(String subject, String... roles) {
        return "Bearer " + createJwt(subject, roles).getTokenValue();
    }
}