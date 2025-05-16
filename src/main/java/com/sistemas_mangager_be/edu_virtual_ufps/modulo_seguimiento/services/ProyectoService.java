package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.services;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Rol;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Usuario;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos.ObjetivoEspecificoDto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos.ProyectoDto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos.UsuarioProyectoDto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.LineaInvestigacion;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.ObjetivoEspecifico;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Proyecto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.enums.EstadoProyecto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.intermedias.UsuarioProyecto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.mappers.ObjetivoEspecificoMapper;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.mappers.ProyectoMapper;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories.LineaInvestigacionRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories.ObjetivoEspecificoRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories.ProyectoRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories.UsuarioProyectoRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.RolRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;
    private final UsuarioProyectoRepository usuarioProyectoRepository;
    private final LineaInvestigacionRepository lineaInvestigacionRepository;
    private final ObjetivoEspecificoRepository objetivoEspecificoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    private final ProyectoMapper proyectoMapper;
    private final ObjetivoEspecificoMapper objetivoEspecificoMapper;



    @Autowired
    public ProyectoService(ProyectoRepository proyectoRepository, UsuarioProyectoRepository usuarioProyectoRepository,
                           LineaInvestigacionRepository lineaInvestigacionRepository, ObjetivoEspecificoRepository objetivoEspecificoRepository,
                           UsuarioRepository usuarioRepository, RolRepository rolRepository, ProyectoMapper proyectoMapper,
                           ObjetivoEspecificoMapper objetivoEspecificoMapper) {
        this.proyectoRepository = proyectoRepository;
        this.usuarioProyectoRepository = usuarioProyectoRepository;
        this.lineaInvestigacionRepository = lineaInvestigacionRepository;
        this.objetivoEspecificoRepository = objetivoEspecificoRepository;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.proyectoMapper = proyectoMapper;
        this.objetivoEspecificoMapper = objetivoEspecificoMapper;
    }

    @Transactional
    public ProyectoDto crearProyecto(ProyectoDto proyectoDto) {
        LineaInvestigacion linea = lineaInvestigacionRepository
                .findById(proyectoDto.getLineaInvestigacion().getId())
                .orElseThrow(() -> new RuntimeException("Línea de investigación no encontrada"));

        Proyecto proyecto = proyectoMapper.toEntity(proyectoDto);
        proyecto.setLineaInvestigacion(linea);
        Proyecto guardado = proyectoRepository.save(proyecto);
        return proyectoMapper.toDto(guardado);
    }

    @Transactional(readOnly = true)
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
    public List<ProyectoDto> listarProyectos() {
        List<Proyecto> proyectos = proyectoRepository.findAll();

        List<ProyectoDto> proyectosDto = proyectos.stream()
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

        return proyectosDto;
    }

    @Transactional
    public ProyectoDto actualizarProyecto(Integer id, ProyectoDto proyectoDto) {
        Proyecto existente = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        EstadoProyecto estadoActual = existente.getEstadoActual();
        Integer nuevoEstadoCode = proyectoDto.getEstadoActual();

        if (nuevoEstadoCode != null) {
            EstadoProyecto nuevoEstado = EstadoProyecto.values()[nuevoEstadoCode];

            if (nuevoEstado.ordinal() > estadoActual.ordinal() + 1) {
                throw new RuntimeException("No se puede saltar fases. Debe avanzar una fase a la vez.");
            }

            if (nuevoEstado == EstadoProyecto.FASE_7) {
                boolean todosEvaluados = existente.getObjetivosEspecificos().stream()
                        .allMatch(obj -> obj.getEvaluacion() != null && obj.getEvaluacion());

                if (!todosEvaluados) {
                    throw new RuntimeException("No se puede pasar a la FASE_7. Todos los objetivos deben estar evaluados.");
                }
            }
        }

        List<ObjetivoEspecificoDto> objetivosDto = proyectoDto.getObjetivosEspecificos();
        proyectoDto.setObjetivosEspecificos(null);

        proyectoMapper.partialUpdate(proyectoDto, existente);

        if (objetivosDto != null) {
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

        Proyecto actualizado = proyectoRepository.save(existente);
        return proyectoMapper.toDto(actualizado);
    }

    @Transactional
    public void eliminarProyecto(Integer id) {
        if (!proyectoRepository.existsById(id)) {
            throw new RuntimeException("Proyecto no encontrado");
        }
        proyectoRepository.deleteById(id);
    }

    @Transactional
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
    public void desasignarUsuarioDeProyecto(Integer idUsuario, Integer idProyecto) {
        boolean existe = usuarioProyectoRepository.existsByIdUsuarioAndIdProyecto(idUsuario, idProyecto);

        if (!existe) {
            throw new RuntimeException("La asignación no existe");
        }

        usuarioProyectoRepository.deleteByIdUsuarioAndIdProyecto(idUsuario, idProyecto);
    }
}
