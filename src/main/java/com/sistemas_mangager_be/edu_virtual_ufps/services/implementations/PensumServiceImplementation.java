package com.sistemas_mangager_be.edu_virtual_ufps.services.implementations;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Pensum;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Programa;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.PensumExistException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.PensumNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.ProgramaNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.PensumRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.ProgramaRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.IPensumService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.PensumDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.requests.MoodleRequest;

@Service
public class PensumServiceImplementation implements IPensumService {

    public static final String IS_ALREADY_USE = "%s ya esta en uso";
    public static final String IS_NOT_FOUND = "%s no fue encontrado";
    public static final String IS_NOT_FOUND_F = "%s no fue encontrada";
    public static final String IS_NOT_ALLOWED = "no esta permitido %s ";
    public static final String IS_NOT_VALID = "%s no es valido";
    public static final String ARE_NOT_EQUALS = "%s no son iguales";
    public static final String IS_NOT_CORRECT = "%s no es correcta";

    @Autowired
    private PensumRepository pensumRepository;

    @Autowired
    private ProgramaRepository programaRepository;

    @Override
    public PensumDTO crearPensum(PensumDTO pensumDTO) throws ProgramaNotFoundException {
        Pensum pensum = new Pensum();
        Programa programa = programaRepository.findById(pensumDTO.getProgramaId()).orElse(null);
        if(programa == null) {
            throw new ProgramaNotFoundException(String.format(IS_NOT_FOUND_F, "EL PROGRAMA CON EL ID " +pensumDTO.getProgramaId()).toLowerCase());
        }

        BeanUtils.copyProperties(pensumDTO, pensum);
        pensum.setProgramaId(programa);
        pensumRepository.save(pensum);

        PensumDTO pensumCreado = new PensumDTO();
        BeanUtils.copyProperties(pensum, pensumCreado);
        pensumCreado.setProgramaId(pensum.getProgramaId().getId());
        return pensumCreado;


    }

    @Override
    public PensumDTO listarPensum(Integer id) throws PensumNotFoundException {
        Pensum pensum = pensumRepository.findById(id).orElse(null);

        if(pensum == null){
            throw new PensumNotFoundException(String.format(IS_NOT_FOUND, "EL PENSUM CON EL ID " + id).toLowerCase());
        }

        PensumDTO pensumDTO = new PensumDTO();
        BeanUtils.copyProperties(pensum, pensumDTO);
        pensumDTO.setProgramaId(pensum.getProgramaId().getId());
        return pensumDTO;
        
        
    }

    @Override
    public PensumDTO actualizarPensum(PensumDTO pensumDTO, Integer id) throws PensumNotFoundException, ProgramaNotFoundException {
        Pensum pensum = pensumRepository.findById(id).orElse(null);

        if(pensum == null){
            throw new PensumNotFoundException(String.format(IS_NOT_FOUND, "EL PENSUM CON EL ID " + id).toLowerCase());
        }

        BeanUtils.copyProperties(pensumDTO, pensum);
        Programa programa = programaRepository.findById(pensumDTO.getProgramaId()).orElse(null);
        if(programa == null) {
            throw new ProgramaNotFoundException(String.format(IS_NOT_FOUND_F, "EL PROGRAMA CON EL ID " +pensumDTO.getProgramaId()).toLowerCase());
        }
        pensum.setId(id);
        pensum.setProgramaId(programa);
        pensumRepository.save(pensum);

        PensumDTO pensumActualizado = new PensumDTO();
        BeanUtils.copyProperties(pensum, pensumActualizado);
        pensumActualizado.setProgramaId(pensum.getProgramaId().getId());
        return pensumActualizado;
    }

    @Override
    public List<PensumDTO> listarPensums() {
        
        List<Pensum> pensums = pensumRepository.findAll();
        return pensums.stream().map(pensum -> {
            PensumDTO pensumDTO = new PensumDTO();
            BeanUtils.copyProperties(pensum, pensumDTO);
            pensumDTO.setProgramaId(pensum.getProgramaId().getId());
            return pensumDTO;
        }).toList();
    }

    @Override
    public List<PensumDTO> listarPensumsPorPrograma(Integer id) throws ProgramaNotFoundException {
        
        Programa programa = programaRepository.findById(id).orElse(null);
        if (programa == null) {
            throw new ProgramaNotFoundException(String.format(IS_NOT_FOUND_F, "EL PROGRAMA CON EL ID " + id).toLowerCase());
        }

        List<Pensum> pensums = pensumRepository.findByProgramaId(programa).stream().toList();
        return pensums.stream().map(pensum -> {
            PensumDTO pensumDTO = new PensumDTO();
            BeanUtils.copyProperties(pensum, pensumDTO);
            pensumDTO.setProgramaId(pensum.getProgramaId().getId());
            return pensumDTO;
        }).toList();
    }
    
}
