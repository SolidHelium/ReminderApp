package com.reminderapp.reminder.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
public class JwtUtil {
    private final String secret;
    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final long jwtExpirationMs = 36000000;

    public JwtUtil(@Value("${app.jwt.secret}") String secret) {
        this.secret = secret;
        this.algorithm = Algorithm.HMAC256(secret.getBytes());
        this.verifier = JWT.require(algorithm).build();
    }

    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        String jwt = JWT.create()
                .withSubject(userDetails.getUsername())
                .withIssuedAt(now)
                .withExpiresAt(new Date(now.getTime() + jwtExpirationMs))
                .sign(algorithm);
        return jwt;
    }

    public String extractUsername(String token) {
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            DecodedJWT jwt = verifier.verify(token);
            String username = jwt.getSubject();
            Date expires = jwt.getExpiresAt();
            return (username.equals(userDetails.getUsername()) && expires.after(new Date()));
        } catch (JWTVerificationException e) {
            return false;
        }
    }
}
