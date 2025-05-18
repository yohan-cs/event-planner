package com.yohan.event_planner.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

/**
 * Service responsible for generating and validating JWT tokens using JJWT version 0.12.6.
 *
 * <p>This service handles both creation and validation of tokens using a Base64-encoded secret key.
 * Expired tokens are gracefully handled inside service methods.</p>
 */
@Service
public class JwtService {

    /**
     * Base64-encoded secret key used for signing and validating JWTs.
     * Configure this in application.properties or application.yml under 'jwt.secret'.
     */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * JWT expiration time in milliseconds.
     * Configure this in application properties under 'jwt.expirationMillis'.
     */
    @Value("${jwt.expirationMillis}")
    private long jwtExpirationMillis;

    /**
     * Generates a signed JWT token for the specified username.
     * Uses HS256 signature algorithm with the signing key.
     *
     * @param username the username to set as JWT subject
     * @return the compact JWT string
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMillis))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts the username (subject) claim from the JWT token.
     * Returns null if the token is expired or invalid.
     *
     * @param token the JWT string
     * @return the username (subject) stored in the token, or null if token is expired or invalid
     */
    public String extractUsername(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (ExpiredJwtException e) {
            return null;
        }
    }

    /**
     * Validates the token by checking if the username matches and the token is not expired.
     *
     * @param token    the JWT token to validate
     * @param username the username expected in the token's subject
     * @return true if token is valid and not expired, false otherwise
     */
    public boolean isTokenValid(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return extractedUsername != null && extractedUsername.equals(username) && !isTokenExpired(token);
    }

    /**
     * Checks whether the JWT token has expired.
     * Returns true if the token is expired or throws {@code ExpiredJwtException} internally.
     *
     * @param token the JWT string
     * @return true if token expiration date is before current time or explicitly expired
     */
    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * Extracts the expiration date from the token claims.
     *
     * @param token the JWT string
     * @return the expiration date
     * @throws ExpiredJwtException if the token is expired
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts a claim from the JWT token using the provided claims resolver function.
     *
     * @param token          the JWT string
     * @param claimsResolver function to apply on Claims to extract specific data
     * @param <T>            the type of the extracted claim
     * @return the extracted claim value
     * @throws ExpiredJwtException if the token is expired
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parses the JWT token using JJWT 0.12.6 parsing API.
     * Uses {@code Jwts.parser()}, configures the signing key, builds the parser,
     * then parses the token and extracts claims.
     *
     * @param token the JWT string to parse
     * @return the JWT claims
     * @throws JwtException if token is invalid, expired, or signature does not match
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())  // Configure builder with signing key
                .build()                         // Build JwtParser instance
                .parseClaimsJws(token)           // Parse token and verify signature
                .getBody();                      // Extract claims (payload)
    }

    /**
     * Decodes the Base64-encoded secret and returns the signing key used for HS256 algorithm.
     *
     * @return the signing Key
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
