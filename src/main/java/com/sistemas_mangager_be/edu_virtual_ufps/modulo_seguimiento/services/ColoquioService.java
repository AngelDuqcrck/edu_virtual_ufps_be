package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.services;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.GrupoCohorte;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Usuario;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos.ColoquioDto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Coloquio;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.mappers.ColoquioMapper;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories.ColoquioRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.GrupoCohorteRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ColoquioService {

    private final GrupoCohorteRepository grupoCohorteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ColoquioRepository coloquioRepository;
    private final ColoquioMapper coloquioMapper;


    @Autowired
    public ColoquioService(GrupoCohorteRepository grupoCohorteRepository, UsuarioRepository usuarioRepository,
                           ColoquioRepository coloquioRepository, ColoquioMapper coloquioMapper) {
        this.grupoCohorteRepository = grupoCohorteRepository;
        this.usuarioRepository = usuarioRepository;
        this.coloquioRepository = coloquioRepository;
        this.coloquioMapper = coloquioMapper;
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_DOCENTE') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
    public void crearColoquio(ColoquioDto coloquioDto) {
        GrupoCohorte grupoCohorte = grupoCohorteRepository.findById(coloquioDto.getGrupoCohorteId())
                .orElseThrow(() -> new EntityNotFoundException("GrupoCohorte no encontrado"));
        Coloquio coloquio = coloquioMapper.toEntity(coloquioDto);
        coloquio.setGrupoCohorte(grupoCohorte);
        coloquioRepository.save(coloquio);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ROLE_ESTUDIANTE') or hasAuthority('ROLE_DOCENTE') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
    public ColoquioDto obtenerColoquioPorId(Integer id) {
        return coloquioRepository.findById(id)
                .map(coloquioMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Coloquio no encontrado"));
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ROLE_ESTUDIANTE') or hasAuthority('ROLE_DOCENTE') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
    public List<ColoquioDto> obtenerColoquiosPorGrupoCohorteId(Long grupoCohorteId) {
        return coloquioRepository.findByGrupoCohorteId(grupoCohorteId)
                .stream()
                .map(coloquioMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ROLE_ESTUDIANTE')")
    public List<ColoquioDto> obtenerColoquiosPorUsuarioId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario activo = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return coloquioRepository.findColoquiosByUsuarioId(activo.getId())
                .stream()
                .map(coloquioMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_DOCENTE') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
    public ColoquioDto actualizarColoquio(Integer id, ColoquioDto coloquioDto) {
        Coloquio existingColoquio = coloquioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Coloquio no encontrado"));

        Coloquio coloquio = coloquioMapper.partialUpdate(coloquioDto, existingColoquio);
        return coloquioMapper.toDto(coloquioRepository.save(coloquio));
    }

}
