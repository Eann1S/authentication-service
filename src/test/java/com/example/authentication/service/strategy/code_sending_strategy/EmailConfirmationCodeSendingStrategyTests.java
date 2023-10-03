package com.example.authentication.service.strategy.code_sending_strategy;

import com.example.authentication.config.service.strategy.code_sending_strategy.EmailConfirmationCodeSendingStrategyConfigProperties;
import com.example.authentication.entity.Account;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.SneakyThrows;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class EmailConfirmationCodeSendingStrategyTests {

    @Mock
    private JavaMailSender mailSender;
    @Mock
    private MimeMessage mimeMessage;
    @Mock
    private EmailConfirmationCodeSendingStrategyConfigProperties configProperties;
    private EmailConfirmationCodeSendingStrategy emailSendConfirmationCodeStrategy;

    @BeforeEach
    void setUp() {
        emailSendConfirmationCodeStrategy = new EmailConfirmationCodeSendingStrategy(mailSender, configProperties);
    }

    @ParameterizedTest
    @InstancioSource
    @SneakyThrows
    void shouldSendConfirmationCodeByEmail(Account account, String emailFrom, String confirmationCode) {
        when(configProperties.getUsername())
                .thenReturn(emailFrom);
        when(mailSender.createMimeMessage())
                .thenReturn(mimeMessage);

        emailSendConfirmationCodeStrategy.sendConfirmationCode(account, confirmationCode);

        verify(mimeMessage).setFrom(new InternetAddress(emailFrom));
        verify(mimeMessage).setRecipient(any(), eq(new InternetAddress(account.getEmail())));
        verify(mimeMessage).setText(contains(confirmationCode), any());
    }
}