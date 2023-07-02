package com.example.authentication.config;

import com.example.authentication.entity.Account;
import com.example.authentication.repository.AccountRepository;
import com.example.authentication.service.AccountService;
import com.example.authentication.service.CacheService;
import com.example.authentication.service.JwtService;
import com.example.authentication.service.MessageGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static com.example.authentication.constants.CachePrefix.JWT_CACHE_PREFIX;

@Configuration
@RequiredArgsConstructor
@PropertySource({
        "classpath:${envTarget:errors}.properties"
})
public class AppConfig {

    private final AccountService accountService;
    private final JwtService jwtService;
    private final CacheService cacheService;

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        authenticationProvider.setUserDetailsService(userDetailsService());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public LogoutHandler logoutHandler() {
        return (request, response, authentication) -> {
            String jwt = jwtService.getJwtFromRequest(request);
            String email = jwtService.extractEmail(jwt);
            cacheService.deleteKey(JWT_CACHE_PREFIX.formatted(email));
        };
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return accountService::getAccountByEmail;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(8);
    }
}
