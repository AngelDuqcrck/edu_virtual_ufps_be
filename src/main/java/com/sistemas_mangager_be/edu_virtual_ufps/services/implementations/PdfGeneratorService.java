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

            // Configuración para manejar imágenes embebidas
            renderer.getSharedContext().setReplacedElementFactory(
                    new ImageReplacedElementFactory(renderer.getSharedContext().getReplacedElementFactory()));

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
        String fechaInicio = formatDate(certificado.getFechaInicio());
        String fechaFin = formatDate(certificado.getFechaFin());
        String fechaCertificado = formatDate(certificado.getFechaCertificado());

        // Extraer día, mes y año de la fecha del certificado
        String[] dateParts = safeSplitDate(fechaCertificado);
        String dia = dateParts[0];
        String mes = dateParts[1];
        String anio = dateParts[2];

        String logoBase64 = "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";
        String bordeBase64 = "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";

        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
            "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
            "<head>\n" +
            "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n" +
            "    <title>Certificado de Contraprestación</title>\n" +
            "    <style type=\"text/css\">\n" +
            "        body {\n" +
            "            font-family: 'Lato', sans-serif;\n" +
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
            "        }\n" +
            "        \n" +
            "        .header {\n" +
            "            text-align: center;\n" +
            "            margin-bottom: 7px;\n" +
            "            padding-bottom: 5px;\n" +
            "        }\n" +
            "\n" +
            "        .header-separator {\n" +
            "            font-family: \"Lato\", sans-serif;\n" +
            "            text-align: center;\n" +
            "            font-size: 15px;\n" +
            "            color: #363744;\n" +
            "            margin-top: 5px;\n" +
            "            padding-top: 5px;\n" +
            "        }\n" +
            "\n" +
            "        .header svg {\n" +
            "            width: 400px; \n" +
            "        }\n" +
            "        \n" +
            "        .title {\n" +
            "            text-align: center;\n" +
            "            font-size: 24px;\n" +
            "            font-weight: 900;\n" +
            "            color: #BC2F00;\n" +
            "            margin: 20px 0;\n" +
            "        }\n" +
            "        \n" +
            "        .content {\n" +
            "            line-height: 1.4;\n" +
            "            margin: 15px 0;\n" +
            "            text-align: justify;\n" +
            "            font-size: 16px;\n" +
            "        }\n" +
            "        \n" +
            "        .footer {\n" +
            "            font-family: \"Lato\", sans-serif;\n" +
            "            text-align: center;\n" +
            "            font-size: 15px;\n" +
            "            color: #363744;\n" +
            "            margin-top: -15px;\n" +
            "            padding-top: 20px;\n" +
            "        }\n" +
            "        \n" +
            "        .footer-content {\n" +
            "            margin-top: 15px;\n" +
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
            "        }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <div class=\"container\">\n" +
            "        <div class=\"certificate-number\">\n" +
            "            Certificado N°: " + certificado.getId() + "\n" +
            "        </div>\n" +
            "        \n" +
            "        <div class=\"header\">\n" +
            "            <header>\n" +
            "                <img src=\"data:image/png;base64," + logoBase64 + "\" width=\"400\" alt=\"logo-UFPS\" />\n" +
            "            </header>\n" +
            "        </div>\n" +
            "        <div class=\"header-separator\">\n" +
            "                <img src=\"data:image/png;base64," + bordeBase64 + "\" width=\"800\" alt=\"borde-UFPS\" />\n" +
            "        </div>\n" +
            "        \n" +
            "        <div class=\"title\">\n" +
            "            CERTIFICADO DE APROBACIÓN Y <br />\n" +
            "            TERMINACIÓN DE CONTRAPRESTACIONES\n" +
            "        </div>\n" +
            "        \n" +
            "        <div class=\"content\">\n" +
            "            <p>La Universidad Francisco de Paula Santander, en uso de sus facultades legales y reglamentarias, certifica que el(la) estudiante \n" +
            "            <span class=\"highlight\">" + certificado.getNombreCompleto() + "</span>, \n" +
            "            identificado(a) con cédula de ciudadanía No. <span class=\"highlight\">" + certificado.getCedula() + "</span>, \n" +
            "            matriculado(a) en el programa académico de <span class=\"highlight\">" + certificado.getPrograma() + "</span>, \n" +
            "            perteneciente a la cohorte <span class=\"highlight\">" + certificado.getCohorteNombre() + "</span>, ha cumplido satisfactoriamente \n" +
            "            con las contraprestaciones correspondientes al periodo académico \n" +
            "            <span class=\"highlight\">" + certificado.getSemestre() + "</span>.</p>\n" +
            "            \n" +
            "            <p>Durante este periodo, el(la) estudiante desarrolló las siguientes actividades:</p>\n" +
            "            \n" +
            "            <div class=\"activities\">" + certificado.getActividades() + "</div>\n" +
            "            \n" +
            "            <p>El periodo de ejecución de las contraprestaciones fue desde el <span class=\"highlight\">" + fechaInicio + "</span> \n" +
            "            hasta el <span class=\"highlight\">" + fechaFin + "</span>, habiendo sido aprobadas según consta en los registros \n" +
            "            oficiales del programa académico.</p>\n" +
            "            \n" +
            "            <p>Este certificado se expide en la ciudad de San José de Cúcuta, \n" +
            "            a los <span class=\"highlight\">" + dia + "</span> días del mes de <span class=\"highlight\">" + mes + "</span> del <span class=\"highlight\">" + anio + "</span>.</p>\n" +
            "        </div>\n" +
            "        \n" +
            "        <div class=\"footer\">\n" +
            "                <img src=\"data:image/png;base64," + bordeBase64 + "\" width=\"800\" alt=\"borde-UFPS\" />\n" +
            "            <div class=\"footer-content\">\n" +
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