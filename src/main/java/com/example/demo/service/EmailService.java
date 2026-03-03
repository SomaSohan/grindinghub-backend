package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Welcome to GrindingHub! Your Verification Code");

        String body = "Hello from GrindingHub! 👋\n\n"
                + "Thank you for registering your factory with us. We're excited to have you join our network of premier grinding services.\n\n"
                + "To complete your registration and verify your business, please use the following One-Time Password (OTP):\n\n"
                + "🔐 " + otp + "\n\n"
                + "This code is valid for the next 5 minutes. Please do not share this code with anyone.\n\n"
                + "Best regards,\n"
                + "The GrindingHub Team\n"
                + "Contact: admin@grindhub.com";

        message.setText(body);
        mailSender.send(message);
    }
}