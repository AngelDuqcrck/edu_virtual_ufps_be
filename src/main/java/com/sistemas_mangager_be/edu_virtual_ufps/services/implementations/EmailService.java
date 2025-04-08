package com.sistemas_mangager_be.edu_virtual_ufps.services.implementations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;


@Service
@Transactional
public class EmailService {
    
    @Autowired
    JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String email;
    

      /**
     * Enviar correo electrónico
     * @param emailTo Dirección de correo electrónico del destinatario.
     * @param subject Asunto del correo.
     * @param mensaje Contenido del mensaje.
     * @param mensaje2 Contenido adicional del mensaje.
     * @throws RuntimeException si ocurre un error durante el envío del correo.
     */
    @Async
    public void sendEmail(String emailTo, String subject, String mensaje, String mensaje2) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(email);
            helper.setTo(emailTo);
            helper.setSubject(subject);
            helper.setText(correoMaterias(mensaje, mensaje2 ), true);

            javaMailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error enviando correo electrónico: " + e.getMessage());
        }
    }

    //Plantilla de Correo Electronico
    private String correoMaterias(String mensaje, String mensaje2 ) {
        return "";
    }
}
