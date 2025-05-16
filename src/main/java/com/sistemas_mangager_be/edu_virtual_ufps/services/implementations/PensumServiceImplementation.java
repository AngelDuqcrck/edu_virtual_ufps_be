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
import com.sistemas_mangager_be.edu_virtual_ufps.entities.SemestrePrograma;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.PensumExistException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.PensumNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.ProgramaNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.PensumRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.ProgramaRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.SemestrePensumRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.SemestreProgramaRepository;
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

    @Autowired
    private SemestreProgramaRepository semestreProgramaRepository;

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

        // Crear los semestres asociados al programa (si no existen)
        crearSemestresParaPrograma(programa, pensumDTO.getCantidadSemestres());

        // Crea los semestres asociados al pensum (si no existen)
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

        // Guardar el valor actual de cantidadSemestres antes de actualizar el pensum
        Integer cantidadSemestresAntigua = pensum.getCantidadSemestres();

        // Actualizar propiedades del pensum
        BeanUtils.copyProperties(pensumDTO, pensum);
        pensum.setProgramaId(programa);

        // Sincronizar semestres si cambió la cantidad
        if (cantidadSemestresAntigua == null || pensumDTO.getCantidadSemestres() != cantidadSemestresAntigua) {
            sincronizarSemestresPensum(pensum, pensumDTO.getCantidadSemestres());
        }

        // Guardar el pensum actualizado
        pensumRepository.save(pensum);

        // Mapear resultado a DTO y retornar
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

    public void vincularMoodleId(MoodleRequest moodleRequest)
            throws PensumNotFoundException {

        // Buscar el pensum por ID
        Pensum pensum = pensumRepository.findById(moodleRequest.getBackendId())
                .orElseThrow(() -> new PensumNotFoundException(
                        String.format(IS_NOT_FOUND, "EL PENSUM CON ID " + moodleRequest.getBackendId()).toLowerCase()));

        // Actualizar el moodleId
        pensum.setMoodleId(moodleRequest.getMoodleId());

        // Guardar los cambios
        pensumRepository.save(pensum);
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
            throws ProgramaNotFoundException {

        // Buscar el SemestrePensum por ID
        SemestrePrograma semestrePrograma = semestreProgramaRepository.findById(moodleRequest.getBackendId())
                .orElseThrow(() -> new ProgramaNotFoundException(
                        String.format(IS_NOT_FOUND, "EL SEMESTRE DEL PENSUM CON ID " + moodleRequest.getBackendId())
                                .toLowerCase()));

        // Actualizar el moodleId
        semestrePrograma.setMoodleId(moodleRequest.getMoodleId());

        // Guardar los cambios
        semestreProgramaRepository.save(semestrePrograma);
    }

    /**
     * Sincroniza los semestres del pensum cuando cambia la cantidad.
     */
    private void sincronizarSemestresPensum(Pensum pensum, Integer nuevaCantidad) {
        List<SemestrePensum> semestresActuales = semestrePensumRepository.findByPensumId(pensum);

        // Ordenamos los semestres por número para asegurar que eliminamos los correctos
        semestresActuales
                .sort((sp1, sp2) -> sp1.getSemestreId().getNumero().compareTo(sp2.getSemestreId().getNumero()));

        // Eliminar semestres excedentes (empezando por los de mayor número)
        if (semestresActuales.size() > nuevaCantidad) {
            for (int i = semestresActuales.size() - 1; i >= nuevaCantidad; i--) {
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

        // Actualizamos la cantidad de semestres en el pensum
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

    /**
     * Crea los registros de SemestrePrograma según la cantidad de semestres
     * especificada.
     */
    private void crearSemestresParaPrograma(Programa programa, int cantidadSemestres) {
        for (int i = 1; i <= cantidadSemestres; i++) {
            Semestre semestre = semestreRepository.findByNumero(i)
                    .orElseThrow(() -> new RuntimeException("Semestre no configurado en la base de datos"));

            // Verificar si ya existe la relación
            if (!semestreProgramaRepository.existsBySemestreAndPrograma(semestre, programa)) {
                SemestrePrograma semestrePrograma = SemestrePrograma.builder()
                        .semestre(semestre)
                        .programa(programa)
                        .moodleId(null) // Se actualizará luego con Moodle
                        .build();

                semestreProgramaRepository.save(semestrePrograma);
            }
        }
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
                    .moodleId(null) // Se actualizará luego con Moodle
                    .build();

            semestrePensumRepository.save(semestrePensum);
        }
    }
}
