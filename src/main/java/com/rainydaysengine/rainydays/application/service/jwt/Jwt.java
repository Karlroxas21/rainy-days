package com.rainydaysengine.rainydays.application.service.jwt;

import com.rainydaysengine.rainydays.errors.ApplicationError;
import com.rainydaysengine.rainydays.utils.CallResult;
import com.rainydaysengine.rainydays.utils.CallWrapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class Jwt {
    private static final Logger logger = LoggerFactory.getLogger(Jwt.class);

    private String secretKey = "";

    private String ALGORITHM = "HmacSHA256";

    public Jwt() {
        CallResult<KeyGenerator> keyGenerator = CallWrapper.syncCall(() -> KeyGenerator.getInstance(ALGORITHM));
        if (keyGenerator.isFailure()) {
            logger.error("Jwt#Jwt(): Error generating key for JWT: {}", keyGenerator.getError());
            throw ApplicationError.InternalError(keyGenerator.getError());
        }

        SecretKey sk = keyGenerator.getResult().generateKey();
        secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());
        System.out.println(secretKey);
    }

    public String generateToken(String identity) {
        Map<String, Object> claims = new HashMap<>();

        // Now
        Instant nowInstant = Instant.now();

        // Duration
        Duration duration = Duration.ofMinutes(60);

        // Expiration
        Instant expirationInstant = nowInstant.plus(duration);

        String jwtToken = Jwts.builder()
                .claims()
                .add(claims)
                .subject(identity)
                .issuedAt(Date.from(nowInstant))
                .expiration(Date.from(expirationInstant))
                .and()
                .signWith(getKey())
                .compact();

        return jwtToken;
    }

    public SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);

        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
