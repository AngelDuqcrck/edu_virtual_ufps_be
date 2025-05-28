package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.services;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Usuario;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos.ActasInfoDto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos.EmailDto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Proyecto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Sustentacion;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.intermedias.UsuarioProyecto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories.ProyectoRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories.UsuarioProyectoRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class EmailServiceSeguimiento {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private UsuarioProyectoRepository usuarioProyectoRepository;
    @Autowired
    private ProyectoRepository proyectoRepository;

    public boolean enviarCorreoRecordatorio(EmailDto emailDto) throws MessagingException, IOException {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true);

            helper.setTo(emailDto.getDestinatario());
            helper.setSubject(emailDto.getAsunto());

            ClassPathResource logoHeader = new ClassPathResource("static/images/topemail.png");
            ClassPathResource footerImage = new ClassPathResource("static/images/botemail.png");
            String asunto = emailDto.getAsunto();
            String cuerpo = emailDto.getCuerpo();
            String cuerpoFormateado = emailDto.getCuerpo().replace("\n", "<br>");

            String contenidoHtml = MessageFormat.format(
                    """
                            <!DOCTYPE html>
                            <html>
                            <head>
                              <meta charset="UTF-8">
                            </head>
                            <body style="font-size: 13px; font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                              <div style="max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd;">
                                <img src="cid:logoHeader" style="display: block; margin: 0 auto 20px; width: 100%; max-width: 600px;" alt="Header">
                                                
                                <p style="font-size: 20px;"><strong>{0}</strong></p>
                                                
                                <p style="font-size: 16px;">{1}.</p>
                                
                                <p style="font-size: 16px; font-weight: bold; color: #d9534f;">
                                  Este es un mensaje automático, por favor no responder.
                                </p>
               
                                <p style="font-weight: bold;">Atentamente,</p>
                                
                                <div style="display: flex; align-items: center; justify-content: space-between; width: 100%; margin-top: 20px;">
                                          <div style="width: 50%; text-align: center;">
                                            <img src="cid:footerImage" alt="EDUTICLAB Logo" style="max-width: 100%; height: auto;">
                                          </div>
                                        
                                          <div style="width: 50%; font-size: 13px; color: #333;">
                                            <p style="margin: 0;">
                                              Comité Organizador EduTIC LAB 2024 II<br>
                                              Facultad de Ingeniería<br>
                                              Programa de Maestría en TIC Aplicadas a la Educación<br>
                                              Unidad de Educación Virtual<br>
                                              Universidad Francisco de Paula Santander<br>
                                              San José de Cúcuta - Colombia
                                            </p>
                                          </div>
                                        </div>
                              </div>
                            </body>
                            </html>
                    """,asunto, cuerpoFormateado);

            helper.setText(contenidoHtml, true);
            helper.addInline("logoHeader", logoHeader);
            helper.addInline("footerImage", footerImage);

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

    public boolean enviarCorreoProgramacionSustentacion(Sustentacion sustentacion) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true);

            List<UsuarioProyecto> usuariosProyecto = usuarioProyectoRepository.findByIdProyecto(sustentacion.getProyecto().getId());
            Optional<Proyecto> proyecto = proyectoRepository.findById(sustentacion.getProyecto().getId());
            String estudianteCorreo = null;
            for (UsuarioProyecto up : usuariosProyecto) {
                String rol = up.getRol().getNombre().toLowerCase();
                Usuario usuario = up.getUsuario();
                switch (rol) {
                    case "estudiante":
                        estudianteCorreo = usuario.getEmail();
                        break;
                }
            }
            EmailDto emailDto = new EmailDto();
            emailDto.setDestinatario(estudianteCorreo);
            emailDto.setAsunto("PROGRAMACIÓN DE SUSTENTACIÓN");

            ActasInfoDto actasInfoDto = new ActasInfoDto();
            actasInfoDto.setTituloProyecto(proyecto.get().getTitulo());
            actasInfoDto.setFecha(sustentacion.getFecha().format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy")));
            actasInfoDto.setHora(sustentacion.getHora().toString() + " - " + sustentacion.getHoraFin().toString());
            actasInfoDto.setSala(sustentacion.getLugar());
            String descripcionSustentacion = sustentacion.getDescripcion();
            actasInfoDto.setDescripcionSustentacion(descripcionSustentacion != null ? descripcionSustentacion : "Sin descripcion");

            helper.setTo(emailDto.getDestinatario());
            helper.setSubject(emailDto.getAsunto());

            ClassPathResource logoHeader = new ClassPathResource("static/images/topemail.png");
            ClassPathResource footerImage = new ClassPathResource("static/images/botemail.png");
            String tituloProyecto = actasInfoDto.getTituloProyecto();
            String fechaSustentacion = actasInfoDto.getFecha(); //05 de Diciembre de 2024.
            String horaSustentacion = actasInfoDto.getHora(); //"03:30 - 04:10 PM"
            String lugarSustentacion = actasInfoDto.getSala();
            descripcionSustentacion = actasInfoDto.getDescripcionSustentacion();
            String infoActa = "006 de la sesión del 26 de noviembre de 2024";

            String contenidoHtml = MessageFormat.format(
                    """
                            <!DOCTYPE html>
                            <html>
                            <head>
                              <meta charset="UTF-8">
                            </head>
                            <body style="font-size: 13px; font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                              <div style="max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd;">
                                <img src="cid:logoHeader" style="display: block; margin: 0 auto 20px; width: 100%; max-width: 600px;" alt="Header">
                                                
                                <p><strong>Cordial saludo</strong></p>
                                                
                                <p>
                                    Me permito informarle que, según el Comité Curricular del Programa Académico Maestría en
                                    Tecnologías de Información y la Comunicación (TIC) Aplicadas a la Educación, se le ha
                                    programado la sustentación de su proyecto de tesis de maestría titulado
                                    <strong>"{0}"</strong> para el día {1}.
                                  </p>
                                
                                <p>Los detalles de la sustentación son los siguientes:</p>
                                <ul>
                                  <li>Modalidad: Virtual</li>
                                  <li>Hora: {2}</li>
                                  <li>{3}</li>
                                  <li>Enlace: {4}</li>
                                </ul>
                                
                                <p>Se requiere:</p>
                                <ol>
                                  <li>Tener acceso a internet estable</li>
                                  <li>Habilitar la cámara</li>
                                  <li>Habilitar el micrófono</li>
                                  <li>Ubicarse en un lugar silencioso</li>
                                  <li>Conectarse 10 minutos antes de la hora indicada</li>
                                </ol>
                                
                                <p style="font-size: 16px; font-weight: bold; color: #d9534f;">
                                  Nota: por favor, confirme su asistencia contestando este correo.
                                </p>
                                
                                <p>Le deseamos el mayor de los éxitos en su sustentación.</p>
                                
                                <p>Si tiene alguna pregunta o requiere asistencia adicional, no dude en
                                ponerse en contacto a través de uvirtual@ufps.edu.co</p>
                                                
                                <p style="font-weight: bold;">Atentamente,</p>
                                
                                <div style="display: flex; align-items: center; justify-content: space-between; width: 100%; margin-top: 20px;">
                                          <div style="width: 50%; text-align: center;">
                                            <img src="cid:footerImage" alt="EDUTICLAB Logo" style="max-width: 100%; height: auto;">
                                          </div>
                                        
                                          <div style="width: 50%; font-size: 13px; color: #333;">
                                            <p style="margin: 0;">
                                              Comité Organizador EduTIC LAB 2024 II<br>
                                              Facultad de Ingeniería<br>
                                              Programa de Maestría en TIC Aplicadas a la Educación<br>
                                              Unidad de Educación Virtual<br>
                                              Universidad Francisco de Paula Santander<br>
                                              San José de Cúcuta - Colombia
                                            </p>
                                          </div>
                                        </div>
                              </div>
                            </body>
                            </html>
                    """,tituloProyecto, fechaSustentacion, horaSustentacion, descripcionSustentacion, lugarSustentacion);

            helper.setText(contenidoHtml, true);
            helper.addInline("logoHeader", logoHeader);
            helper.addInline("footerImage", footerImage);

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
