package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.services;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.GrupoCohorte;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos.ColoquioDto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Coloquio;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.mappers.ColoquioMapper;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories.ColoquioRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.GrupoCohorteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ColoquioService {

    private final GrupoCohorteRepository grupoCohorteRepository;

    private final ColoquioRepository coloquioRepository;
    private final ColoquioMapper coloquioMapper;


    @Autowired
    public ColoquioService(GrupoCohorteRepository grupoCohorteRepository, ColoquioRepository coloquioRepository, ColoquioMapper coloquioMapper) {
        this.grupoCohorteRepository = grupoCohorteRepository;
        this.coloquioRepository = coloquioRepository;
        this.coloquioMapper = coloquioMapper;
    }

    @Transactional
    public void crearColoquio(ColoquioDto coloquioDto) {
        GrupoCohorte grupoCohorte = grupoCohorteRepository.findById(coloquioDto.getGrupoCohorteId())
                .orElseThrow(() -> new EntityNotFoundException("GrupoCohorte no encontrado"));
        Coloquio coloquio = coloquioMapper.toEntity(coloquioDto);
        coloquio.setGrupoCohorte(grupoCohorte);
        coloquioRepository.save(coloquio);
    }

    @Transactional(readOnly = true)
    public ColoquioDto obtenerColoquioPorId(Integer id) {
        return coloquioRepository.findById(id)
                .map(coloquioMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Coloquio no encontrado"));
    }

    @Transactional(readOnly = true)
    public List<ColoquioDto> obtenerColoquiosPorGrupoCohorteId(Long grupoCohorteId) {
        return coloquioRepository.findByGrupoCohorteId(grupoCohorteId)
                .stream()
                .map(coloquioMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ColoquioDto> obtenerColoquiosPorUsuarioId(Integer usuarioId) {
        return coloquioRepository.findColoquiosByUsuarioId(usuarioId)
                .stream()
                .map(coloquioMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ColoquioDto actualizarColoquio(Integer id, ColoquioDto coloquioDto) {
        Coloquio existingColoquio = coloquioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Coloquio no encontrado"));

        Coloquio coloquio = coloquioMapper.partialUpdate(coloquioDto, existingColoquio);
        return coloquioMapper.toDto(coloquioRepository.save(coloquio));
    }

}
