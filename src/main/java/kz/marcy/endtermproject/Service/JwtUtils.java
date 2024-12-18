package kz.marcy.endtermproject.Service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;

@Component
public class JwtUtils {

    private final SecretKey secretKey;
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10 hours

    public JwtUtils(@Value("${jwt.secret}") String secret) {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String login, String role, String id, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("id", id);
        claims.put("username", username);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(login)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }


    public boolean validateToken(String token, String username) {
        try {
            final String extractedUsername = extractLogin(token);
            return extractedUsername.equals(username) && !isTokenExpired(token);
        } catch (ExpiredJwtException e) {
            System.err.println("Token expired: " + e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            System.err.println("Unsupported JWT: " + e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            System.err.println("Malformed JWT: " + e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            System.err.println("Illegal argument: " + e.getMessage());
            return false;
        }
    }

    // Extract Username
    public String extractLogin(String token) {
        return extractClaims(token).getSubject();
    }

    public String extractUsername(String token) {
        return extractClaims(token).get("username", String.class);
    }

    // Extract Roles
    public String extractRoles(String token) {
        return extractClaims(token).get("role", String.class);
    }



    public Authentication getAuthentication(String username, String role) {
        GrantedAuthority authority = new SimpleGrantedAuthority(role);
        return new UsernamePasswordAuthenticationToken(username, null, List.of(authority));
    }

    public String extractId(String token) {
        return extractClaims(token).get("id", String.class);
    }


    // Check Expiry
    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // Extract Claims
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
