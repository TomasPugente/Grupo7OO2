package com.example.unla.grupo7.services.implementation;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarCredenciales(String destinatario,
                                   String usuario,
                                   String password) {

        SimpleMailMessage mensaje = new SimpleMailMessage();

        mensaje.setTo(destinatario);
        mensaje.setSubject("Credenciales de acceso - Epicentro Gourmet");
        mensaje.setText(
            "Hola,\n\n" +
            "Se creó tu cuenta para acceder a Epicentro Gourmet.\n\n" +
            "Usuario: " + usuario + "\n" +
            "Contraseña temporal: " + password + "\n\n" +
            "Ingresá a la aplicación con estas credenciales y cambiá " +
            "la contraseña cuando esa funcionalidad esté disponible.\n\n" +
            "Saludos,\nEquipo de Epicentro Gourmet"
        );

        mailSender.send(mensaje);
    }
}