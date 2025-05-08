package com.sistemas_mangager_be.edu_virtual_ufps.services.implementations;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Materia;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Pensum;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.*;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.MateriaRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.PensumRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.IMateriaService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.MateriaDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.requests.MateriaSemestreRequest;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.requests.MoodleRequest;

@Service
public class MateriaServiceImplementation implements IMateriaService {
    
    public static final String IS_ALREADY_USE = "%s ya esta en registrada en el sistema";
    public static final String IS_NOT_FOUND = "%s no fue encontrado";
    public static final String IS_NOT_FOUND_F = "%s no fue encontrada";
    public static final String IS_NOT_ALLOWED = "no esta permitido %s ";
    public static final String IS_NOT_VALID = "%s no es valido";
    public static final String ARE_NOT_EQUALS = "%s no son iguales";
    public static final String IS_NOT_CORRECT = "%s no es correcta";

    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired
    private PensumRepository pensumRepository;
    
    @Override
    public MateriaDTO crearMateria(MateriaDTO materiaDTO) throws PensumNotFoundException, MateriaExistsException {
        if(materiaRepository.existsByCodigo(materiaDTO.getCodigo())) {
            throw new MateriaExistsException(String.format(IS_ALREADY_USE, "LA MATERIA CON EL CODIGO " + materiaDTO.getCodigo()).toLowerCase());
        }

        Materia materia = new Materia();
        BeanUtils.copyProperties(materiaDTO, materia);

        Pensum pensum = pensumRepository.findById(materiaDTO.getPensumId()).orElse(null);
        
        if (pensum == null) {
            throw new PensumNotFoundException(String.format(IS_NOT_FOUND, "EL PENSUM CON EL ID " + materiaDTO.getPensumId()).toLowerCase());
        }

        materia.setPensumId(pensum);
        materiaRepository.save(materia);

        MateriaDTO materiaCreada = new MateriaDTO();
        BeanUtils.copyProperties(materia, materiaCreada);
        materiaCreada.setPensumId(materia.getPensumId().getId());
        return materiaCreada;
        
        
    }
    
    @Override
    public MateriaDTO actualizarMateria(Integer id,MateriaDTO materiaDTO) throws MateriaExistsException, PensumNotFoundException, MateriaNotFoundException {
        
        Materia materia = materiaRepository.findById(id).orElse(null);
        if (materia == null) {
            throw new MateriaNotFoundException(String.format(IS_NOT_FOUND_F, "EL MATERIA CON EL ID " + materiaDTO.getId()).toLowerCase());
        }

        if(!materia.getCodigo().equals(materiaDTO.getCodigo()) && materiaRepository.existsByCodigo(materiaDTO.getCodigo())) {
            throw new MateriaExistsException(String.format(IS_ALREADY_USE, "LA MATERIA CON EL CODIGO " + materiaDTO.getCodigo()).toLowerCase());
        }
        
        Pensum pensum = pensumRepository.findById(materiaDTO.getPensumId()).orElse(null);
        if (pensum == null) {
            throw new PensumNotFoundException(String.format(IS_NOT_FOUND, "EL PENSUM CON EL ID " + materiaDTO.getPensumId()).toLowerCase());
        }

        BeanUtils.copyProperties(materiaDTO, materia);
        materia.setPensumId(pensum);
        materia.setId(id);
        
        materiaRepository.save(materia);

        MateriaDTO materiaActualizada = new MateriaDTO();
        BeanUtils.copyProperties(materia, materiaActualizada);
        materiaActualizada.setPensumId(materia.getPensumId().getId());
        return materiaActualizada;

    }

    @Override
    public MateriaDTO listarMateria(Integer materiaId) throws MateriaNotFoundException {
        Materia materia = materiaRepository.findById(materiaId).orElse(null);
        if (materia == null) {
            throw new MateriaNotFoundException(String.format(IS_NOT_FOUND_F, "EL MATERIA CON EL ID " + materiaId).toLowerCase());
        }

        MateriaDTO materiaDTO = new MateriaDTO();
        BeanUtils.copyProperties(materia, materiaDTO);
        materiaDTO.setPensumId(materia.getPensumId().getId());
        return materiaDTO;
    }

    @Override
    public List<MateriaDTO> listarMaterias() {
        
        List<Materia> materias = materiaRepository.findAll();
        return materias.stream().map(materia -> {
            MateriaDTO materiaDTO = new MateriaDTO();
            BeanUtils.copyProperties(materia, materiaDTO);
            materiaDTO.setPensumId(materia.getPensumId().getId());
            return materiaDTO;
        }).toList();
    }

    @Override
    public List<MateriaDTO> listarMateriasPorPensum(Integer pensumId) throws PensumNotFoundException {
        Pensum pensum = pensumRepository.findById(pensumId).orElse(null);
        if (pensum == null) {
            throw new PensumNotFoundException(String.format(IS_NOT_FOUND, "EL PENSUM CON EL ID " + pensumId).toLowerCase());
        }

        List<Materia> materias = materiaRepository.findByPensumId(pensum).stream().toList();
        return materias.stream().map(materia -> {
            MateriaDTO materiaDTO = new MateriaDTO();
            BeanUtils.copyProperties(materia, materiaDTO);
            materiaDTO.setPensumId(materia.getPensumId().getId());
            return materiaDTO;
        }).toList();
    }

    @Override
    public List<MateriaDTO> listarMateriasPorPensumPorSemestre(MateriaSemestreRequest materiaSemestreRequest) throws PensumNotFoundException {
       
        Pensum pensum = pensumRepository.findById(materiaSemestreRequest.getPensumId()).orElse(null);
        if (pensum == null) {
            throw new PensumNotFoundException(String.format(IS_NOT_FOUND, "EL PENSUM CON EL ID " + materiaSemestreRequest.getPensumId()).toLowerCase());
        }

        List<Materia> materias = materiaRepository.findByPensumId(pensum).stream().toList();
        return materias.stream().filter(materia -> materia.getSemestre().equals(materiaSemestreRequest.getSemestre())).map(materia -> {
            MateriaDTO materiaDTO = new MateriaDTO();
            BeanUtils.copyProperties(materia, materiaDTO);
            materiaDTO.setPensumId(materia.getPensumId().getId());
            return materiaDTO;
        }).toList();
    }


}
