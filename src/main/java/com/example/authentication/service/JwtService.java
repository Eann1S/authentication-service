package com.example.authentication.service;

import com.example.authentication.entity.Account;
import com.example.authentication.exception.ExpiredAuthenticationTokenException;
import com.example.authentication.exception.InvalidAuthenticationTokenException;
import com.example.authentication.service.caching.CachingService;
import com.example.authentication.service.strategy.cache_key_strategy.CacheKeyFormattingStrategy;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.time.Duration;
import java.util.Date;

@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private static final Duration JWT_EXPIRATION_DURATION =  Duration.ofDays(1);
    private final AccountService accountService;
    private final JwtBuilder jwtBuilder;
    private final JwtParser jwtParser;
    private final CachingService cachingService;
    private final CacheKeyFormattingStrategy cacheKeyFormattingStrategy;

    public String createJwtFor(Account account) {
        String jwt = generateJwt(account);
        storeJwtOfAccountInCache(account, jwt);
        log.info("jwt for account {} was created", account.getId());
        return jwt;
    }

    public void invalidateJwtOf(Account account) {
        deleteJwtOfAccountFromCache(account);
        log.info("jwt for account {} was invalidated", account.getId());
    }

    public Account extractAccountFrom(String jwt) {
        String email = extractSubjectFrom(jwt);
        return accountService.findAccountByEmailInDatabase(email);
    }

    public boolean isAccountAuthorized(Account account, String jwt) {
        String jwtFromCache = getJwtOfAccountFromCache(account);
        return StringUtils.equals(jwt, jwtFromCache);
    }

    private String extractSubjectFrom(String jwt) {
        Claims claims = extractClaimsFrom(jwt);
        return claims.getSubject();
    }

    private Claims extractClaimsFrom(String jwt) {
        Jws<Claims> parsedJwt = parse(jwt);
        return parsedJwt.getBody();
    }

    private Jws<Claims> parse(String jwt) {
        try {
            return jwtParser.parseClaimsJws(jwt);
        } catch (ExpiredJwtException e) {
            throw new ExpiredAuthenticationTokenException(e);
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new InvalidAuthenticationTokenException(e);
        }
    }

    private String generateJwt(Account account) {
        long currentTimeInMillis = System.currentTimeMillis();
        return jwtBuilder
                .setSubject(account.getEmail())
                .setIssuedAt(new Date(currentTimeInMillis))
                .setExpiration(new Date(currentTimeInMillis + JWT_EXPIRATION_DURATION.toMillis()))
                .compact();
    }

    private void storeJwtOfAccountInCache(Account account, String jwt) {
        String cacheKey = cacheKeyFormattingStrategy.formatCacheKey(account);
        cachingService.storeInCache(cacheKey, jwt, JWT_EXPIRATION_DURATION);
    }

    private void deleteJwtOfAccountFromCache(Account account) {
        String cacheKey = cacheKeyFormattingStrategy.formatCacheKey(account);
        cachingService.deleteFromCache(cacheKey);
    }

    private String getJwtOfAccountFromCache(Account account) {
        String cacheKey = cacheKeyFormattingStrategy.formatCacheKey(account);
        return cachingService.getFromCache(cacheKey, String.class)
                .orElseThrow(ExpiredAuthenticationTokenException::new);
    }
}
