package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.services;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Usuario;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos.SustentacionDto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos.SustentacionEvaluadorDto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Sustentacion;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.intermedias.SustentacionEvaluador;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.mappers.SustentacionMapper;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories.ProyectoRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories.SustentacionEvaluadorRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories.SustentacionRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SustentacionService {

    private final SustentacionRepository sustentacionRepository;
    private final SustentacionMapper sustentacionMapper;
    private final SustentacionEvaluadorRepository sustentacionEvaluadorRepository;
    private final ProyectoRepository proyectoRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public SustentacionService(SustentacionRepository sustentacionRepository, SustentacionMapper sustentacionMapper,
                               SustentacionEvaluadorRepository sustentacionEvaluadorRepository, ProyectoRepository proyectoRepository,
                               UsuarioRepository usuarioRepository) {
        this.sustentacionRepository = sustentacionRepository;
        this.sustentacionMapper = sustentacionMapper;
        this.sustentacionEvaluadorRepository = sustentacionEvaluadorRepository;
        this.proyectoRepository = proyectoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public SustentacionDto crearSustentacion(SustentacionDto sustentacionDto) {
        if (!proyectoRepository.existsById(sustentacionDto.getIdProyecto())) {
            throw new EntityNotFoundException("Proyecto no encontrado");
        }

        Sustentacion sustentacion = sustentacionMapper.toEntity(sustentacionDto);

        Sustentacion savedSustentacion = sustentacionRepository.save(sustentacion);

        return sustentacionMapper.toDto(savedSustentacion);
    }

    @Transactional(readOnly = true)
    public SustentacionDto obtenerSustentacion(Integer id) {
        Sustentacion sustentacion = sustentacionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sustentacion no encontrada"));

        SustentacionDto sustentacionDto = sustentacionMapper.toDto(sustentacion);
        List<SustentacionEvaluador> evaluadores = sustentacionEvaluadorRepository.findByIdSustentacion(id);

        List<SustentacionEvaluadorDto> evaluadoresDto = evaluadores.stream()
                .map(evaluador -> {
                    Usuario usuario = evaluador.getUsuario();
                    return new SustentacionEvaluadorDto(
                            evaluador.getIdSustentacion(),
                            evaluador.getIdUsuario(),
                            evaluador.getObservaciones(),
                            evaluador.getNota(),
                            usuario.getNombreCompleto(),
                            usuario.getFotoUrl(),
                            usuario.getEmail(),
                            usuario.getTelefono()
                    );
                })
                .collect(Collectors.toList());

        sustentacionDto.setEvaluadores(evaluadoresDto);
        return sustentacionDto;
    }

    @Transactional(readOnly = true)
    public List<SustentacionDto> listarSustentaciones() {
        List<Sustentacion> sustentaciones = sustentacionRepository.findAll();

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
    public void eliminarSustentacion(Integer id) {
        if (!sustentacionRepository.existsById(id)) {
            throw new EntityNotFoundException("Sustentacion no encontrada");
        }
        sustentacionRepository.deleteById(id);
    }

    @Transactional
    public void asignarEvaluadorASustentacion(SustentacionEvaluadorDto sustentacionEvaluadorDto) {
        Sustentacion sustentacion = sustentacionRepository.findById(sustentacionEvaluadorDto.getIdSustentacion())
                .orElseThrow(() -> new EntityNotFoundException("Sustentacion no encontrada"));

        Usuario usuario = usuarioRepository.findById(sustentacionEvaluadorDto.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario con ID " + sustentacionEvaluadorDto.getIdUsuario() + " no encontrado"));

        SustentacionEvaluador sustentacionEvaluador = new SustentacionEvaluador();
        sustentacionEvaluador.setIdSustentacion(sustentacion.getId());
        sustentacionEvaluador.setIdUsuario(usuario.getId());
        sustentacionEvaluador.setSustentacion(sustentacion);
        sustentacionEvaluador.setUsuario(usuario);

        sustentacionEvaluadorRepository.save(sustentacionEvaluador);
    }

    @Transactional
    public void eliminarEvaluadorDeSustentacion(Integer idSustentacion, Integer idEvaluador) {
        boolean existe = sustentacionEvaluadorRepository.existsByIdUsuarioAndIdSustentacion(idEvaluador, idSustentacion);

        if (!existe) {
            throw new RuntimeException("La asignación no existe");
        }

        sustentacionEvaluadorRepository.deleteByIdUsuarioAndIdSustentacion(idEvaluador, idSustentacion);
    }

    @Transactional
    public void evaluarSustentacion(SustentacionEvaluadorDto sustentacionEvaluadorDto) {
        boolean existe = sustentacionEvaluadorRepository.existsByIdUsuarioAndIdSustentacion(
                sustentacionEvaluadorDto.getIdUsuario(),
                sustentacionEvaluadorDto.getIdSustentacion());

        if (!existe) {
            throw new RuntimeException("La asignación no existe");
        }

        SustentacionEvaluador sustentacionEvaluador = sustentacionEvaluadorRepository.findByIdUsuarioAndIdSustentacion(
                sustentacionEvaluadorDto.getIdUsuario(),
                sustentacionEvaluadorDto.getIdSustentacion());

        if (sustentacionEvaluadorDto.getObservaciones() != null && !sustentacionEvaluadorDto.getObservaciones().isBlank()) {
            sustentacionEvaluador.setObservaciones(sustentacionEvaluadorDto.getObservaciones());
        }
        if (sustentacionEvaluadorDto.getNota() != null) {
            sustentacionEvaluador.setNota(sustentacionEvaluadorDto.getNota());
        }
        sustentacionEvaluadorRepository.save(sustentacionEvaluador);
    }
}
