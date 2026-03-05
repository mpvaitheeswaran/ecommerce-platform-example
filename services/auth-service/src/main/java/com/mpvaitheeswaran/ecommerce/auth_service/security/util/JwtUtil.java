package com.mpvaitheeswaran.ecommerce.auth_service.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;
import java.util.HexFormat;
import java.util.UUID;

@Component
public class JwtUtil {

    private final SecretKey signingKey;
    private final long accessTokenExpiryMs;
    private final long refreshTokenExpiryMs;

    // Constructor reads values from application.yml
    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiry-ms}") long accessTokenExpiryMs,
            @Value("${jwt.refresh-token-expiry-ms}") long refreshTokenExpiryMs) {

        // Decode the Base64-encoded secret from env var into a signing key
        this.signingKey = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secret));
        this.accessTokenExpiryMs  = accessTokenExpiryMs;
        this.refreshTokenExpiryMs = refreshTokenExpiryMs;
    }

    // ── ACCESS TOKEN ──────────────────────────────────────────────────────────
    // Short-lived (15 min). Contains userId, email, role as claims.
    // Validated on every request by the JWT filter.

    public String generateAccessToken(UUID userId, String email, String role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .claim("role", role)
                .claim("type", "ACCESS")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(accessTokenExpiryMs)))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    // ── REFRESH TOKEN ─────────────────────────────────────────────────────────
    // Long-lived (30 days). Opaque random UUID — NOT a JWT.
    // We store SHA-256(rawToken) in the DB.
    // On use: look up DB by hash, validate not expired/revoked, issue new access token.

    public String generateRefreshToken() {
        // UUID-based opaque token (unpredictable, not decodable)
        return UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();
    }

    // SHA-256 hash of the raw refresh token for secure DB storage
    public String hashRefreshToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    public Instant refreshTokenExpiry() {
        return Instant.now().plusMillis(refreshTokenExpiryMs);
    }

    // ── VALIDATION ────────────────────────────────────────────────────────────

    public Claims validateAccessToken(String token) {
        // Throws ExpiredJwtException, MalformedJwtException, SignatureException etc.
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isAccessToken(Claims claims) {
        return "ACCESS".equals(claims.get("type", String.class));
    }

    public UUID extractUserId(Claims claims) {
        return UUID.fromString(claims.getSubject());
    }

    public String extractRole(Claims claims) {
        return claims.get("role", String.class);
    }

    public long getAccessTokenExpirySeconds() {
        return accessTokenExpiryMs / 1000;
    }
}