package com.irku.blog.service;


import com.irku.blog.entity.ContactRequest;

public interface EmailService {
    void sendContactEmail(ContactRequest request);
}
