package com.example.authentication.service.strategy.code_sending_strategy;

import com.example.authentication.config.service.strategy.code_sending_strategy.EmailConfirmationCodeSendingStrategyConfigProperties;
import com.example.authentication.entity.Account;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import static com.example.authentication.service.strategy.code_sending_strategy.EmailConfirmationCodeSendingStrategy.EmailMessagePart.MAIL_SUBJECT;
import static com.example.authentication.service.strategy.code_sending_strategy.EmailConfirmationCodeSendingStrategy.EmailMessagePart.MAIL_TEXT;
import static org.apache.commons.codec.CharEncoding.UTF_8;

@Component
@Qualifier("email")
@RequiredArgsConstructor
@Slf4j
public class EmailConfirmationCodeSendingStrategy implements ConfirmationCodeSendingStrategy {

    private final JavaMailSender mailSender;
    private final EmailConfirmationCodeSendingStrategyConfigProperties configProperties;

    @Override
    @SneakyThrows
    public void sendConfirmationCode(Account account, String confirmationCode) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, UTF_8);

        messageHelper.setSubject(MAIL_SUBJECT.messagePart);
        messageHelper.setFrom(configProperties.getUsername());
        messageHelper.setTo(account.getEmail());
        messageHelper.setText(MAIL_TEXT.getFormattedMessagePart(confirmationCode));

        mailSender.send(message);
        log.info("confirmation code was sent to {}", account.getEmail());
    }

    enum EmailMessagePart {
        MAIL_SUBJECT("Email confirmation"),
        MAIL_TEXT("Your confirmation code: %s");

        final String messagePart;

        EmailMessagePart(String messagePart) {
            this.messagePart = messagePart;
        }

        String getFormattedMessagePart(Object... params) {
            return messagePart.formatted(params);
        }
    }
}
