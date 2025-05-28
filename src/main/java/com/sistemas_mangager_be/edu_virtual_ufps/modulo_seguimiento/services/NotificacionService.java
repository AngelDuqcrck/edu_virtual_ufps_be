package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.services;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Usuario;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos.EmailDto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Coloquio;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Sustentacion;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories.ColoquioEstudianteRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories.ColoquioRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories.SustentacionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class NotificacionService {
    
    private final ColoquioEstudianteRepository coloquioEstudianteRepository;
    private final SustentacionRepository sustentacionRepository;
    private final ColoquioRepository coloquioRepository;
    private final EmailServiceSeguimiento emailService;

    public NotificacionService(SustentacionRepository sustentacionRepository, ColoquioRepository coloquioRepository, EmailServiceSeguimiento emailService,
                               ColoquioEstudianteRepository coloquioEstudianteRepository) {
        this.sustentacionRepository = sustentacionRepository;
        this.coloquioRepository = coloquioRepository;
        this.emailService = emailService;
        this.coloquioEstudianteRepository = coloquioEstudianteRepository;
    }

    @Scheduled(cron = "0 0 6 * * *")
    public void enviarNotificacionesDeActividades() {
        LocalDate hoy = LocalDate.now();
        LocalDate fechaLimite = hoy.plusDays(3);
        LocalDate fechaPasada = hoy.minusDays(1);

        try {
            List<Sustentacion> sustentaciones = sustentacionRepository.findByFecha(fechaLimite);
            for (Sustentacion s : sustentaciones) {
                if (s.getSustentacionRealizada() == null || !s.getSustentacionRealizada()) {
                    List<Usuario> estudiantes = sustentacionRepository.findEstudiantesBySustentacionId(s.getId());
                    for (Usuario usuario : estudiantes) {
                        String correo = usuario.getEmail();
                        String asunto = "Recordatorio de sustentación";
                        String cuerpo = "Tienes una sustentación el día " + s.getFecha() + " a las " + s.getHora() +
                                "\nLugar: " + s.getLugar() + "\nDescripción: " + s.getDescripcion();

                        EmailDto email = new EmailDto();
                        email.setDestinatario(correo);
                        email.setAsunto(asunto);
                        email.setCuerpo(cuerpo);

                        emailService.enviarCorreoRecordatorio(email);
                    }
                }
            }

            List<Coloquio> coloquios = coloquioRepository.findByFecha(fechaLimite);
            for (Coloquio c : coloquios) {
                List<Usuario> usuarios = coloquioRepository.findUsuariosByColoquioId(c.getId());
                for (Usuario usuario : usuarios) {
                    boolean noAsistio = !coloquioEstudianteRepository.existsByColoquioIdAndIdEstudiante(c.getId(), usuario.getId());
                    if (noAsistio) {
                        String correo = usuario.getEmail();
                        String asunto = "Recordatorio de coloquio";
                        String cuerpo = "Tienes un coloquio el día " + c.getFecha() + " a las " + c.getHora() +
                                "\nLugar: " + c.getLugar() + "\nDescripción: " + c.getDescripcion();

                        EmailDto email = new EmailDto();
                        email.setDestinatario(correo);
                        email.setAsunto(asunto);
                        email.setCuerpo(cuerpo);

                        emailService.enviarCorreoRecordatorio(email);
                    }
                }
            }

            List<Sustentacion> sustentacionesPasadas = sustentacionRepository.findByFecha(fechaPasada);
            for (Sustentacion s : sustentacionesPasadas) {
                if (s.getSustentacionRealizada() == null || !s.getSustentacionRealizada()) {
                    List<Usuario> estudiantes = sustentacionRepository.findEstudiantesBySustentacionId(s.getId());
                    for (Usuario usuario : estudiantes) {
                        String correo = usuario.getEmail();
                        String asunto = "Seguimiento de sustentación";
                        String cuerpo = "Ayer tuviste una sustentación el día " + s.getFecha() + " a las " + s.getHora() +
                                "\nLugar: " + s.getLugar() + "\nDescripción: " + s.getDescripcion();

                        EmailDto email = new EmailDto();
                        email.setDestinatario(correo);
                        email.setAsunto(asunto);
                        email.setCuerpo(cuerpo);

                        emailService.enviarCorreoRecordatorio(email);
                    }
                }
            }

            List<Coloquio> coloquiosPasados = coloquioRepository.findByFecha(fechaPasada);
            for (Coloquio c : coloquiosPasados) {
                List<Usuario> usuarios = coloquioRepository.findUsuariosByColoquioId(c.getId());
                for (Usuario usuario : usuarios) {
                    boolean noAsistio = !coloquioEstudianteRepository.existsByColoquioIdAndIdEstudiante(c.getId(), usuario.getId());
                    if (noAsistio) {
                        String correo = usuario.getEmail();
                        String asunto = "Seguimiento de coloquio";
                        String cuerpo = "Ayer tuviste un coloquio el día " + c.getFecha() + " a las " + c.getHora() +
                                "\nLugar: " + c.getLugar() + "\nDescripción: " + c.getDescripcion();

                        EmailDto email = new EmailDto();
                        email.setDestinatario(correo);
                        email.setAsunto(asunto);
                        email.setCuerpo(cuerpo);

                        emailService.enviarCorreoRecordatorio(email);
                    }
                }
            }
        } catch (Exception e) {
            log.error("❌ Error inesperado al enviar notificaciones: {}", e.getMessage());
        }
    }

}
