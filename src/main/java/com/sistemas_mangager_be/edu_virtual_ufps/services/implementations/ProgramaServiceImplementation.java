package com.sistemas_mangager_be.edu_virtual_ufps.services.implementations;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Programa;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.ProgramaExistsException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.ProgramaNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.ProgramaRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.IProgramaService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.ProgramaDTO;

@Service
public class ProgramaServiceImplementation implements IProgramaService {

    public static final String IS_ALREADY_USE = "%s ya esta registrado en el sistema";
    public static final String IS_NOT_FOUND = "%s no fue encontrado";
    public static final String IS_NOT_FOUND_F = "%s no fue encontrada";
    public static final String IS_NOT_ALLOWED = "no esta permitido %s ";
    public static final String IS_NOT_VALID = "%s no es valido";
    public static final String ARE_NOT_EQUALS = "%s no son iguales";
    public static final String IS_NOT_CORRECT = "%s no es correcta";

    @Autowired
    private ProgramaRepository programaRepository;

    @Override
    public ProgramaDTO listarPrograma(Integer id) throws ProgramaNotFoundException {
        Programa programa = programaRepository.findById(id).orElse(null);
        if (programa == null) {
            throw new ProgramaNotFoundException(
                    String.format(IS_NOT_FOUND, "EL PROGRAMA CON EL ID " + id).toLowerCase());
        }

        return ProgramaDTO.builder()
                .id(programa.getId())
                .nombre(programa.getNombre())
                .codigo(programa.getCodigo())
                .build();
    }

    @Override
    public ProgramaDTO crearPrograma(ProgramaDTO programaDTO) throws ProgramaExistsException {
        // Validar si ya existe un programa con el mismo código
        if (programaRepository.existsByCodigo(programaDTO.getCodigo())) {
            throw new ProgramaExistsException(
                    String.format(IS_ALREADY_USE, "El código de programa " + programaDTO.getCodigo()));
        }

        Programa programa = new Programa();
        BeanUtils.copyProperties(programaDTO, programa);
        programaRepository.save(programa);

        ProgramaDTO programaCreado = new ProgramaDTO();
        BeanUtils.copyProperties(programa, programaCreado);
        return programaCreado;
    }

    @Override
    public ProgramaDTO actualizarPrograma(ProgramaDTO programaDTO, Integer id)
            throws ProgramaNotFoundException, ProgramaExistsException {
        Programa programa = programaRepository.findById(id)
                .orElseThrow(() -> new ProgramaNotFoundException(
                        String.format(IS_NOT_FOUND, "EL PROGRAMA CON EL ID " + id).toLowerCase()));

        // Validar si el código está cambiando y si el nuevo código ya existe
        if (!programa.getCodigo().equals(programaDTO.getCodigo()) &&
                programaRepository.existsByCodigo(programaDTO.getCodigo())) {
            throw new ProgramaExistsException(
                    String.format(IS_ALREADY_USE, "El código de programa " + programaDTO.getCodigo()));
        }

        BeanUtils.copyProperties(programaDTO, programa);
        programa.setId(id);
        programaRepository.save(programa);

        ProgramaDTO programaActualizado = new ProgramaDTO();
        BeanUtils.copyProperties(programa, programaActualizado);
        return programaActualizado;
    }

    @Override
    public List<ProgramaDTO> listarProgramas() {
        return programaRepository.findAll().stream().map(programa -> {
            ProgramaDTO programaDTO = new ProgramaDTO();
            BeanUtils.copyProperties(programa, programaDTO);
            return programaDTO;
        }).toList();
    }

}
