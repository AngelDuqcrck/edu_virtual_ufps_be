package com.sistemas_mangager_be.edu_virtual_ufps.services.implementations;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Pensum;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Programa;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Semestre;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.SemestrePensum;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.PensumExistException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.PensumNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.ProgramaNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.PensumRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.ProgramaRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.SemestrePensumRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.SemestreRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.IPensumService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.PensumDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.requests.MoodleRequest;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.PensumResponse;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.PensumSemestreResponse;

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

    @Autowired
    private SemestrePensumRepository semestrePensumRepository;

    @Autowired
    private SemestreRepository semestreRepository;

    @Override
    public PensumDTO crearPensum(PensumDTO pensumDTO) throws ProgramaNotFoundException {
        // Validar y crear el pensum base
        Programa programa = programaRepository.findById(pensumDTO.getProgramaId())
                .orElseThrow(() -> new ProgramaNotFoundException(
                        String.format(IS_NOT_FOUND_F, "EL PROGRAMA CON EL ID " + pensumDTO.getProgramaId())
                                .toLowerCase()));

        Pensum pensum = new Pensum();
        BeanUtils.copyProperties(pensumDTO, pensum);
        pensum.setProgramaId(programa);
        pensumRepository.save(pensum);

        // Crear los semestres asociados al pensum
        crearSemestresParaPensum(pensum, pensumDTO.getCantidadSemestres());

        // Retornar el DTO con la información
        PensumDTO pensumCreado = new PensumDTO();
        BeanUtils.copyProperties(pensum, pensumCreado);
        pensumCreado.setProgramaId(pensum.getProgramaId().getId());
        return pensumCreado;
    }

    @Override
    public PensumSemestreResponse listarPensum(Integer id) throws PensumNotFoundException {
        Pensum pensum = pensumRepository.findById(id)
                .orElseThrow(() -> new PensumNotFoundException(
                        String.format(IS_NOT_FOUND, "EL PENSUM CON EL ID " + id).toLowerCase()));

        return mapToPensumSemestreResponse(pensum);
    }

    @Override
    public PensumDTO actualizarPensum(PensumDTO pensumDTO, Integer id)
            throws PensumNotFoundException, ProgramaNotFoundException {

        Pensum pensum = pensumRepository.findById(id)
                .orElseThrow(() -> new PensumNotFoundException(
                        String.format(IS_NOT_FOUND, "EL PENSUM CON EL ID " + id).toLowerCase()));

        Programa programa = programaRepository.findById(pensumDTO.getProgramaId())
                .orElseThrow(() -> new ProgramaNotFoundException(
                        String.format(IS_NOT_FOUND_F, "EL PROGRAMA CON EL ID " + pensumDTO.getProgramaId())
                                .toLowerCase()));

        BeanUtils.copyProperties(pensumDTO, pensum);
        pensum.setProgramaId(programa);

        // Sincronizar semestres si cambió la cantidad
        if (pensumDTO.getCantidadSemestres() != pensum.getCantidadSemestres()) {
            sincronizarSemestresPensum(pensum, pensumDTO.getCantidadSemestres());
        }

        pensumRepository.save(pensum);

        PensumDTO pensumActualizado = new PensumDTO();
        BeanUtils.copyProperties(pensum, pensumActualizado);
        pensumActualizado.setProgramaId(pensum.getProgramaId().getId());
        return pensumActualizado;
    }

    @Override
    public List<PensumSemestreResponse> listarPensums() {
        List<Pensum> pensums = pensumRepository.findAll();
        return pensums.stream().map(this::mapToPensumSemestreResponse).toList();
    }

    @Override
    public List<PensumSemestreResponse> listarPensumsPorPrograma(Integer id) throws ProgramaNotFoundException {
        Programa programa = programaRepository.findById(id)
                .orElseThrow(() -> new ProgramaNotFoundException(
                        String.format(IS_NOT_FOUND_F, "EL PROGRAMA CON EL ID " + id).toLowerCase()));

        List<Pensum> pensums = pensumRepository.findByProgramaId(programa);
        return pensums.stream().map(this::mapToPensumSemestreResponse).toList();
    }

    public void vincularSemestreMoodleId(MoodleRequest moodleRequest)
            throws PensumNotFoundException {

        // Buscar el SemestrePensum por ID
        SemestrePensum semestrePensum = semestrePensumRepository.findById(moodleRequest.getBackendId())
                .orElseThrow(() -> new PensumNotFoundException(
                        String.format(IS_NOT_FOUND, "EL SEMESTRE DEL PENSUM CON ID " + moodleRequest.getBackendId())
                                .toLowerCase()));

        // Actualizar el moodleId
        semestrePensum.setMoodleId(moodleRequest.getMoodleId());

        // Guardar los cambios
        semestrePensumRepository.save(semestrePensum);
    }

    /**
     * Crea los registros de SemestrePensum según la cantidad de semestres
     * especificada.
     */
    private void crearSemestresParaPensum(Pensum pensum, int cantidadSemestres) {
        for (int i = 1; i <= cantidadSemestres; i++) {
            Semestre semestre = semestreRepository.findByNumero(i)
                    .orElseThrow(() -> new RuntimeException("Semestre no configurado en la base de datos"));

            SemestrePensum semestrePensum = SemestrePensum.builder()
                    .semestreId(semestre)
                    .pensumId(pensum)
                    .moodleId(null)
                    .programaId(pensum.getProgramaId())
                    .build();

            semestrePensumRepository.save(semestrePensum);
        }
    }

    /**
     * Sincroniza los semestres del pensum cuando cambia la cantidad.
     */
    private void sincronizarSemestresPensum(Pensum pensum, int nuevaCantidad) {
        List<SemestrePensum> semestresActuales = semestrePensumRepository.findByPensumId(pensum);

        // Eliminar semestres excedentes
        if (semestresActuales.size() > nuevaCantidad) {
            for (int i = nuevaCantidad; i < semestresActuales.size(); i++) {
                semestrePensumRepository.delete(semestresActuales.get(i));
            }
        }
        // Agregar semestres faltantes
        else if (semestresActuales.size() < nuevaCantidad) {
            for (int i = semestresActuales.size() + 1; i <= nuevaCantidad; i++) {
                Semestre semestre = semestreRepository.findByNumero(i)
                        .orElseThrow(() -> new RuntimeException("Semestre no configurado"));

                SemestrePensum nuevoSemestre = SemestrePensum.builder()
                        .semestreId(semestre)
                        .pensumId(pensum)
                        .moodleId(null)
                        .programaId(pensum.getProgramaId())
                        .build();

                semestrePensumRepository.save(nuevoSemestre);
            }
        }

        pensum.setCantidadSemestres(nuevaCantidad);
    }

    private PensumSemestreResponse mapToPensumSemestreResponse(Pensum pensum) {

        List<SemestrePensum> semestresPensum = semestrePensumRepository.findByPensumId(pensum);

        List<PensumSemestreResponse.SemestreResponse> semestreResponses = semestresPensum.stream()
                .map(semestrePensum -> {
                    return new PensumSemestreResponse.SemestreResponse().builder()
                            .id(semestrePensum.getId())
                            .nombre(semestrePensum.getSemestreId().getNombre())
                            .numero(semestrePensum.getSemestreId().getNumero())
                            .moodleId(semestrePensum.getMoodleId())
                            .build();
                })
                .toList();

        return PensumSemestreResponse.builder()
                .id(pensum.getId())
                .nombre(pensum.getNombre())
                .cantidadSemestres(pensum.getCantidadSemestres())
                .programaId(pensum.getProgramaId().getId())
                .programaNombre(pensum.getProgramaId().getNombre())
                .semestres(semestreResponses)
                .build();
    }
}
