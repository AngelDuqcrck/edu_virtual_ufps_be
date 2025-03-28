package com.sistemas_mangager_be.edu_virtual_ufps.services.implementations;

import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Cohorte;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.CohorteGrupo;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.CohorteNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.CohorteGrupoRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.CohorteRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.ICohorteService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.CohorteDTO;

import jakarta.transaction.Transactional;
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

    @Autowired
    private CohorteGrupoRepository cohorteGrupoRepository;

    @Override
    public CohorteDTO crearCohorte(CohorteDTO cohorteDTO) {
        Cohorte cohorte = new Cohorte();
        BeanUtils.copyProperties(cohorteDTO, cohorte);

        cohorte.setFechaCreacion(new Date());

        cohorteRepository.save(cohorte);

        CohorteGrupo cohorteGrupoA = new CohorteGrupo();
        cohorteGrupoA.setCohorteId(cohorte);
        cohorteGrupoA.setNombre(cohorte.getNombre() + " Grupo A");

        cohorteGrupoRepository.save(cohorteGrupoA);

        CohorteGrupo cohorteGrupoB = new CohorteGrupo();
        cohorteGrupoB.setCohorteId(cohorte);
        cohorteGrupoB.setNombre(cohorte.getNombre() + " Grupo B");

        cohorteGrupoRepository.save(cohorteGrupoB);

        CohorteDTO cohorteCreado = new CohorteDTO();
        BeanUtils.copyProperties(cohorte, cohorteCreado);
        return cohorteCreado;
    }

    @Override
    public CohorteDTO listarCohorte(Integer id) throws CohorteNotFoundException {
        Cohorte cohorte = cohorteRepository.findById(id).orElse(null);
        if (cohorte == null) {
            throw new CohorteNotFoundException(
                    String.format(IS_NOT_FOUND_F, "LA COHORTE CON EL ID " + id).toLowerCase());

        }

        CohorteDTO cohorteDTO = new CohorteDTO();
        BeanUtils.copyProperties(cohorte, cohorteDTO);
        return cohorteDTO;
    }

    @Override
    @Transactional
    public CohorteDTO actualizarCohorte(CohorteDTO cohorteDTO, Integer id) throws CohorteNotFoundException {
        // 1. Buscar la cohorte existente
        Cohorte cohorte = cohorteRepository.findById(id)
                .orElseThrow(() -> new CohorteNotFoundException(
                        String.format(IS_NOT_FOUND_F, "LA COHORTE CON EL ID " + id).toLowerCase()));

        // 2. Guardar el nombre original para comparación
        String nombreOriginal = cohorte.getNombre();

        // 3. Actualizar propiedades de la cohorte
        BeanUtils.copyProperties(cohorteDTO, cohorte);
        cohorte.setId(id);
        cohorte.setFechaCreacion(new Date());

        // 4. Guardar la cohorte actualizada
        Cohorte cohorteActualizada = cohorteRepository.save(cohorte);

        // 5. Buscar grupos existentes
        List<CohorteGrupo> gruposExistentes = cohorteGrupoRepository.findAllByCohorteId(cohorteActualizada);

        // 6. Si el nombre cambió, actualizar los nombres de los grupos
        if (!nombreOriginal.equals(cohorteActualizada.getNombre())) {
            for (CohorteGrupo grupo : gruposExistentes) {
                // Mantener el sufijo (Grupo A, Grupo B) pero actualizar el prefijo
                String sufijo = grupo.getNombre().substring(nombreOriginal.length());
                grupo.setNombre(cohorteActualizada.getNombre() + sufijo);
                cohorteGrupoRepository.save(grupo);
            }
        }

        // 7. Si no existen grupos, crearlos (como en crearCohorte)
        if (gruposExistentes.isEmpty()) {
            crearGruposParaCohorte(cohorteActualizada);
        }

        // 8. Retornar DTO actualizado
        CohorteDTO responseDTO = new CohorteDTO();
        BeanUtils.copyProperties(cohorteActualizada, responseDTO);

        return responseDTO;
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

    

    private void crearGruposParaCohorte(Cohorte cohorte) {
        CohorteGrupo grupoA = new CohorteGrupo();
        grupoA.setCohorteId(cohorte);
        grupoA.setNombre(cohorte.getNombre() + " Grupo A");

        CohorteGrupo grupoB = new CohorteGrupo();
        grupoB.setCohorteId(cohorte);
        grupoB.setNombre(cohorte.getNombre() + " Grupo B");

        cohorteGrupoRepository.save(grupoA);
        cohorteGrupoRepository.save(grupoB);
    }
}
