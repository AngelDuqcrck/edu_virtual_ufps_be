package com.sistemas_mangager_be.edu_virtual_ufps.services.implementations;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Materia;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Pensum;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.SemestrePensum;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.*;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.MateriaRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.PensumRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.SemestrePensumRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.IMateriaService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.MateriaDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.requests.MateriaSemestreRequest;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.requests.MoodleRequest;

import oracle.net.aso.m;

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

        @Autowired
        private SemestrePensumRepository semestrePensumRepository;

        @Override
        public MateriaDTO crearMateria(MateriaDTO materiaDTO)
                        throws PensumNotFoundException, MateriaExistsException, SemestrePensumNotFoundException {
                // Verificar si ya existe una materia con el mismo código
                if (materiaRepository.existsByCodigo(materiaDTO.getCodigo())) {
                        throw new MateriaExistsException(String
                                        .format(IS_ALREADY_USE, "LA MATERIA CON EL CODIGO " + materiaDTO.getCodigo())
                                        .toLowerCase());
                }

                // Buscar el pensum
                Pensum pensum = pensumRepository.findById(materiaDTO.getPensumId())
                                .orElseThrow(() -> new PensumNotFoundException(String
                                                .format(IS_NOT_FOUND, "EL PENSUM CON EL ID " + materiaDTO.getPensumId())
                                                .toLowerCase()));

                // Verificar que el semestre esté dentro del rango permitido para el pensum
                if (pensum.getCantidadSemestres() == null ||
                                Integer.parseInt(materiaDTO.getSemestre()) > pensum.getCantidadSemestres() ||
                                Integer.parseInt(materiaDTO.getSemestre()) < 1) {
                        throw new SemestrePensumNotFoundException(
                                        String.format(IS_NOT_VALID,
                                                        "EL SEMESTRE " + materiaDTO.getSemestre()
                                                                        + " NO ESTÁ EN EL RANGO DEL PENSUM (1-"
                                                                        + pensum.getCantidadSemestres() + ")")
                                                        .toLowerCase());
                }

                // Buscar la relación entre el semestre y el pensum
                Optional<SemestrePensum> semestrePensumOpt = semestrePensumRepository
                                .findFirstByPensumIdAndSemestreId_Numero(pensum,
                                                Integer.parseInt(materiaDTO.getSemestre()));

                if (semestrePensumOpt.isEmpty()) {
                        throw new SemestrePensumNotFoundException(
                                        String.format(IS_NOT_FOUND, "LA RELACIÓN ENTRE EL PENSUM Y EL SEMESTRE "
                                                        + materiaDTO.getSemestre()).toLowerCase());
                }

                SemestrePensum semestrePensum = semestrePensumOpt.get();

                // Crear y configurar la nueva materia
                Materia materia = new Materia();
                BeanUtils.copyProperties(materiaDTO, materia);
                materia.setPensumId(pensum);
                materia.setSemestrePensum(semestrePensum);

                materiaRepository.save(materia);

                // Mapear la materia creada a DTO para retornarla
                MateriaDTO materiaCreada = new MateriaDTO();
                BeanUtils.copyProperties(materia, materiaCreada);
                materiaCreada.setSemestrePensumId(materia.getSemestrePensum().getId());
                materiaCreada.setPensumId(materia.getPensumId().getId());
                return materiaCreada;
        }

        @Override
        public MateriaDTO actualizarMateria(Integer id, MateriaDTO materiaDTO)
                        throws MateriaExistsException, PensumNotFoundException, MateriaNotFoundException,
                        SemestrePensumNotFoundException {

                // Buscar la materia existente
                Materia materia = materiaRepository.findById(id)
                                .orElseThrow(() -> new MateriaNotFoundException(
                                                String.format(IS_NOT_FOUND_F, "LA MATERIA CON EL ID " + id)
                                                                .toLowerCase()));

                // Verificar código único si ha cambiado
                if (!materia.getCodigo().equals(materiaDTO.getCodigo())
                                && materiaRepository.existsByCodigo(materiaDTO.getCodigo())) {
                        throw new MateriaExistsException(String
                                        .format(IS_ALREADY_USE, "LA MATERIA CON EL CODIGO " + materiaDTO.getCodigo())
                                        .toLowerCase());
                }

                // Buscar el pensum
                Pensum pensum = pensumRepository.findById(materiaDTO.getPensumId())
                                .orElseThrow(() -> new PensumNotFoundException(
                                                String.format(IS_NOT_FOUND,
                                                                "EL PENSUM CON EL ID " + materiaDTO.getPensumId())
                                                                .toLowerCase()));

                // Verificar que el semestre esté dentro del rango permitido para el pensum
                if (pensum.getCantidadSemestres() == null ||
                                Integer.parseInt(materiaDTO.getSemestre()) > pensum.getCantidadSemestres() ||
                                Integer.parseInt(materiaDTO.getSemestre()) < 1) {
                        throw new SemestrePensumNotFoundException(
                                        String.format(IS_NOT_VALID,
                                                        "EL SEMESTRE " + materiaDTO.getSemestre()
                                                                        + " NO ESTÁ EN EL RANGO DEL PENSUM (1-"
                                                                        + pensum.getCantidadSemestres() + ")")
                                                        .toLowerCase());
                }

                // Buscar la relación entre el semestre y el pensum
                Optional<SemestrePensum> semestrePensumOpt = semestrePensumRepository
                                .findFirstByPensumIdAndSemestreId_Numero(pensum,
                                                Integer.parseInt(materiaDTO.getSemestre()));

                if (semestrePensumOpt.isEmpty()) {
                        throw new SemestrePensumNotFoundException(
                                        String.format(IS_NOT_FOUND, "LA RELACIÓN ENTRE EL PENSUM Y EL SEMESTRE "
                                                        + materiaDTO.getSemestre()).toLowerCase());
                }

                SemestrePensum semestrePensum = semestrePensumOpt.get();

                // Actualizar la materia
                BeanUtils.copyProperties(materiaDTO, materia);
                materia.setId(id); // Asegurar que el ID siga siendo el mismo
                materia.setPensumId(pensum);
                materia.setSemestrePensum(semestrePensum);

                materiaRepository.save(materia);

                // Mapear la materia actualizada a DTO para retornarla
                MateriaDTO materiaActualizada = new MateriaDTO();
                BeanUtils.copyProperties(materia, materiaActualizada);
                materiaActualizada.setPensumId(materia.getPensumId().getId());
                materiaActualizada.setSemestrePensumId(materia.getSemestrePensum().getId());
                return materiaActualizada;
        }

        public void vincularMoodleId(MoodleRequest moodleRequest)
                        throws MateriaNotFoundException, MateriaExistsException {
                Materia materia = materiaRepository.findById(moodleRequest.getBackendId()).orElse(null);
                if (materia == null) {
                        throw new MateriaNotFoundException(String
                                        .format(IS_NOT_FOUND_F, "EL MATERIA CON EL ID " + moodleRequest.getBackendId())
                                        .toLowerCase());
                }

                if (materiaRepository.existsByMoodleId(moodleRequest.getMoodleId())) {
                        throw new MateriaExistsException(
                                        String.format(IS_ALREADY_USE,
                                                        "LA MATERIA CON EL MOODLE ID " + moodleRequest.getMoodleId())
                                                        .toLowerCase());
                }
                materia.setMoodleId(moodleRequest.getMoodleId());
                materiaRepository.save(materia);
        }

        @Override
        public MateriaDTO listarMateria(Integer materiaId) throws MateriaNotFoundException {
                Materia materia = materiaRepository.findById(materiaId).orElse(null);
                if (materia == null) {
                        throw new MateriaNotFoundException(
                                        String.format(IS_NOT_FOUND_F, "EL MATERIA CON EL ID " + materiaId)
                                                        .toLowerCase());
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
                        throw new PensumNotFoundException(
                                        String.format(IS_NOT_FOUND, "EL PENSUM CON EL ID " + pensumId).toLowerCase());
                }

                List<Materia> materias = materiaRepository.findByPensumId(pensum).stream().toList();
                return materias.stream().map(materia -> {
                        MateriaDTO materiaDTO = new MateriaDTO();
                        BeanUtils.copyProperties(materia, materiaDTO);
                        materiaDTO.setPensumId(materia.getPensumId().getId());
                        materiaDTO.setSemestrePensumId(materia.getSemestrePensum().getId());
                        return materiaDTO;
                }).toList();
        }

        @Override
        public List<MateriaDTO> listarMateriasPorPensumPorSemestre(MateriaSemestreRequest materiaSemestreRequest)
                        throws PensumNotFoundException {

                Pensum pensum = pensumRepository.findById(materiaSemestreRequest.getPensumId()).orElse(null);
                if (pensum == null) {
                        throw new PensumNotFoundException(String
                                        .format(IS_NOT_FOUND,
                                                        "EL PENSUM CON EL ID " + materiaSemestreRequest.getPensumId())
                                        .toLowerCase());
                }

                List<Materia> materias = materiaRepository.findByPensumId(pensum).stream().toList();
                return materias.stream()
                                .filter(materia -> materia.getSemestre().equals(materiaSemestreRequest.getSemestre()))
                                .map(materia -> {
                                        MateriaDTO materiaDTO = new MateriaDTO();
                                        BeanUtils.copyProperties(materia, materiaDTO);
                                        materiaDTO.setPensumId(materia.getPensumId().getId());
                                        return materiaDTO;
                                }).toList();
        }

}
