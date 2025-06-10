package com.heynet.spring_template.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

  private final JavaMailSender mailSender;

  @Value("${spring.mail.username}")
  private String fromEmail;

  @Value("${app.frontend.url:http://localhost:3000}")
  private String frontendUrl;

  public void sendPasswordResetEmail(String toEmail, String resetToken) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(fromEmail);
    message.setTo(toEmail);
    message.setSubject("Reset de Senha - Spring Template");

    String resetLink = frontendUrl + "/reset-password?token=" + resetToken;
    String emailBody =
        "Olá,\n\n"
            + "Você solicitou um reset de senha para sua conta.\n\n"
            + "Clique no link abaixo para redefinir sua senha:\n"
            + resetLink
            + "\n\n"
            + "Este link expira em 30 minutos.\n\n"
            + "Se você não solicitou este reset, ignore este email.\n\n"
            + "Atenciosamente,\n"
            + "Equipe Spring Template";

    message.setText(emailBody);
    mailSender.send(message);
  }
}
