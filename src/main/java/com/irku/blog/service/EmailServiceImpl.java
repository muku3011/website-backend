package com.irku.blog.service;

import com.irku.blog.entity.ContactRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${contact.to.email}")
    private String toEmail;

    @Value("${spring.application.name:Website}")
    private String appName;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendContactEmail(ContactRequest request) {
        String subject = "[Contact] " + request.getSubject();
        String body = buildBody(request);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        message.setReplyTo(request.getEmail());
        // Optional: set from if your SMTP requires/permits it
        // message.setFrom(toEmail);

        mailSender.send(message);
    }

    private String buildBody(ContactRequest r) {
        return "You received a new contact form submission from " + appName + "\n\n" +
                "Name: " + r.getFirstName() + " " + r.getLastName() + "\n" +
                "Email: " + r.getEmail() + "\n" +
                "Subject: " + r.getSubject() + "\n" +
                "Subscribed to newsletter: " + (r.isNewsletter() ? "Yes" : "No") + "\n\n" +
                "Message:\n" +
                r.getMessage() + "\n";
    }
}
