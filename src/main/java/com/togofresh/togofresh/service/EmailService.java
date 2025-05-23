package com.togofresh.togofresh.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envía un email para restablecer la contraseña
     * @param to Email del destinatario
     * @param subject Asunto del email
     * @param text Contenido del email
     */
    public void enviarEmailResetContrasena(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error al enviar email: " + e.getMessage());
            // Puedes lanzar una excepción personalizada aquí si lo prefieres
        }
    }
}