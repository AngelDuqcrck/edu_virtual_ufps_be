package com.sistemas_mangager_be.edu_virtual_ufps.services.implementations;

import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Cohorte;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.CohorteNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.CohorteRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.ICohorteService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.CohorteDTO;

import net.minidev.json.writer.BeansMapper.Bean;

@Service
public class CohorteServiceImplementation implements ICohorteService {
    
    public static final String IS_ALREADY_USE = "%s ya esta en uso";
    public static final String IS_NOT_FOUND = "%s no fue encontrado";
    public static final String IS_NOT_FOUND_F = "%s no fue encontrada";
    public static final String IS_NOT_ALLOWED = "no esta permitido %s ";
    public static final String IS_NOT_VALID = "%s no es valido";
    public static final String ARE_NOT_EQUALS = "%s no son iguales";
    public static final String IS_NOT_CORRECT = "%s no es correcta";

    @Autowired
    private CohorteRepository cohorteRepository;

    @Override
    public CohorteDTO crearCohorte(CohorteDTO cohorteDTO) {
        Cohorte cohorte = new Cohorte();
        BeanUtils.copyProperties(cohorteDTO, cohorte);

        cohorte.setFechaCreacion(new Date());

        cohorteRepository.save(cohorte);

        CohorteDTO cohorteCreado = new CohorteDTO();
        BeanUtils.copyProperties(cohorte, cohorteCreado);
        return cohorteCreado;
    }

    @Override
    public CohorteDTO listarCohorte(Integer id) throws CohorteNotFoundException {
        Cohorte cohorte = cohorteRepository.findById(id).orElse(null);
        if (cohorte == null) {
            throw new CohorteNotFoundException(String.format(IS_NOT_FOUND_F, "LA COHORTE CON EL ID " + id).toLowerCase());
        
        }

        CohorteDTO cohorteDTO = new CohorteDTO();
        BeanUtils.copyProperties(cohorte, cohorteDTO);
        return cohorteDTO;
    }

    @Override
    public CohorteDTO actualizarCohorte(CohorteDTO cohorteDTO, Integer id) throws CohorteNotFoundException {
        Cohorte cohorte = cohorteRepository.findById(id).orElse(null);
        if (cohorte == null) {
            throw new CohorteNotFoundException(String.format(IS_NOT_FOUND_F, "LA COHORTE CON EL ID " + id).toLowerCase());
        
        }
        BeanUtils.copyProperties(cohorteDTO, cohorte);
        cohorte.setId(id);
        cohorte.setFechaCreacion(new Date());
        cohorteRepository.save(cohorte);

        CohorteDTO cohorteActualizada = new CohorteDTO();
        BeanUtils.copyProperties(cohorte, cohorteActualizada);
        return cohorteActualizada;
    }

    @Override
    public List<CohorteDTO> listarCohortes() {
        List<Cohorte> cohortes = cohorteRepository.findAll();
        return cohortes.stream().map(cohorte -> {
            CohorteDTO cohorteDTO = new CohorteDTO();
            BeanUtils.copyProperties(cohorte, cohorteDTO);
            return cohorteDTO;
        }).toList();
    }

}
