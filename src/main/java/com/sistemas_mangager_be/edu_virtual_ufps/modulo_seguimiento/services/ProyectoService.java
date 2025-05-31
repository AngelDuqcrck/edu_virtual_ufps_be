package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.services;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Rol;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Usuario;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos.*;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.*;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.enums.EstadoProyecto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.enums.TipoSustentacion;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.intermedias.SustentacionEvaluador;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.intermedias.UsuarioProyecto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.mappers.*;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories.*;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.RolRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.UsuarioRepository;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;
    private final UsuarioProyectoRepository usuarioProyectoRepository;
    private final LineaInvestigacionRepository lineaInvestigacionRepository;
    private final LineaInvestigacionMapper lineaInvestigacionMapper;
    private final GrupoInvestigacionRepository grupoInvestigacionRepository;
    private final GrupoInvestigacionMapper grupoInvestigacionMapper;
    private final ObjetivoEspecificoRepository objetivoEspecificoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    private final ProyectoMapper proyectoMapper;
    private final ObjetivoEspecificoMapper objetivoEspecificoMapper;
    private final DefinitivaMapper definitivaMapper;
    private final DefinitivaRepository definitivaRepository;
    private final SustentacionRepository sustentacionRepository;
    private final SustentacionEvaluadorRepository sustentacionEvaluadorRepository;

    private final GenerarDocumentosService generarDocumentosService;
    private final MetaODSRepository metaODSRepository;


    @Autowired
    public ProyectoService(ProyectoRepository proyectoRepository, UsuarioProyectoRepository usuarioProyectoRepository,
                           LineaInvestigacionRepository lineaInvestigacionRepository, LineaInvestigacionMapper lineaInvestigacionMapper,
                           GrupoInvestigacionRepository grupoInvestigacionRepository, GrupoInvestigacionMapper grupoInvestigacionMapper,
                           ObjetivoEspecificoRepository objetivoEspecificoRepository,
                           UsuarioRepository usuarioRepository, RolRepository rolRepository, ProyectoMapper proyectoMapper,
                           ObjetivoEspecificoMapper objetivoEspecificoMapper, DefinitivaMapper definitivaMapper,
                           DefinitivaRepository definitivaRepository,
                           SustentacionRepository sustentacionRepository,
                           SustentacionEvaluadorRepository sustentacionEvaluadorRepository, GenerarDocumentosService generarDocumentosService,
                           MetaODSRepository metaODSRepository) {
        this.proyectoRepository = proyectoRepository;
        this.usuarioProyectoRepository = usuarioProyectoRepository;
        this.lineaInvestigacionRepository = lineaInvestigacionRepository;
        this.lineaInvestigacionMapper = lineaInvestigacionMapper;
        this.grupoInvestigacionRepository = grupoInvestigacionRepository;
        this.grupoInvestigacionMapper = grupoInvestigacionMapper;
        this.objetivoEspecificoRepository = objetivoEspecificoRepository;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.proyectoMapper = proyectoMapper;
        this.objetivoEspecificoMapper = objetivoEspecificoMapper;
        this.definitivaMapper = definitivaMapper;
        this.definitivaRepository = definitivaRepository;
        this.sustentacionRepository = sustentacionRepository;
        this.sustentacionEvaluadorRepository = sustentacionEvaluadorRepository;
        this.generarDocumentosService = generarDocumentosService;
        this.metaODSRepository = metaODSRepository;
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ESTUDIANTE')")
    public ProyectoDto crearProyecto(ProyectoDto proyectoDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario activo = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean yaTieneProyecto = usuarioProyectoRepository.existsByUsuarioIdAndRolNombre(activo.getId(), "Estudiante");
        if (yaTieneProyecto) {
            throw new RuntimeException("El estudiante ya tiene un proyecto asignado");
        }

        LineaInvestigacion lineaInvestigacion = null;
        if (proyectoDto.getLineaInvestigacion() != null && proyectoDto.getLineaInvestigacion().getId() != null) {
            lineaInvestigacion = lineaInvestigacionRepository
                    .findById(proyectoDto.getLineaInvestigacion().getId())
                    .orElseThrow(() -> new RuntimeException("Línea de investigación no encontrada"));
        }

        List<MetaODS> metaODSList = proyectoDto.getMetaODS().stream()
                .map(dto -> metaODSRepository.findById(dto.getId())
                        .orElseThrow(() -> new RuntimeException("MetaODS no encontrada con ID: " + dto.getId())))
                .toList();

        Proyecto proyecto = proyectoMapper.toEntity(proyectoDto);
        proyecto.setLineaInvestigacion(lineaInvestigacion);
        proyecto.setMetaODS(metaODSList);
        proyecto.setCreatedAt(LocalDate.now());
        proyecto.setUpdatedAt(LocalDate.now());
        Proyecto guardado = proyectoRepository.save(proyecto);

        UsuarioProyecto usuarioProyecto = new UsuarioProyecto();
        usuarioProyecto.setIdUsuario(activo.getId());
        usuarioProyecto.setIdProyecto(guardado.getId());
        usuarioProyecto.setUsuario(activo);
        usuarioProyecto.setProyecto(guardado);
        usuarioProyecto.setRol(activo.getRolId());
        usuarioProyectoRepository.save(usuarioProyecto);

        return proyectoMapper.toDto(guardado);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ROLE_ESTUDIANTE')")
    public ProyectoDto obtenerProyectoEstudiante() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario activo = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Proyecto proyecto = usuarioProyectoRepository.findProyectoByEstudianteId(activo.getId())
                .orElseThrow(() -> new RuntimeException("El estudiante no tiene un proyecto asignado"));

        ProyectoDto proyectoDto = proyectoMapper.toDto(proyecto);
        List<UsuarioProyecto> asignaciones = usuarioProyectoRepository.findByIdProyecto(proyecto.getId());

        List<UsuarioProyectoDto> usuarios = asignaciones.stream()
                .map(asignacion -> {
                    Usuario usuario = asignacion.getUsuario();
                    return new UsuarioProyectoDto(
                            asignacion.getIdUsuario(),
                            asignacion.getIdProyecto(),
                            asignacion.getRol(),
                            usuario.getNombreCompleto(),
                            usuario.getFotoUrl(),
                            usuario.getEmail(),
                            usuario.getTelefono()
                    );
                })
                .collect(Collectors.toList());

        proyectoDto.setUsuariosAsignados(usuarios);
        return proyectoDto;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ROLE_DOCENTE') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
    public ProyectoDto obtenerProyecto(Integer id) {
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        ProyectoDto proyectoDto = proyectoMapper.toDto(proyecto);
        List<UsuarioProyecto> asignaciones = usuarioProyectoRepository.findByIdProyecto(id);

        List<UsuarioProyectoDto> usuarios = asignaciones.stream()
                .map(asignacion -> {
                    Usuario usuario = asignacion.getUsuario();
                    return new UsuarioProyectoDto(
                            asignacion.getIdUsuario(),
                            asignacion.getIdProyecto(),
                            asignacion.getRol(),
                            usuario.getNombreCompleto(),
                            usuario.getFotoUrl(),
                            usuario.getEmail(),
                            usuario.getTelefono()
                    );
                })
                .collect(Collectors.toList());

        proyectoDto.setUsuariosAsignados(usuarios);
        return proyectoDto;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ROLE_DOCENTE') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
    public List<ProyectoDto> listarProyectos(@Nullable Integer lineaId,
                                             @Nullable Integer grupoId,
                                             @Nullable Integer programaId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_SUPERADMIN"));

        List<Proyecto> proyectos = null;

        if (isAdmin) {
            proyectos = proyectoRepository.findAllByFiltros(lineaId, grupoId, programaId);
        } else {
            Usuario activo = usuarioRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            proyectos = usuarioProyectoRepository.findProyectosByDocenteDirectorId(activo.getId(), lineaId, grupoId, programaId);
        }

        return proyectos.stream()
                .map(proyecto -> {
                    ProyectoDto proyectoDto = proyectoMapper.toDto(proyecto);

                    List<UsuarioProyecto> asignaciones = usuarioProyectoRepository.findByIdProyecto(proyecto.getId());

                    List<UsuarioProyectoDto> usuarios = asignaciones.stream()
                            .map(asignacion -> {
                                Usuario usuario = asignacion.getUsuario();
                                return new UsuarioProyectoDto(
                                        asignacion.getIdUsuario(),
                                        asignacion.getIdProyecto(),
                                        asignacion.getRol(),
                                        usuario.getNombreCompleto(),
                                        usuario.getFotoUrl(),
                                        usuario.getEmail(),
                                        usuario.getTelefono()
                                );
                            })
                            .collect(Collectors.toList());

                    proyectoDto.setUsuariosAsignados(usuarios);
                    return proyectoDto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ESTUDIANTE') or hasAuthority('ROLE_DOCENTE') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
    public ProyectoDto actualizarProyecto(Integer id, ProyectoDto proyectoDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_SUPERADMIN"));

        Usuario activo = null;
        String rol = null;

        if (!isAdmin) {
            activo = usuarioRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            rol = activo.getRolId().getNombre();
        }

        Proyecto existente = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        existente.setUpdatedAt(LocalDate.now());

        //solo actualiza el estado si el rol es docente, admin o superadmin
        if("Docente".equalsIgnoreCase(rol) || isAdmin){
            EstadoProyecto estadoActual = existente.getEstadoActual();
            Integer nuevoEstadoCode = proyectoDto.getEstadoActual();

            if (nuevoEstadoCode != null) {
                EstadoProyecto nuevoEstado = EstadoProyecto.values()[nuevoEstadoCode];

                if (nuevoEstado.ordinal() > estadoActual.ordinal() + 1) {
                    throw new RuntimeException("No se puede saltar fases. Debe avanzar una fase a la vez.");
                }

                if (nuevoEstado == EstadoProyecto.FASE_7) {
                    boolean todosEvaluados = existente.getObjetivosEspecificos().stream()
                            .allMatch(obj -> {
                                EvaluacionObjetivo eval = obj.getEvaluacion();
                                return eval != null && eval.isDirector() && eval.isCodirector();
                            });

                    if (!todosEvaluados) {
                        throw new RuntimeException("No se puede pasar a la FASE_7. Todos los objetivos deben estar evaluados.");
                    }
                }
            }
        }
        else if(rol.equals("Estudiante")){
            proyectoDto.setEstadoActual(null);
        }
        //solo actualiza el estado si el rol es docente, admin o superadmin

        //quitar objetivos para manejarlos por separado
        List<ObjetivoEspecificoDto> objetivosDto = proyectoDto.getObjetivosEspecificos();
        proyectoDto.setObjetivosEspecificos(null);
        //quitar objetivos para manejarlos por separado

        //datos basicos
        LineaInvestigacion nuevaLinea = existente.getLineaInvestigacion();
        if (proyectoDto.getLineaInvestigacion() != null && proyectoDto.getLineaInvestigacion().getId() != null) {
            nuevaLinea = lineaInvestigacionRepository
                    .findById(proyectoDto.getLineaInvestigacion().getId())
                    .orElseThrow(() -> new RuntimeException("Línea de investigación no encontrada"));
        }
        existente.setLineaInvestigacion(nuevaLinea);

        proyectoMapper.partialUpdate(proyectoDto, existente);
        //datos basicos

        //actualizar objetivos
        if (objetivosDto != null && "Estudiante".equalsIgnoreCase(rol)) {
            List<Integer> idsObjetivosEnviado = objetivosDto.stream()
                    .filter(o -> o.getId() != null)
                    .map(ObjetivoEspecificoDto::getId)
                    .collect(Collectors.toList());

            existente.getObjetivosEspecificos().removeIf(obj ->
                    !idsObjetivosEnviado.contains(obj.getId())
            );

            for (ObjetivoEspecificoDto objetivoDto : objetivosDto) {
                if (objetivoDto.getId() != null) {
                    ObjetivoEspecifico objetivoExistente = objetivoEspecificoRepository.findById(objetivoDto.getId())
                            .orElseThrow(() -> new RuntimeException("Objetivo con id " + objetivoDto.getId() + " no encontrado"));

                    objetivoDto.setAvanceReal(null);
                    objetivoDto.setEvaluacion(null);
                    objetivoEspecificoMapper.partialUpdate(objetivoDto, objetivoExistente);
                    objetivoEspecificoRepository.save(objetivoExistente);
                } else {
                    ObjetivoEspecifico nuevoObjetivo = new ObjetivoEspecifico();
                    nuevoObjetivo.setProyecto(existente);
                    nuevoObjetivo.setNumeroOrden(objetivoDto.getNumeroOrden());
                    nuevoObjetivo.setDescripcion(objetivoDto.getDescripcion());

                    objetivoEspecificoRepository.save(nuevoObjetivo);
                    existente.getObjetivosEspecificos().add(nuevoObjetivo);
                }
            }
        } else if (objetivosDto != null && ("Docente".equalsIgnoreCase(rol) || isAdmin)) {
            List<Integer> idsObjetivosEnviado = objetivosDto.stream()
                    .filter(o -> o.getId() != null)
                    .map(ObjetivoEspecificoDto::getId)
                    .collect(Collectors.toList());

            existente.getObjetivosEspecificos().removeIf(obj ->
                    !idsObjetivosEnviado.contains(obj.getId())
            );

            for (ObjetivoEspecificoDto objetivoDto : objetivosDto) {
                if (objetivoDto.getId() != null) {
                    ObjetivoEspecifico objetivoExistente = objetivoEspecificoRepository.findById(objetivoDto.getId())
                            .orElseThrow(() -> new RuntimeException("Objetivo con id " + objetivoDto.getId() + " no encontrado"));

                    objetivoEspecificoMapper.partialUpdate(objetivoDto, objetivoExistente);
                    objetivoEspecificoRepository.save(objetivoExistente);
                } else {
                    ObjetivoEspecifico nuevoObjetivo = new ObjetivoEspecifico();
                    nuevoObjetivo.setProyecto(existente);
                    nuevoObjetivo.setNumeroOrden(objetivoDto.getNumeroOrden());
                    nuevoObjetivo.setDescripcion(objetivoDto.getDescripcion());

                    objetivoEspecificoRepository.save(nuevoObjetivo);
                    existente.getObjetivosEspecificos().add(nuevoObjetivo);
                }
            }
        }
        //actualizar objetivos

        Proyecto actualizado = proyectoRepository.save(existente);

        if (actualizado.getEstadoActual() == EstadoProyecto.FASE_2 || actualizado.getEstadoActual() == EstadoProyecto.FASE_3 ||
                actualizado.getEstadoActual() == EstadoProyecto.FASE_7 || actualizado.getEstadoActual() == EstadoProyecto.FASE_8 ||
                actualizado.getEstadoActual() == EstadoProyecto.FASE_9){
            generarDocumentosService.generarDocumentosSegunEstado(actualizado);
        }

        return proyectoMapper.toDto(actualizado);
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ESTUDIANTE') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
    public void eliminarProyecto(Integer id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_SUPERADMIN"));

        if (isAdmin) {
            if (!proyectoRepository.existsById(id)) {
                throw new RuntimeException("Proyecto no encontrado");
            }
            proyectoRepository.deleteById(id);
        } else {
            Usuario activo = usuarioRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            if (activo.getRolId().getNombre().equals("Estudiante")) {
                Proyecto proyecto = usuarioProyectoRepository.findProyectoByEstudianteId(activo.getId())
                        .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

                if (!proyecto.getId().equals(id)) {
                    throw new RuntimeException("No tienes permiso para eliminar este proyecto.");
                }
                proyectoRepository.deleteById(proyecto.getId());
            }
        }
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
    public void asignarUsuarioAProyecto(UsuarioProyectoDto dto) {
        Proyecto proyecto = proyectoRepository.findById(dto.getIdProyecto())
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario con ID " + dto.getIdUsuario() + " no encontrado"));

        Rol rol = rolRepository.findById(dto.getRol().getId())
                .orElseThrow(() -> new RuntimeException("Rol con ID " + dto.getRol().getId() + " no encontrado"));

        UsuarioProyecto usuarioProyecto = new UsuarioProyecto();
        usuarioProyecto.setIdUsuario(usuario.getId());
        usuarioProyecto.setIdProyecto(proyecto.getId());
        usuarioProyecto.setUsuario(usuario);
        usuarioProyecto.setProyecto(proyecto);
        usuarioProyecto.setRol(rol);

        usuarioProyectoRepository.save(usuarioProyecto);
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
    public void desasignarUsuarioDeProyecto(Integer idUsuario, Integer idProyecto) {
        boolean existe = usuarioProyectoRepository.existsByIdUsuarioAndIdProyecto(idUsuario, idProyecto);

        if (!existe) {
            throw new RuntimeException("La asignación no existe");
        }

        usuarioProyectoRepository.deleteByIdUsuarioAndIdProyecto(idUsuario, idProyecto);
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_DOCENTE') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
    public DefinitivaDto calcularYAsignarDefinitiva(Integer idProyecto, TipoSustentacion tipoSustentacion) {
        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        if (proyecto.getEstadoActual() != EstadoProyecto.FASE_9) {
            throw new RuntimeException("Solo se puede asignar la calificación en la fase de " + EstadoProyecto.FASE_9.getDescripcion() + " ("+ EstadoProyecto.FASE_9.name() + ")");
        }

        Sustentacion sustentacion = sustentacionRepository.findByProyectoIdAndOptionalTipoSustentacion(idProyecto, tipoSustentacion)
                .orElseThrow(() -> new RuntimeException("Sustentación no encontrada para el proyecto"));

        List<SustentacionEvaluador> evaluadores = sustentacionEvaluadorRepository.findByIdSustentacion(sustentacion.getId());

        if (evaluadores.isEmpty()) {
            throw new RuntimeException("No hay evaluadores asignados a la sustentación");
        }

        double promedio = evaluadores.stream()
                .map(SustentacionEvaluador::getNota)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElseThrow(() -> new RuntimeException("No se encontraron notas válidas"));

        Definitiva definitiva = proyecto.getDefinitiva();
        if (definitiva == null) {
            definitiva = new Definitiva();
            definitiva.setProyecto(proyecto);
        }

        definitiva.setCalificacion(promedio);
        definitiva.setHonores(promedio >= 4.5);

        proyecto.setDefinitiva(definitiva);
        proyectoRepository.save(proyecto);

        return definitivaMapper.toDto(proyecto.getDefinitiva());
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ROLE_ESTUDIANTE') or hasAuthority('ROLE_DOCENTE') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
    public List<LineaInvestigacionDto> listarLineasInvestigacion() {
        return lineaInvestigacionRepository.findAll().stream()
                .map(lineaInvestigacionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ROLE_ESTUDIANTE') or hasAuthority('ROLE_DOCENTE') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
    public List<GruposYLineasInvestigacionDto> listarGruposConLineas() {
        List<GrupoInvestigacion> grupos = grupoInvestigacionRepository.findAll();

        return grupos.stream().map(grupo -> {
            GrupoInvestigacionDto grupoDto = grupoInvestigacionMapper.toDto(grupo);

            List<LineaInvestigacion> lineas = lineaInvestigacionRepository.findByGrupoInvestigacionId(grupo.getId());

            List<LineaInvestigacionBasicaDto> lineasDto = lineas.stream()
                    .map(lineaInvestigacionMapper::toDtoBasica)
                    .collect(Collectors.toList());

            return new GruposYLineasInvestigacionDto(
                    grupoDto.getId(),
                    grupoDto.getNombre(),
                    grupoDto.getPrograma(),
                    lineasDto
            );
        }).collect(Collectors.toList());
    }
}
