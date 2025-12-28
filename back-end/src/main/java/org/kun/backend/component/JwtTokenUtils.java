package org.kun.backend.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kun.backend.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenUtils {

    @Value("${jwt.expiration.access}")
    private long accessExpMs;

    @Value("${jwt.expiration.refresh}")
    private long refreshExpMs;

    @Value("${jwt.secret}")
    private String secretKey;

    // Cache secret key để tránh decode nhiều lần
    private SecretKey cachedSecretKey;

    private SecretKey getKey() {
        if (cachedSecretKey == null) {
            try {
                cachedSecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
            } catch (IllegalArgumentException e) {
                log.error("Invalid JWT secret key format", e);
                throw new RuntimeException("JWT secret key configuration error", e);
            }
        }
        return cachedSecretKey;
    }

    public String generateAccessToken(User user) {

        Date now = new Date();
        Date exp = new Date(now.getTime() + accessExpMs);

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", user.getId())
                .issuedAt(now)
                .expiration(exp)
                .signWith(getKey())
                .compact();
    }

    public String generateRefreshToken(User user) {

        Date now = new Date();
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + refreshExpMs))
                .signWith(getKey())
                .compact();
    }

    private Claims extractAll(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.debug("JWT token expired: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            log.warn("Malformed JWT token: {}", e.getMessage());
            throw new JwtException("Invalid token format", e);
        } catch (SignatureException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
            throw new JwtException("Invalid token signature", e);
        } catch (JwtException e) {
            log.warn("JWT parsing error: {}", e.getMessage());
            throw new JwtException("Token validation failed", e);
        } catch (Exception e) {
            log.error("Unexpected error parsing JWT token", e);
            throw new JwtException("Token processing error", e);
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> fn) {
        try {
            return fn.apply(extractAll(token));
        } catch (ExpiredJwtException e) {
            // Với expired token, vẫn có thể extract claims từ payload
            return fn.apply(e.getClaims());
        }
    }

    public boolean isExpired(String token) {
        try {
            Date expiration = extractClaim(token, Claims::getExpiration);
            return expiration != null && expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (JwtException e) {
            log.warn("Cannot check token expiration: {}", e.getMessage());
            return true; // Coi như expired nếu không parse được
        }
    }

    public boolean isValid(String token) {
        try {
            extractAll(token);
            return !isExpired(token);
        } catch (JwtException e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (JwtException e) {
            log.warn("Cannot extract username from token: {}", e.getMessage());
            return null;
        }
    }
}

