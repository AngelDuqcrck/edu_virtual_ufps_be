package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.services;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Usuario;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos.CriterioEvaluacionDto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos.SustentacionDto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos.SustentacionEvaluadorDto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.CriterioEvaluacion;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Proyecto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Sustentacion;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.enums.TipoSustentacion;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.intermedias.SustentacionEvaluador;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.mappers.CriterioEvaluacionMapper;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.mappers.SustentacionMapper;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories.*;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.UsuarioRepository;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SustentacionService {

    private final SustentacionRepository sustentacionRepository;
    private final SustentacionMapper sustentacionMapper;
    private final SustentacionEvaluadorRepository sustentacionEvaluadorRepository;
    private final ProyectoRepository proyectoRepository;
    private final UsuarioProyectoRepository usuarioProyectoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CriterioEvaluacionRepository criterioEvaluacionRepository;
    private final CriterioEvaluacionMapper criterioEvaluacionMapper;
    private final EmailServiceSeguimiento emailServiceSeguimiento;


    @Autowired
    public SustentacionService(SustentacionRepository sustentacionRepository, SustentacionMapper sustentacionMapper,
                               SustentacionEvaluadorRepository sustentacionEvaluadorRepository, ProyectoRepository proyectoRepository,
                               UsuarioProyectoRepository usuarioProyectoRepository, UsuarioRepository usuarioRepository, CriterioEvaluacionRepository criterioEvaluacionRepository, CriterioEvaluacionMapper criterioEvaluacionMapper, EmailServiceSeguimiento emailServiceSeguimiento) {
        this.sustentacionRepository = sustentacionRepository;
        this.sustentacionMapper = sustentacionMapper;
        this.sustentacionEvaluadorRepository = sustentacionEvaluadorRepository;
        this.proyectoRepository = proyectoRepository;
        this.usuarioProyectoRepository = usuarioProyectoRepository;
        this.usuarioRepository = usuarioRepository;
        this.criterioEvaluacionRepository = criterioEvaluacionRepository;
        this.criterioEvaluacionMapper = criterioEvaluacionMapper;
        this.emailServiceSeguimiento = emailServiceSeguimiento;
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
    public SustentacionDto crearSustentacion(SustentacionDto sustentacionDto) {
        Integer idProyecto = sustentacionDto.getIdProyecto();
        TipoSustentacion tipo;

        if (!proyectoRepository.existsById(sustentacionDto.getIdProyecto())) {
            throw new EntityNotFoundException("Proyecto no encontrado");
        }

        try {
            tipo = TipoSustentacion.valueOf(sustentacionDto.getTipoSustentacion().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Tipo de sustentación inválido. Debe ser 'TESIS' o 'ANTEPROYECTO'");
        }

        boolean yaExiste = sustentacionRepository.existsByProyectoIdAndTipoSustentacion(idProyecto, tipo);
        if (yaExiste) {
            throw new RuntimeException("Ya existe una sustentación de tipo '" + tipo + "' para este proyecto.");
        }

        Sustentacion sustentacion = sustentacionMapper.toEntity(sustentacionDto);

        Sustentacion savedSustentacion = sustentacionRepository.save(sustentacion);

        if (savedSustentacion.getTipoSustentacion().equals(TipoSustentacion.TESIS)){
            emailServiceSeguimiento.enviarCorreoProgramacionSustentacion(savedSustentacion);
        }

        return sustentacionMapper.toDto(savedSustentacion);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ROLE_ESTUDIANTE') or hasAuthority('ROLE_DOCENTE') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
    public SustentacionDto obtenerSustentacion(Integer idProyecto, TipoSustentacion tipoSustentacion) {
        Sustentacion sustentacion = sustentacionRepository.findByProyectoIdAndOptionalTipoSustentacion(idProyecto, tipoSustentacion)
                .orElseThrow(() -> new EntityNotFoundException("Sustentacion no encontrada"));

        SustentacionDto sustentacionDto = sustentacionMapper.toDto(sustentacion);
        List<SustentacionEvaluador> evaluadores = sustentacionEvaluadorRepository.findByIdSustentacion(sustentacion.getId());

        List<SustentacionEvaluadorDto> evaluadoresDto = evaluadores.stream()
                .map(evaluador -> {
                    Usuario usuario = evaluador.getUsuario();
                    return new SustentacionEvaluadorDto(
                            evaluador.getIdSustentacion(),
                            evaluador.getIdUsuario(),
                            evaluador.getObservaciones(),
                            evaluador.getNota(),
                            evaluador.isJuradoExterno(),
                            usuario.getNombreCompleto(),
                            usuario.getFotoUrl(),
                            usuario.getEmail(),
                            usuario.getTelefono()
                    );
                })
                .collect(Collectors.toList());

        List<CriterioEvaluacion> criterios = criterioEvaluacionRepository.findBySustentacionId(sustentacion.getId());

        List<CriterioEvaluacionDto> criteriosDto = criterios.stream()
                .map(criterio -> criterioEvaluacionMapper.toDto(criterio))
                .collect(Collectors.toList());

        sustentacionDto.setEvaluadores(evaluadoresDto);
        sustentacionDto.setCriteriosEvaluacion(criteriosDto);
        return sustentacionDto;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ROLE_ESTUDIANTE') or hasAuthority('ROLE_DOCENTE') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
    public List<SustentacionDto> listarSustentaciones(@Nullable Integer idProyecto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_SUPERADMIN"));


        List<Sustentacion> sustentaciones = null;

        if (isAdmin) {
            if (idProyecto == null) {
                throw new IllegalArgumentException("Debe proporcionar un ID de proyecto");
            }
            sustentaciones = sustentacionRepository.findByProyectoId(idProyecto);
        } else {
            Usuario activo = usuarioRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            if(activo.getRolId().getNombre().equalsIgnoreCase("Estudiante")){
                Proyecto proyecto = usuarioProyectoRepository.findProyectoByEstudianteId(activo.getId())
                        .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado"));
                sustentaciones = sustentacionRepository.findByProyectoId(proyecto.getId());
            } else {
                List<SustentacionEvaluador> evaluaciones = sustentacionEvaluadorRepository.findByIdUsuario(activo.getId());
                sustentaciones = evaluaciones.stream()
                        .map(SustentacionEvaluador::getSustentacion)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            }
        }

        return sustentaciones.stream().map(sustentacion -> {
            SustentacionDto dto = sustentacionMapper.toDto(sustentacion);

            List<SustentacionEvaluador> evaluadores = sustentacionEvaluadorRepository.findByIdSustentacion(sustentacion.getId());

            List<SustentacionEvaluadorDto> evaluadoresDto = evaluadores.stream()
                    .map(evaluador -> {
                        Usuario usuario = evaluador.getUsuario();
                        return new SustentacionEvaluadorDto(
                                evaluador.getIdSustentacion(),
                                evaluador.getIdUsuario(),
                                evaluador.getObservaciones(),
                                evaluador.getNota(),
                                evaluador.isJuradoExterno(),
                                usuario.getNombreCompleto(),
                                usuario.getFotoUrl(),
                                usuario.getEmail(),
                                usuario.getTelefono()
                        );
                    })
                    .collect(Collectors.toList());

            dto.setEvaluadores(evaluadoresDto);
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
    public SustentacionDto actualizarSustentacion(Integer id, SustentacionDto sustentacionDto) {
        Sustentacion sustentacion = sustentacionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sustentacion no encontrada"));

        if (!proyectoRepository.existsById(sustentacion.getProyecto().getId())) {
            throw new EntityNotFoundException("Proyecto no encontrado");
        }

        sustentacionMapper.partialUpdate(sustentacionDto, sustentacion);
        Sustentacion updatedSustentacion = sustentacionRepository.save(sustentacion);

        return sustentacionMapper.toDto(updatedSustentacion);
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
    public void eliminarSustentacion(Integer id) {
        if (!sustentacionRepository.existsById(id)) {
            throw new EntityNotFoundException("Sustentacion no encontrada");
        }
        sustentacionRepository.deleteById(id);
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
    public void asignarEvaluadorASustentacion(SustentacionEvaluadorDto sustentacionEvaluadorDto) {
        Sustentacion sustentacion = sustentacionRepository.findById(sustentacionEvaluadorDto.getIdSustentacion())
                .orElseThrow(() -> new EntityNotFoundException("Sustentacion no encontrada"));

        Usuario usuario = usuarioRepository.findById(sustentacionEvaluadorDto.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario con ID " + sustentacionEvaluadorDto.getIdUsuario() + " no encontrado"));

        SustentacionEvaluador sustentacionEvaluador = new SustentacionEvaluador();
        sustentacionEvaluador.setIdSustentacion(sustentacion.getId());
        sustentacionEvaluador.setIdUsuario(usuario.getId());
        sustentacionEvaluador.setJuradoExterno(sustentacionEvaluadorDto.isJuradoExterno());
        sustentacionEvaluador.setSustentacion(sustentacion);
        sustentacionEvaluador.setUsuario(usuario);

        sustentacionEvaluadorRepository.save(sustentacionEvaluador);
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
    public void eliminarEvaluadorDeSustentacion(Integer idSustentacion, Integer idEvaluador) {
        boolean existe = sustentacionEvaluadorRepository.existsByIdUsuarioAndIdSustentacion(idEvaluador, idSustentacion);

        if (!existe) {
            throw new RuntimeException("La asignación no existe");
        }

        sustentacionEvaluadorRepository.deleteByIdUsuarioAndIdSustentacion(idEvaluador, idSustentacion);
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_DOCENTE') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
    public void evaluarSustentacion(SustentacionEvaluadorDto sustentacionEvaluadorDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_SUPERADMIN"));

        SustentacionEvaluador sustentacionEvaluador = new SustentacionEvaluador();

        if (isAdmin){
            boolean existe = sustentacionEvaluadorRepository.existsByIdUsuarioAndIdSustentacion(
                    sustentacionEvaluadorDto.getIdUsuario(),
                    sustentacionEvaluadorDto.getIdSustentacion());

            if (!existe) {
                throw new RuntimeException("La asignación no existe");
            }

            sustentacionEvaluador = sustentacionEvaluadorRepository.findByIdUsuarioAndIdSustentacion(
                    sustentacionEvaluadorDto.getIdUsuario(),
                    sustentacionEvaluadorDto.getIdSustentacion());

        } else {
            Usuario activo = usuarioRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            sustentacionEvaluadorDto.setIdUsuario(activo.getId());
            boolean existe = sustentacionEvaluadorRepository.existsByIdUsuarioAndIdSustentacion(
                    activo.getId(),
                    sustentacionEvaluadorDto.getIdSustentacion());

            if (!activo.getId().equals(sustentacionEvaluadorDto.getIdUsuario())) {
                throw new RuntimeException("No tienes permiso para evaluar esta sustentación");
            }

            if (!existe) {
                throw new RuntimeException("La asignación no existe");
            }

            sustentacionEvaluador = sustentacionEvaluadorRepository.findByIdUsuarioAndIdSustentacion(
                    activo.getId(),
                    sustentacionEvaluadorDto.getIdSustentacion());
        }

        if (sustentacionEvaluadorDto.getObservaciones() != null && !sustentacionEvaluadorDto.getObservaciones().isBlank()) {
            sustentacionEvaluador.setObservaciones(sustentacionEvaluadorDto.getObservaciones());
        }
        if (sustentacionEvaluadorDto.getNota() != null) {
            sustentacionEvaluador.setNota(sustentacionEvaluadorDto.getNota());
        }
        sustentacionEvaluadorRepository.save(sustentacionEvaluador);
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
    public CriterioEvaluacionDto agregarCriterioEvaluacion(CriterioEvaluacionDto criterioEvaluacionDto) {
        Sustentacion sustentacion = sustentacionRepository.findById(criterioEvaluacionDto.getIdSustentacion())
                .orElseThrow(() -> new EntityNotFoundException("Sustentación no encontrada"));

        CriterioEvaluacion criterio = criterioEvaluacionMapper.toEntity(criterioEvaluacionDto);
        criterio.setSustentacion(sustentacion);

        return criterioEvaluacionMapper.toDto(criterioEvaluacionRepository.save(criterio));
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
    public CriterioEvaluacionDto actualizarCriterioEvaluacion(Integer id, CriterioEvaluacionDto dto) {
        CriterioEvaluacion criterio = criterioEvaluacionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Criterio no encontrado"));

        criterioEvaluacionMapper.partialUpdate(dto, criterio);

        return criterioEvaluacionMapper.toDto(criterioEvaluacionRepository.save(criterio));
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
    public void eliminarCriterioEvaluacion(Integer id) {
        if (!criterioEvaluacionRepository.existsById(id)) {
            throw new EntityNotFoundException("Criterio no encontrado");
        }
        criterioEvaluacionRepository.deleteById(id);
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
    public void marcarSustentacionRealizada(Integer idSustentacion) {
        Sustentacion sustentacion = sustentacionRepository.findById(idSustentacion)
                .orElseThrow(() -> new EntityNotFoundException("Sustentación no encontrada"));

        sustentacion.setSustentacionRealizada(true);
        sustentacionRepository.save(sustentacion);
    }
}
