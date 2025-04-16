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
                "            width: 350px;\n" +
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
                "             <img src=\"https://upload.wikimedia.org/wikipedia/commons/thumb/a/ad/Logo_de_UFPS.svg/640px-Logo_de_UFPS.svg.png\" alt=\"Logo de la Empresa\">\n" +
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
