package com.irku.blog.controller;

import com.irku.blog.entity.ContactRequest;
import com.irku.blog.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8080", "http://localhost:3000", "http://localhost:5500"}, allowCredentials = "false")
public class ContactController {

    private final EmailService emailService;

    public ContactController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<Void> submit(@Valid @RequestBody ContactRequest request) {
        emailService.sendContactEmail(request);
        return ResponseEntity.accepted().build();
    }
}
