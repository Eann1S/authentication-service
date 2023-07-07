package com.example.authentication.service;

import com.example.authentication.exception.EmptyAuthenticationHeaderException;
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
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

import static com.example.authentication.constants.CachePrefix.JWT_CACHE_PREFIX;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${secret.key}")
    private String secretKey;
    private final MessageGenerator messageGenerator;
    private final CacheService cacheService;

    String generateJwt(UserDetails userDetails) {
        String jwt = Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 3600 * 24))
                .signWith(getSignInKey(secretKey))
                .compact();

        String email = userDetails.getUsername();
        cacheService.storeInCache(JWT_CACHE_PREFIX.formatted(email), jwt, Duration.ofDays(1));
        return jwt;
    }

    void invalidateJwtByEmail(String email) {
        cacheService.deleteFromCache(JWT_CACHE_PREFIX.formatted(email));
    }

    boolean isJwtValid(String jwt, UserDetails userDetails) {
        String email = extractEmailFromJwt(jwt);
        return email.equals(userDetails.getUsername()) && !isJwtExpired(jwt);
    }

    String extractEmailFromJwt(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }

    String getJwtFromRequest(HttpServletRequest httpRequest) {
        String header = httpRequest.getHeader(AUTHORIZATION);
        if (StringUtils.isBlank(header) || !header.startsWith("Bearer ")) {
            throw new EmptyAuthenticationHeaderException(messageGenerator.generateMessage("error.header.is_empty", AUTHORIZATION));
        }
        return header.split(" ")[1].trim();
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
