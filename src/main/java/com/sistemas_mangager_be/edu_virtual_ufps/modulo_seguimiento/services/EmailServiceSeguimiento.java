package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.services;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos.EmailDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class EmailServiceSeguimiento {
    @Autowired
    private JavaMailSender mailSender;

    public boolean enviarCorreo(EmailDto emailDto) throws MessagingException, IOException {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true);

            helper.setTo(emailDto.getDestinatario());
            helper.setSubject(emailDto.getAsunto());

            String contenidoHtml = "<!DOCTYPE html>" +
                    "<html>" +
                    "<head>" +
                    "  <meta charset='UTF-8'>" +
                    "  <style>" +
                    "    .container { font-family: Arial, sans-serif; padding: 20px; border: 1px solid #ddd; border-radius: 8px; }" +
                    "    .titulo { font-size: 20px; font-weight: bold; color: #2c3e50; }" +
                    "    .contenido { margin-top: 10px; font-size: 16px; color: #333; }" +
                    "    .footer { margin-top: 20px; font-size: 12px; color: #999; }" +
                    "  </style>" +
                    "</head>" +
                    "<body>" +
                    "  <div class='container'>" +
                    "    <div class='titulo'>" + emailDto.getAsunto() + "</div>" +
                    "    <div class='contenido'>" + emailDto.getCuerpo().replace("\n", "<br>") + "</div>" +
                    "    <div class='footer'>Este es un mensaje automático, por favor no responder.</div>" +
                    "  </div>" +
                    "</body>" +
                    "</html>";

            helper.setText(contenidoHtml, true);

            if (emailDto.getAdjuntos() != null) {
                for (MultipartFile archivo : emailDto.getAdjuntos()) {
                    if (!archivo.isEmpty() && archivo.getContentType().equals("application/pdf")) {
                        helper.addAttachment(archivo.getOriginalFilename(), archivo);
                    }
                }
            }

            mailSender.send(mensaje);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error enviando correo: " + e.getMessage(), e);
        }
    }
}
