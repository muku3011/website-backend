package com.irku.blog.controller;

import com.irku.blog.entity.ContactRequest;
import com.irku.blog.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contact")
@Tag(name = "Contact", description = "Send contact requests")
public class ContactController {

    @Autowired
    private EmailService emailService;

    @Operation(summary = "Submit a contact request")
    @PostMapping
    public ResponseEntity<Void> submit(@Valid @RequestBody ContactRequest request) {
        emailService.sendContactEmail(request);
        return ResponseEntity.accepted().build();
    }
}