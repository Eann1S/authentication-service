package com.example.authentication.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.function.Function;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${JWT_SECRET_KEY}")
    private String secretKey;
    private final MessageGenerator messageGenerator;

    public String generateJwt(UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 3600 * 24))
                .signWith(getSignInKey(secretKey))
                .compact();
    }

    public boolean isJwtValid(String jwt, UserDetails userDetails) {
        String email = extractEmail(jwt);
        return email.equals(userDetails.getUsername()) && !isJwtExpired(jwt);
    }

    public String extractEmail(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }

    public String getJwtFromRequest(HttpServletRequest httpRequest) {
        String header = httpRequest.getHeader(AUTHORIZATION);
        if (StringUtils.isBlank(header) || !header.startsWith("Bearer ")) {
            throw new NoSuchElementException(messageGenerator.generateMessage("error.header.is_empty", AUTHORIZATION));
        }
        return header.substring(7);
    }

    private boolean isJwtExpired(String jwt) {
        return extractClaim(jwt, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String jwt, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(jwt);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey(secretKey))
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    private Key getSignInKey(String secretKey) {
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }
}
