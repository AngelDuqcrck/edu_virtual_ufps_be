package com.sistemas_mangager_be.edu_virtual_ufps.services.implementations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.CorreoResponse;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.MatriculaResponse;

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
     * 
     * @param emailTo  Dirección de correo electrónico del destinatario.
     * @param subject  Asunto del correo.
     * @param mensaje  Contenido del mensaje.
     * @param mensaje2 Contenido adicional del mensaje.
     * @throws RuntimeException si ocurre un error durante el envío del correo.
     */
    @Async
    public void sendEmail(String emailTo, String subject, CorreoResponse correoResponse) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(email);
            helper.setTo(emailTo);
            helper.setSubject(subject);
            helper.setText(correoMaterias(correoResponse), true);

            javaMailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error enviando correo electrónico: " + e.getMessage());
        }
    }

    /**
     * Enviar correo electrónico
     * 
     * @param emailTo  Dirección de correo electrónico del destinatario.
     * @param subject  Asunto del correo.
     * @param mensaje  Contenido del mensaje.
     * @param mensaje2 Contenido adicional del mensaje.
     * @throws RuntimeException si ocurre un error durante el envío del correo.
     */
    @Async
    public void sendEmailPassword(String emailTo, String subject, String mensaje, String mensaje2) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(email);
            helper.setTo(emailTo);
            helper.setSubject(subject);
            helper.setText(correoContrasena(mensaje, mensaje2), true);

            javaMailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error enviando correo electrónico: " + e.getMessage());
        }
    }

    private String correoContrasena(String mensaje, String mensaje2) {
        String logoBase64 = "";

        String html = "<!DOCTYPE html>\n" +
                "<html lang=\"es\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Email</title>\n" +
                "    <style>\n" +
                "        * { font-family: Tahoma; margin: 0; padding: 0; background-color: #ffffff; }\n" +
                "        .container { padding: 20px; background-color: #ffffff; }\n" +
                "        .imagen { text-align: center; margin-bottom: 7px; }\n" +
                "        .header img {\n" +
                "            width: 400px; /* Corregí el valor faltante (px) */\n" +
                "            margin-bottom: -40px;\n" +
                "        }\n" +
                "        .titulo { text-align: center; margin-bottom: 15px; color: #000000; }\n" +
                "        .header { border-bottom: 2px solid #BC0017; color: #000000; padding: 10px 0; text-align: center; padding-bottom: 20px; }\n"
                +
                "        .content { margin: 20px 0; text-align: center; color: #000000; }\n" +
                "        .content p { margin-left: 20px; }\n" +
                "        .footer { border-top: 2px solid #BC0017; color: #000000; text-align: center; padding: 10px 0; }\n"
                +
                "        button { margin-top: 20px; padding-left: 20px; padding-right: 20px; color: #EBEBEB; width: auto; height: auto; padding-top: 10px; padding-bottom: 10px; border: 3px solid #EBEBEB; border-radius: 30px; transition: all 0.2s; cursor: pointer; background: #BC0017; font-size: 1.3em; font-weight: 550; }\n"
                +
                "        button:hover { background: #BC0017; color: EBEBEB; font-size: 1.4em; }\n" +
                "        @media (max-width: 768px) { \n" +
                "            button { font-size: 1.3em; padding: 8px 18px; }\n" +
                "        }\n" +
                "        @media (max-width: 480px) {\n" +
                "            button { font-size: 1.15em; padding: 6px 16px; }\n" +
                "        }\n" +
                "        .logo-container {\n" +
                "            display: flex;\n" +
                "            justify-content: flex-start;\n" +
                "            align-items: center;\n" +
                "            margin-bottom: -15px;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <div class=\"logo-container\">\n" +
                "                <img src=\"data:image/png;base64," + logoBase64
                + "\" width=\"500\" alt=\"Logo-UFPS\" />\n" +
                "        </div>\n" +
                "            <h2>Sistema de Información Académica Virtual</h2>\n" +
                "            <h2 style=\"font-weight: lighter;\">Maestría en Tecnologías de Información y Comunicación (TIC) Aplicadas a la Educación</h2>\n"
                +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <div class=\"titulo\">\n" +
                "                <h2 style=\"font-weight: lighter;\">" + mensaje + "</h2>\n" +
                "                <a href=\"" + mensaje2 + "\" target=\"_blank\">\n" +
                "                    <button>Restablecer Contraseña</button>\n" +
                "                </a>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        <div class=\"footer\">\n" +
                "            <p>Este es un mensaje automático, <strong>por favor NO responder.</strong></p>\n" +
                "            <p>&copy; 2025 Todos los derechos reservados.</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";

        return html;
    }

    // Plantilla de Correo Electronico
    private String correoMaterias(CorreoResponse correoResponse) {
        String html = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Matriculas de materias</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            background-color: #f4f4f4;\n" +
                "            color: #333;\n" +
                "        }\n" +
                "        .container {\n" +
                "            width: 80%;\n" +
                "            margin: 0 auto;\n" +
                "            background-color: #fff;\n" +
                "            padding: 20px;\n" +
                "            border-radius: 5px;\n" +
                "            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\n" +
                "        }\n" +
                "        .header {\n" +
                "            text-align: center;\n" +
                "            border-bottom: 1px solid #e0e0e0;\n" +
                "            margin-bottom: 20px;\n" +
                "        }\n" +
                "        .header img {\n" +
                "            width: 250px;\n" +
                "            margin-bottom: -40px;\n" +
                "        }\n" +
                "        .content {\n" +
                "            margin-bottom: 20px;\n" +
                "        }\n" +
                "        .footer {\n" +
                "            text-align: center;\n" +
                "            font-size: 12px;\n" +
                "            color: #777;\n" +
                "        }\n" +
                "        /* Estilos personalizados para la tabla */\n" +
                "        .custom-table {\n" +
                "            width: 100%;\n" +
                "            border-collapse: collapse;\n" +
                "        }\n" +
                "        .custom-table thead {\n" +
                "            background-color: #aa1916;\n" +
                "            color: white;\n" +
                "        }\n" +
                "        .custom-table th, \n" +
                "        .custom-table td {\n" +
                "            padding: 12px 16px;\n" +
                "            text-align: left;\n" +
                "            border-bottom: 1px solid #e0e0e0;\n" +
                "        }\n" +
                "        .custom-table tbody tr:hover {\n" +
                "            background-color: #f5f5f5;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "             <img src=\"https://upload.wikimedia.org/wikipedia/commons/thumb/a/ad/Logo_de_UFPS.svg/640px-Logo_de_UFPS.svg.png\" alt=\"Logo de la Empresa\">\n"
                +
                "            <h1>Materias Matriculadas " + correoResponse.getSemestre() + "</h1>\n" +
                "            <h3>Maestría en Tecnologías de Información y Comunicación (TIC) Aplicadas a la Educación</h3>\n"
                +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <p>Estimado/a " + correoResponse.getNombreEstudiante() + ",</p>\n" +
                "            <p>Por la presente, le enviamos las materias matriculadas para el "
                + correoResponse.getSemestre() + ".</p>\n" +
                "        </div>\n" +
                "\n" +
                "        <div class=\"relative overflow-x-auto\">\n" +
                "          <table class=\"custom-table\">\n" +
                "              <thead>\n" +
                "                  <tr>\n" +
                "                      <th scope=\"col\" class=\"px-6 py-3\">\n" +
                "                          Materia\n" +
                "                      </th>\n" +
                "                      <th scope=\"col\" class=\"px-6 py-3\">\n" +
                "                          Nombre\n" +
                "                      </th>\n" +
                "                      <th scope=\"col\" class=\"px-6 py-3\">\n" +
                "                          Grupo\n" +
                "                      </th>\n" +
                "                      <th scope=\"col\" class=\"px-6 py-3\">\n" +
                "                          Créditos\n" +
                "                      </th>\n" +
                "                      <th scope=\"col\" class=\"px-6 py-3\">\n" +
                "                          Semestre\n" +
                "                      </th>\n" +
                "                  </tr>\n" +
                "              </thead>\n" +
                "              <tbody>\n";

        // Generar filas de la tabla con las matrículas
        for (MatriculaResponse matricula : correoResponse.getMatriculas()) {
            html += "                  <tr>\n" +
                    "                      <td class=\"px-6 py-4 font-medium text-gray-900 whitespace-nowrap\">\n" +
                    "                          " + matricula.getCodigoMateria() + "\n" +
                    "                      </td>\n" +
                    "                      <td class=\"px-6 py-4\">\n" +
                    "                          " + matricula.getNombreMateria() + "\n" +
                    "                      </td>\n" +
                    "                      <td class=\"px-6 py-4\">\n" +
                    "                          " + matricula.getGrupoNombre() + "\n" +
                    "                      </td>\n" +
                    "                      <td class=\"px-6 py-4\">\n" +
                    "                          " + matricula.getCreditos()
                    + "\n" +
                    "                      </td>\n" +
                    "                      <td class=\"px-6 py-4\">\n" +
                    "                          " + matricula.getSemestreMateria() + "\n" +
                    "                      </td>\n" +
                    "                  </tr>\n";
        }

        html += "              </tbody>\n" +
                "          </table>\n" +
                "        </div>\n" +
                "        \n" +
                "        <br>\n" +
                "        <div class=\"footer\">\n" +
                "            <p>&copy; "
                + " 2025 Unidad de Educación Virtual UFPS. Todos los derechos reservados.</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";

        return html;
    }
}
