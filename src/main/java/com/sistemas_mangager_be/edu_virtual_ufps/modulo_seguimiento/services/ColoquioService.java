package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.services;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.GrupoCohorte;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Usuario;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos.ColoquioDto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Coloquio;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.mappers.ColoquioMapper;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories.ColoquioEstudianteRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories.ColoquioRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.GrupoCohorteRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.UsuarioRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.GrupoCohorteDocenteResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ColoquioService {

    private final GrupoCohorteRepository grupoCohorteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ColoquioRepository coloquioRepository;
    private final ColoquioMapper coloquioMapper;
    private final ColoquioEstudianteRepository coloquioEstudianteRepository;


    @Autowired
    public ColoquioService(GrupoCohorteRepository grupoCohorteRepository, UsuarioRepository usuarioRepository,
                           ColoquioRepository coloquioRepository, ColoquioMapper coloquioMapper,
                           ColoquioEstudianteRepository coloquioEstudianteRepository) {
        this.grupoCohorteRepository = grupoCohorteRepository;
        this.usuarioRepository = usuarioRepository;
        this.coloquioRepository = coloquioRepository;
        this.coloquioMapper = coloquioMapper;
        this.coloquioEstudianteRepository = coloquioEstudianteRepository;
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_DOCENTE') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
    public ColoquioDto crearColoquio(ColoquioDto coloquioDto) {
        GrupoCohorte grupoCohorte = grupoCohorteRepository.findById(coloquioDto.getGrupoCohorteId())
                .orElseThrow(() -> new EntityNotFoundException("GrupoCohorte no encontrado"));
        Coloquio coloquio = coloquioMapper.toEntity(coloquioDto);
        coloquio.setGrupoCohorte(grupoCohorte);
        return coloquioMapper.toDto(coloquioRepository.save(coloquio));
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ROLE_ESTUDIANTE') or hasAuthority('ROLE_DOCENTE') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
    public ColoquioDto obtenerColoquioPorId(Integer id) {
        ColoquioDto coloquioDto = coloquioRepository.findById(id)
                .map(coloquioMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Coloquio no encontrado"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isEstudiante = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ESTUDIANTE"));

        if(isEstudiante) {
            Usuario activo = usuarioRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            boolean tieneEntregas = coloquioEstudianteRepository.existsByColoquioIdAndIdEstudiante(coloquioDto.getId(), activo.getId());
            coloquioDto.setTieneEntregas(tieneEntregas);
        } else {
            List<Integer> estudiantesConEntregas = coloquioEstudianteRepository.findIdEstudiantesConDocumentoEntregado(coloquioDto.getId());
            boolean tieneEntregas = estudiantesConEntregas != null && !estudiantesConEntregas.isEmpty();
            coloquioDto.setTieneEntregas(tieneEntregas);
        }
        return coloquioDto;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ROLE_ESTUDIANTE') or hasAuthority('ROLE_DOCENTE') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
    public List<ColoquioDto> obtenerColoquiosPorGrupoCohorteId(Long grupoCohorteId) {
        List<ColoquioDto> coloquios = coloquioRepository.findByGrupoCohorteId(grupoCohorteId)
                .stream()
                .map(coloquioMapper::toDto)
                .collect(Collectors.toList());

        coloquios.forEach(coloquioDto -> {
            List<Integer> estudiantesConEntregas = coloquioEstudianteRepository.findIdEstudiantesConDocumentoEntregado(coloquioDto.getId());
            boolean tieneEntregas = estudiantesConEntregas != null && !estudiantesConEntregas.isEmpty();
            coloquioDto.setTieneEntregas(tieneEntregas);
        });
        return coloquios;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ROLE_ESTUDIANTE')")
    public List<ColoquioDto> obtenerColoquiosPorUsuarioId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario activo = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<ColoquioDto> coloquios = coloquioRepository.findColoquiosByUsuarioId(activo.getId())
                .stream()
                .map(coloquioMapper::toDto)
                .collect(Collectors.toList());

        coloquios.forEach(coloquioDto -> {
            boolean tieneEntregas = coloquioEstudianteRepository.existsByColoquioIdAndIdEstudiante(coloquioDto.getId(), activo.getId());
            coloquioDto.setTieneEntregas(tieneEntregas);
        });
        return coloquios;
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_DOCENTE') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
    public ColoquioDto actualizarColoquio(Integer id, ColoquioDto coloquioDto) {
        Coloquio existingColoquio = coloquioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Coloquio no encontrado"));

        Coloquio coloquio = coloquioMapper.partialUpdate(coloquioDto, existingColoquio);
        return coloquioMapper.toDto(coloquioRepository.save(coloquio));
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ROLE_ESTUDIANTE') or hasAuthority('ROLE_DOCENTE') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
    public List<Map<String, Object>> estudiantesConEntregasPorColoquioId(Integer idColoquio) {
        List<Integer> idsEstudiantes = coloquioEstudianteRepository.findIdEstudiantesConDocumentoEntregado(idColoquio);
        if(idsEstudiantes == null || idsEstudiantes.isEmpty()) {
            return Collections.emptyList();
        }
        List<Usuario> usuarios = usuarioRepository.findAllById(idsEstudiantes);

        return usuarios.stream()
                .map(u -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", u.getId());
                    map.put("nombreCompleto", u.getNombreCompleto());
                    map.put("foto", u.getFotoUrl());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ROLE_DOCENTE')")
    public List<GrupoCohorteDocenteResponse> listarGruposPorDocente() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario activo = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<GrupoCohorte> grupoCohorteDocentes = grupoCohorteRepository.findByDocenteId(activo);
        return grupoCohorteDocentes.stream().map(grupoCohorteDocente -> {
            GrupoCohorteDocenteResponse grupoCohorteDocenteResponse = new GrupoCohorteDocenteResponse().builder()
                    .id(grupoCohorteDocente.getId())
                    .grupoCohorteId(grupoCohorteDocente.getId())
                    .grupoId(grupoCohorteDocente.getGrupoId().getId())
                    .cohorteGrupoId(grupoCohorteDocente.getCohorteGrupoId().getId())
                    .docenteId(grupoCohorteDocente.getDocenteId().getId())
                    .docenteNombre(grupoCohorteDocente.getDocenteId().getNombreCompleto())
                    .cohorteGrupoNombre(grupoCohorteDocente.getCohorteGrupoId().getNombre())
                    .cohorteId(grupoCohorteDocente.getCohorteId().getId())
                    .cohorteNombre(grupoCohorteDocente.getCohorteId().getNombre())
                    .fechaCreacion(grupoCohorteDocente.getFechaCreacion().toString())
                    .grupoNombre(grupoCohorteDocente.getGrupoId().getNombre())
                    .codigoGrupo(grupoCohorteDocente.getGrupoId().getCodigo())
                    .materia(grupoCohorteDocente.getGrupoId().getMateriaId().getNombre())
                    .codigoMateria(grupoCohorteDocente.getGrupoId().getMateriaId().getCodigo())
                    .semestreMateria(grupoCohorteDocente.getGrupoId().getMateriaId().getSemestre())
                    .moodleId(grupoCohorteDocente.getMoodleId())
                    .materiaId(grupoCohorteDocente.getGrupoId().getMateriaId().getId())
                    .programaId(grupoCohorteDocente.getGrupoId().getMateriaId().getPensumId().getProgramaId().getId())
                    .build();
            return grupoCohorteDocenteResponse;
        }).toList();

    }


}
