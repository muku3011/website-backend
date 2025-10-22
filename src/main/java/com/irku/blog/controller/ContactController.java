package com.irku.blog.controller;

import com.irku.blog.entity.ContactRequest;
import com.irku.blog.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contact")
@Tag(name = "Contact", description = "Send contact requests")
public class ContactController {

    private final EmailService emailService;

    public ContactController(EmailService emailService) {
        this.emailService = emailService;
    }

    @Operation(summary = "Submit a contact request")
    @PostMapping
    public ResponseEntity<Void> submit(@Valid @RequestBody ContactRequest request) {
        emailService.sendContactEmail(request);
        return ResponseEntity.accepted().build();
    }
}