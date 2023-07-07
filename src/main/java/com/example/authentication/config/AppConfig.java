package com.example.authentication.config;

import com.example.authentication.repository.AccountRepository;
import com.example.authentication.service.MessageGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@PropertySource({
        "classpath:${envTarget:errors}.properties",
        "classpath:${envTarget:messages}.properties"
})
public class AppConfig {

    private final AccountRepository accountRepository;
    private final MessageGenerator messageGenerator;

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        authenticationProvider.setUserDetailsService(userDetailsService());
        return authenticationProvider;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> accountRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(
                        messageGenerator.generateMessage("error.entity.not_found", email)
                ));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(8);
    }
}
