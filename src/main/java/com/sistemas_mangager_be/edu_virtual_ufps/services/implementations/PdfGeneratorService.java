package com.sistemas_mangager_be.edu_virtual_ufps.services.implementations;

import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.CertificadoResponse;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Service
public class PdfGeneratorService {

    public byte[] generateCertificadoPdf(CertificadoResponse certificado) {
        String htmlContent = generateCertificadoHtml(certificado);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();

            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF del certificado", e);
        }
    }

    private String generateCertificadoHtml(CertificadoResponse certificado) {
        // Formatear fechas
        String fechaCertificado = formatDate(certificado.getFechaCertificado());

        // Extraer día, mes y año de la fecha del certificado
        String[] dateParts = safeSplitDate(fechaCertificado);
        String dia = dateParts[0];
        String mes = dateParts[1];
        String anio = dateParts[2];

        String logoBase64 = "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";
        String bordeBase64 = "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";

        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"
                +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                "<head>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n" +
                "    <title>Constancia de Contraprestación</title>\n" +
                "    <style type=\"text/css\">\n" +
                "        body {\n" +
                "            font-family: 'Trebuchet MS', sans-serif;\n" +
                "            color: #363744;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            line-height: 1.25;\n" +
                "        }\n" +
                "        \n" +
                "        .container {\n" +
                "            max-width: 800px;\n" +
                "            margin: 0 auto;\n" +
                "            padding: 20px;\n" +
                "            font-family: 'Trebuchet MS', sans-serif;\n" +
                "        }\n" +
                "        \n" +
                "        .logo-container {\n" +
                "            display: flex;\n" +
                "            justify-content: flex-start;\n" +
                "            align-items: center;\n" +
                "            margin-bottom: -15px;\n" +
                "        }\n" +
                "        \n" +
                "        .logo {\n" +
                "            height: 120px;\n" +
                "        }\n" +
                "        \n" +
                "        .separator {\n" +
                "            border-top: 1px solid #a09d9d;\n" +
                "            margin: 0px 0;\n" +
                "        }\n" +
                "        \n" +
                "        .title {\n" +
                "            text-align: center;\n" +
                "            font-size: 18px;\n" +
                "            font-weight: 900;\n" +
                "            margin-bottom: 20px;\n" +
                "            margin-top: 30px;\n" +
                "            font-family: 'Trebuchet MS', sans-serif;\n" +
                "        }\n" +
                "        \n" +
                "        .content {\n" +
                "            line-height: 1.5;\n" +
                "            margin: 5px 0;\n" +
                "            text-align: justify;\n" +
                "            font-size: 16px;\n" +
                "            font-family: 'Trebuchet MS', sans-serif;\n" +
                "        }\n" +
                "        \n" +
                "        .footer {\n" +
                "            font-family: 'Trebuchet MS', sans-serif;\n" +
                "            text-align: center;\n" +
                "            font-size: 15px;\n" +
                "            color: #363744;\n" +
                "            margin-top: 20 px;\n" +
                "        }\n" +
                "        \n" +
                "        .footer-content {\n" +
                "            margin-top: 0px;\n" +
                "        }\n" +
                "        \n" +
                "        .footer p {\n" +
                "            margin: 4px;\n" +
                "        }\n" +
                "        \n" +
                "        .certificate-number {\n" +
                "            text-align: right;\n" +
                "            font-size: 14px;\n" +
                "            color: #666;\n" +
                "            margin-bottom: 20px;\n" +
                "            font-family: 'Trebuchet MS', sans-serif;\n" +
                "        }\n" +
                "        \n" +
                "        .highlight {\n" +
                "            font-weight: bold;\n" +
                "        }\n" +
                "        \n" +
                "        .activities {\n" +
                "            font-style: italic;\n" +
                "            text-align: center;\n" +
                "            margin: 20px 0;\n" +
                "            padding: 10px;\n" +
                "            background-color: #f8f9fa;\n" +
                "            font-family: 'Trebuchet MS', sans-serif;\n" +
                "        }\n" +
                "        \n" +
                "        .firma {\n" +
                "            margin-top: 85px;\n" +
                "            text-align: start;\n" +
                "            border-top: 1px solid #000;\n" +
                "            width: 60%;\n" +
                "            padding-top: 5px;\n" +
                "            margin-bottom: 25px;\n" +
                "            font-family: 'Trebuchet MS', sans-serif;\n" +
                "        }\n" +
                "        \n" +
                "        .cargo {\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            font-weight: normal;\n" +
                "        }\n" +
                "        \n" +
                "        .titulo-principal {\n" +
                "            text-align: center;\n" +
                "            font-weight: bold;\n" +
                "            font-size: 18px;\n" +
                "            margin: 30px 0 15px 0;\n" +
                "            font-family: 'Trebuchet MS', sans-serif;\n" +
                "        }\n" +
                "        \n" +
                "        .subtitulo-principal {\n" +
                "            text-align: center;\n" +
                "            font-weight: bold;\n" +
                "            font-size: 16px;\n" +
                "            margin: 0 0 30px 0;\n" +
                "            font-family: 'Trebuchet MS', sans-serif;\n" +
                "        }\n" +
                "        \n" +
                "        .hace-constar {\n" +
                "            text-align: center;\n" +
                "            font-weight: bold;\n" +
                "            margin: 30px 0;\n" +
                "            font-family: 'Trebuchet MS', sans-serif;\n" +
                "        }\n" +
                "        \n" +
                "        .parrafo {\n" +
                "            text-align: justify;\n" +
                "            line-height: 1.5;\n" +
                "            margin: 15px 0;\n" +
                "            font-family: 'Trebuchet MS', sans-serif;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"logo-container\">\n" +
                "                <img src=\"data:image/png;base64," + logoBase64
                + "\" width=\"500\" alt=\"Logo-UFPS\" />\n" +
                "        </div>\n" +
                "        \n" +
                "        <div class=\"separator\"></div>\n" +
                "        \n" +
                "        \n" +
                "        <div class=\"title\">\n" +
                "            CONSTANCIA DE CUMPLIMIENTO CONTRAPRESTACIÓN DE SERVICIOS POSGRADOS\n" +
                "        </div>\n" +
                "        \n" +
                "        <div class=\"subtitulo-principal\">\n" +
                "            EL SUSCRITO DIRECTOR DE LA MAESTRÍA EN TECNOLOGÍAS DE LA INFORMACIÓN Y LA COMUNICACIÓN (TIC) APLICADAS A LA\n"
                +
                "            EDUCACIÓN DE LA UNIVERSIDAD FRANCISCO DE PAULA SANTANDER\n" +
                "        </div>\n" +
                "        \n" +
                "        <div class=\"hace-constar\">\n" +
                "            HACE CONSTAR:\n" +
                "        </div>\n" +
                "        \n" +
                "        <div class=\"content\">\n" +
                "            <p class=\"parrafo\">\n" +
                "                Que, el estudiante <span class=\"highlight\">" + certificado.getNombreCompleto()
                + "</span>,\n" +
                "                con cédula de ciudadanía No. <span class=\"highlight\">" + certificado.getCedula()
                + "</span>\n" +
                "                y código No. <span class=\"highlight\">" + certificado.getCodigoEstudiante()
                + "</span>, apoyó al Programa\n" +
                "                <span class=\"highlight\">" + certificado.getPrograma() + "</span> realizando\n" +
                "                <span class=\"highlight\">" + certificado.getActividades()
                + "</span>,  estas actividades se llevaron \n" +
                "                a cabo durante el periodo académico <span class=\"highlight\">"
                + certificado.getSemestre() + "</span>.\n" +
                "            </p>\n" +
                "            \n" +
                "            <p class=\"parrafo\">\n" +
                "                La participación del estudiante fue fundamental para diseñar estrategias de mejora\n" +
                "                en los procesos de comunicación y atención a los estudiantes. En este sentido, la\n" +
                "                presente certificación tiene como objetivo respaldar la participación de la\n" +
                "                estudiante en las actividades mencionadas y confirmar los beneficios otorgados según <span\n"
                +
                "                    class=\"highlight\">Acuerdo 020 del 25 de junio del 2020</span>, por <span class=\"highlight\">"
                +
                "                   " + certificado.getCohorteNombre() + "</span>.\n" +
                "            </p>\n" +
                "            \n" +
                "            <p class=\"parrafo\">\n" +
                "                En constancia se firma a los <span class=\"highlight\">" + dia
                + "</span> días del mes de <span\n" +
                "                    class=\"highlight\">" + mes + "</span> de <span class=\"highlight\">" + anio
                + "</span>.\n" +
                "            </p>\n" +
                "        </div>\n" +
                "        \n" +
                "        <div class=\"firma\">\n" +
                "            <p><strong>PhD. Matías Herrera Cáceres</strong></p>\n" +
                "            <p class=\"cargo\">Director de la Maestría en TIC Aplicadas a la Educación.</p>\n" +
                "            <p class=\"cargo\">UFPS - Cúcuta</p>\n" +
                "        </div>\n" +
                "        \n" +
                "        <div class=\"footer\">\n" +
                "            <div class=\"footer-content\">\n" +
                "                <img src=\"data:image/png;base64," + bordeBase64
                + "\" width=\"800\" alt=\"borde-UFPS\" />\n" +
                "                <p>Avenida Gran Colombia No. 12E-96 Barrio Colsag</p>\n" +
                "                <p>Telefono (057) (7) 5776655 - www.ufps.edu.co</p>\n" +
                "                <p>ugad@ufps.edu.co San José de Cúcuta - Colombia</p>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";

    }

    private String formatDate(Date date) {
        if (date == null) {
            return "fecha no disponible";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
        return sdf.format(date);
    }

    private String[] safeSplitDate(String formattedDate) {
        // Formato esperado: "15 de marzo de 2023"
        String[] parts = formattedDate.split(" de ");

        if (parts.length != 3) {
            // Si el formato no es el esperado, devolver valores por defecto
            return new String[] { "--", "--", "----" };
        }

        // Eliminar espacios en blanco alrededor
        String dia = parts[0].trim();
        String mes = parts[1].trim();

        // El año podría tener más partes si hay espacios adicionales
        String[] yearParts = parts[2].trim().split(" ");
        String anio = yearParts.length > 0 ? yearParts[0] : "----";

        return new String[] { dia, mes, anio };
    }
}