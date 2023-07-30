package com.example.authentication.service;

import com.example.authentication.exception.EmptyHeaderException;
import com.example.authentication.exception.ExpiredAuthenticationTokenException;
import com.example.authentication.exception.InvalidAuthenticationTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.function.Function;

import static com.example.authentication.constant.CachePrefix.JWT_CACHE_PREFIX;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${secret.key}")
    private String secretKey;
    private final MessageGenerator messageGenerator;
    private final CacheService cacheService;
    private final AccountService accountService;

    public String getUserEmailByJwt(String jwt) {
        String email = extractEmailFromJwt(jwt);
        validateJwtPresenceInCache(email);
        return accountService.getAccountByEmail(email).getEmail();
    }

    String generateJwt(UserDetails userDetails) {
        String jwt = Jwts.builder()
                .setClaims(Jwts.claims())
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

    String extractEmailFromJwt(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }

    String getJwtFromRequest(HttpServletRequest httpRequest) {
        String header = httpRequest.getHeader(AUTHORIZATION);
        if (StringUtils.isBlank(header) || !header.startsWith("Bearer ")) {
            throw new EmptyHeaderException(messageGenerator.generateMessage("error.header.is_empty", AUTHORIZATION));
        }
        return header.split(" ")[1].trim();
    }

    private void validateJwtPresenceInCache(String email) {
        if (cacheService.getFromCache(JWT_CACHE_PREFIX.formatted(email), String.class).isEmpty()) {
            throw new InvalidAuthenticationTokenException(messageGenerator.generateMessage("error.auth-token.invalid"));
        }
    }

    private <T> T extractClaim(String jwt, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(jwt);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String jwt) {
        Claims body;
        try {
            body = Jwts.parserBuilder()
                    .setSigningKey(getSignInKey(secretKey))
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();
        } catch (SignatureException | MalformedJwtException e) {
            throw new InvalidAuthenticationTokenException(messageGenerator.generateMessage("error.auth-token.invalid"), e);
        } catch (ExpiredJwtException e) {
            throw new ExpiredAuthenticationTokenException(messageGenerator.generateMessage("error.auth-token.expired"), e);
        }
        return body;
    }

    private Key getSignInKey(String secretKey) {
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }
}
