package com.example.authentication.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import static org.apache.commons.codec.CharEncoding.UTF_8;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final MessageGenerator messageGenerator;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @SneakyThrows
    void sendMessageByEmail(String email, String activationCode) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, UTF_8);

        messageHelper.setSubject(messageGenerator.generateMessage("mail.subject"));
        messageHelper.setFrom(senderEmail);
        messageHelper.setTo(email);
        messageHelper.setText(messageGenerator.generateMessage("mail.text", activationCode));

        mailSender.send(message);
    }
}
