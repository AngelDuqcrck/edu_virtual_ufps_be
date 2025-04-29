package com.sistemas_mangager_be.edu_virtual_ufps.services.implementations;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Contraprestacion;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Estudiante;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Soporte;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.TipoContraprestacion;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.ContraprestacionException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.EstudianteNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.ContraprestacionRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.EstudianteRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.TipoContraprestacionRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.IContraprestacionService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.ByteArrayMultipartFile;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.ContraprestacionDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.CertificadoResponse;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.ContraprestacionResponse;

@Service
public class ContraprestacionServiceImplementation implements IContraprestacionService {

        public static final String IS_ALREADY_USE = "%s ya esta en uso";
        public static final String IS_NOT_FOUND = "%s no fue encontrado";
        public static final String IS_NOT_FOUND_F = "%s no fue encontrada";
        public static final String IS_NOT_ALLOWED = "no esta permitido %s ";
        public static final String IS_NOT_VALID = "%s no es valido";
        public static final String ARE_NOT_EQUALS = "%s no son iguales";
        public static final String IS_NOT_CORRECT = "%s no es correcta";
        private static final String CONTRAPRESTACION_EXISTENTE = "El estudiante ya tiene una contraprestación activa para el semestre %s";

        @Autowired
        private PdfGeneratorService pdfGeneratorService;

        @Autowired
        private S3Service s3Service;

        @Autowired
        private EstudianteRepository estudianteRepository;
        @Autowired
        private ContraprestacionRepository contraprestacionRepository;

        @Autowired
        private TipoContraprestacionRepository tipoContraprestacionRepository;

        public void crearContraprestacion(ContraprestacionDTO contraprestacionDTO)
                        throws EstudianteNotFoundException, ContraprestacionException {

                Estudiante estudiante = estudianteRepository.findById(contraprestacionDTO.getEstudianteId())
                                .orElseThrow(() -> new EstudianteNotFoundException(
                                                String.format(IS_NOT_FOUND, "Estudiante con ID: "
                                                                + contraprestacionDTO.getEstudianteId())));

                TipoContraprestacion tipoContraprestacion = tipoContraprestacionRepository
                                .findById(contraprestacionDTO.getTipoContraprestacionId())
                                .orElseThrow(() -> new ContraprestacionException(
                                                String.format(IS_NOT_FOUND_F,
                                                                "Tipo de contraprestacion con ID: "
                                                                                + contraprestacionDTO
                                                                                                .getTipoContraprestacionId())
                                                                .toLowerCase()));

                // Validar si ya existe una contraprestación para este estudiante en el semestre
                String semestre = calcularSemestre(new Date());
                if (contraprestacionRepository.existsByEstudianteIdAndSemestre(estudiante, semestre)) {
                        throw new ContraprestacionException(
                                        String.format(CONTRAPRESTACION_EXISTENTE, semestre));
                }

                Contraprestacion contraprestacion = Contraprestacion.builder()
                                .aprobada(false)
                                .actividades(contraprestacionDTO.getActividades())
                                .fechaCreacion(new Date())
                                .semestre(semestre)
                                .fechaFin(contraprestacionDTO.getFechaFin())
                                .fechaInicio(contraprestacionDTO.getFechaInicio())
                                .estudianteId(estudiante)
                                .tipoContraprestacionId(tipoContraprestacion)
                                .certificadoGenerado(false)
                                .build();

                contraprestacionRepository.save(contraprestacion);
        }

        public void actualizarContraprestacion(Integer id, ContraprestacionDTO contraprestacionDTO)
                        throws EstudianteNotFoundException, ContraprestacionException {

                Contraprestacion contraprestacion = contraprestacionRepository.findById(id)
                                .orElseThrow(() -> new ContraprestacionException(
                                                String.format(IS_NOT_FOUND_F, "Contraprestación con ID: " + id)
                                                                .toLowerCase()));

                // Validar si se está cambiando el estudiante o el semestre
                boolean estudianteCambiado = !contraprestacion.getEstudianteId().getId()
                                .equals(contraprestacionDTO.getEstudianteId());
                boolean semestreCambiado = !contraprestacion.getSemestre().equals(calcularSemestre(new Date()));

                if (estudianteCambiado || semestreCambiado) {
                        Estudiante nuevoEstudiante = estudianteCambiado ? estudianteRepository
                                        .findById(contraprestacionDTO.getEstudianteId())
                                        .orElseThrow(() -> new EstudianteNotFoundException(
                                                        String.format(IS_NOT_FOUND,
                                                                        "Estudiante con ID: " + contraprestacionDTO
                                                                                        .getEstudianteId())))
                                        : contraprestacion.getEstudianteId();

                        String nuevoSemestre = semestreCambiado ? calcularSemestre(new Date())
                                        : contraprestacion.getSemestre();

                        // Validar si ya existe una contraprestación para el nuevo estudiante y semestre
                        if (contraprestacionRepository.existsByEstudianteIdAndSemestre(nuevoEstudiante,
                                        nuevoSemestre)) {
                                throw new ContraprestacionException(
                                                String.format(CONTRAPRESTACION_EXISTENTE, nuevoSemestre));
                        }

                        if (estudianteCambiado) {
                                contraprestacion.setEstudianteId(nuevoEstudiante);
                        }
                        if (semestreCambiado) {
                                contraprestacion.setSemestre(nuevoSemestre);
                        }
                }

                // Actualizar tipo de contraprestación si cambió
                if (!contraprestacion.getTipoContraprestacionId().getId()
                                .equals(contraprestacionDTO.getTipoContraprestacionId())) {
                        TipoContraprestacion tipoContraprestacion = tipoContraprestacionRepository
                                        .findById(contraprestacionDTO.getTipoContraprestacionId())
                                        .orElseThrow(() -> new ContraprestacionException(
                                                        String.format(IS_NOT_FOUND_F,
                                                                        "Tipo de contraprestación con ID: "
                                                                                        + contraprestacionDTO
                                                                                                        .getTipoContraprestacionId())
                                                                        .toLowerCase()));
                        contraprestacion.setTipoContraprestacionId(tipoContraprestacion);
                }

                contraprestacion.setActividades(contraprestacionDTO.getActividades());
                contraprestacion.setFechaInicio(contraprestacionDTO.getFechaInicio());
                contraprestacion.setFechaFin(contraprestacionDTO.getFechaFin());

                contraprestacionRepository.save(contraprestacion);
        }

        public ContraprestacionResponse listarContraprestacion(Integer idContraprestacion)
                        throws ContraprestacionException {
                Contraprestacion contraprestacion = contraprestacionRepository.findById(idContraprestacion)
                                .orElseThrow(() -> new ContraprestacionException(
                                                String.format(IS_NOT_FOUND_F,
                                                                "Contraprestacion con ID: " + idContraprestacion)
                                                                .toLowerCase()));
                return ContraprestacionResponse.builder()
                                .id(contraprestacion.getId())
                                .estudianteId(contraprestacion.getEstudianteId().getId())
                                .estudianteNombre(contraprestacion.getEstudianteId().getNombre() + " " +
                                                contraprestacion.getEstudianteId().getNombre2() + " " +
                                                contraprestacion.getEstudianteId().getApellido() + " " +
                                                contraprestacion.getEstudianteId().getApellido2())
                                .actividades(contraprestacion.getActividades())
                                .fechaCreacion(contraprestacion.getFechaCreacion())
                                .fechaInicio(contraprestacion.getFechaInicio())
                                .fechaFin(contraprestacion.getFechaFin())
                                .tipoContraprestacionId(contraprestacion.getTipoContraprestacionId().getId())
                                .tipoContraprestacionNombre(contraprestacion.getTipoContraprestacionId().getNombre())
                                .porcentajeContraprestacion(
                                                String.valueOf(contraprestacion.getTipoContraprestacionId()
                                                                .getPorcentaje()))
                                .aprobada(contraprestacion.getAprobada())
                                .semestre(contraprestacion.getSemestre())
                                .soporte(contraprestacion.getSoporteId())
                                .primerNombre(contraprestacion.getEstudianteId().getNombre())
                                .segundoNombre(contraprestacion.getEstudianteId().getNombre2())
                                .primerApellido(contraprestacion.getEstudianteId().getApellido())
                                .segundoApellido(contraprestacion.getEstudianteId().getApellido2())
                                .build();
        }

        public List<ContraprestacionResponse> listarContraprestaciones() {
                return contraprestacionRepository.findAll().stream().map(contraprestacion -> ContraprestacionResponse
                                .builder()
                                .id(contraprestacion.getId())
                                .estudianteId(contraprestacion.getEstudianteId().getId())
                                .estudianteNombre(contraprestacion.getEstudianteId().getNombre() + " " +
                                                contraprestacion.getEstudianteId().getNombre2() + " " +
                                                contraprestacion.getEstudianteId().getApellido() + " " +
                                                contraprestacion.getEstudianteId().getApellido2())
                                .actividades(contraprestacion.getActividades())
                                .fechaCreacion(contraprestacion.getFechaCreacion())
                                .fechaInicio(contraprestacion.getFechaInicio())
                                .fechaFin(contraprestacion.getFechaFin())
                                .tipoContraprestacionId(contraprestacion.getTipoContraprestacionId().getId())
                                .tipoContraprestacionNombre(contraprestacion.getTipoContraprestacionId().getNombre())
                                .porcentajeContraprestacion(
                                                String.valueOf(contraprestacion.getTipoContraprestacionId()
                                                                .getPorcentaje()))
                                .semestre(contraprestacion.getSemestre())
                                .build()).collect(Collectors.toList());
        }

        public List<ContraprestacionResponse> listarContraprestacionesPorEstudiante(Integer estudianteId)
                        throws EstudianteNotFoundException {
                Estudiante estudiante = estudianteRepository.findById(estudianteId)
                                .orElseThrow(() -> new EstudianteNotFoundException(
                                                String.format(IS_NOT_FOUND, "Estudiante con ID: " + estudianteId)));

                return contraprestacionRepository.findByEstudianteId(estudiante).stream()
                                .map(contraprestacion -> ContraprestacionResponse.builder()
                                                .id(contraprestacion.getId())
                                                .estudianteId(contraprestacion.getEstudianteId().getId())
                                                .estudianteNombre(contraprestacion.getEstudianteId().getNombre())
                                                .actividades(contraprestacion.getActividades())
                                                .fechaCreacion(contraprestacion.getFechaCreacion())
                                                .fechaInicio(contraprestacion.getFechaInicio())
                                                .fechaFin(contraprestacion.getFechaFin())
                                                .tipoContraprestacionId(
                                                                contraprestacion.getTipoContraprestacionId().getId())
                                                .tipoContraprestacionNombre(contraprestacion.getTipoContraprestacionId()
                                                                .getNombre())
                                                .porcentajeContraprestacion(
                                                                String.valueOf(contraprestacion
                                                                                .getTipoContraprestacionId()
                                                                                .getPorcentaje()))
                                                .build())
                                .collect(Collectors.toList());
        }

        public List<ContraprestacionResponse> listarContraprestacionesPorTipoContraprestacion(
                        Integer tipoContraprestacionId) throws ContraprestacionException {
                TipoContraprestacion tipoContraprestacion = tipoContraprestacionRepository
                                .findById(tipoContraprestacionId)
                                .orElseThrow(() -> new ContraprestacionException(
                                                String.format(IS_NOT_FOUND_F,
                                                                "Tipo de contraprestacion con ID: "
                                                                                + tipoContraprestacionId)
                                                                .toLowerCase()));

                return contraprestacionRepository.findByTipoContraprestacionId(tipoContraprestacion).stream()
                                .map(contraprestacion -> ContraprestacionResponse.builder()
                                                .id(contraprestacion.getId())
                                                .estudianteId(contraprestacion.getEstudianteId().getId())
                                                .estudianteNombre(contraprestacion.getEstudianteId().getNombre())
                                                .actividades(contraprestacion.getActividades())
                                                .fechaCreacion(contraprestacion.getFechaCreacion())
                                                .fechaInicio(contraprestacion.getFechaInicio())
                                                .fechaFin(contraprestacion.getFechaFin())
                                                .tipoContraprestacionId(
                                                                contraprestacion.getTipoContraprestacionId().getId())
                                                .tipoContraprestacionNombre(contraprestacion.getTipoContraprestacionId()
                                                                .getNombre())
                                                .porcentajeContraprestacion(
                                                                String.valueOf(contraprestacion
                                                                                .getTipoContraprestacionId()
                                                                                .getPorcentaje()))
                                                .build())
                                .collect(Collectors.toList());
        }

        public void aprobarContraprestacion(Integer id, MultipartFile informeFinal)
                        throws ContraprestacionException, IOException {

                Contraprestacion contraprestacion = contraprestacionRepository.findById(id)
                                .orElseThrow(() -> new ContraprestacionException(
                                                String.format(IS_NOT_FOUND_F, "Contraprestación con ID: " + id)
                                                                .toLowerCase()));

                if (informeFinal == null || informeFinal.isEmpty()) {
                        throw new ContraprestacionException(
                                        "El informe final es requerido para aprobar la contraprestación");
                }

                if (!informeFinal.getContentType().equals("application/pdf") &&
                                !informeFinal.getContentType().equals(
                                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                        throw new ContraprestacionException("Solo se permiten archivos PDF o DOCX");
                }

                Soporte soporte = s3Service.uploadFile(informeFinal, "contraprestaciones");

                // 4. Actualizar la contraprestación
                contraprestacion.setFechaFin(new Date());
                contraprestacion.setAprobada(true);
                contraprestacion.setSoporteId(soporte); // Asociar el soporte subido

                contraprestacionRepository.save(contraprestacion);
        }

        public CertificadoResponse listarInformacionCertificado(Integer contraprestacionId)
                        throws ContraprestacionException {
                Contraprestacion contraprestacion = contraprestacionRepository.findById(contraprestacionId)
                                .orElseThrow(() -> new ContraprestacionException(
                                                String.format(IS_NOT_FOUND_F,
                                                                "La contraprestación con ID: " + contraprestacionId)));

                return CertificadoResponse.builder()
                                .id(contraprestacion.getId())
                                .nombreCompleto(contraprestacion.getEstudianteId().getNombre() + " " +
                                                contraprestacion.getEstudianteId().getNombre2() + " " +
                                                contraprestacion.getEstudianteId().getApellido() + " " +
                                                contraprestacion.getEstudianteId().getApellido2())
                                .cedula(contraprestacion.getEstudianteId().getCedula())
                                .programa(contraprestacion.getEstudianteId().getProgramaId().getNombre())
                                .programaId(contraprestacion.getEstudianteId().getProgramaId().getId())
                                .cohorteId(contraprestacion.getEstudianteId().getCohorteId().getId())
                                .cohorteNombre(contraprestacion.getEstudianteId().getCohorteId().getNombre())
                                .fechaCreacion(contraprestacion.getFechaCreacion())
                                .semestre(contraprestacion.getSemestre())
                                .actividades(contraprestacion.getActividades())
                                .fechaInicio(contraprestacion.getFechaInicio())
                                .fechaFin(contraprestacion.getFechaFin())
                                .fechaCertificado(new Date())
                                .aprobada(contraprestacion.getAprobada())
                                .codigoEstudiante(contraprestacion.getEstudianteId().getCodigo())
                                .build();
        }

        public byte[] generarCertificado(Integer contraprestacionId) throws ContraprestacionException, IOException {
                // 1. Validar y obtener datos del certificado
                CertificadoResponse certificado = validarYGenerarDatosCertificado(contraprestacionId);

                // 2. Generar PDF
                byte[] pdfBytes = pdfGeneratorService.generateCertificadoPdf(certificado);

                // 3. Subir a S3 y guardar metadata
                guardarCertificadoEnS3(contraprestacionId, pdfBytes, certificado.getCodigoEstudiante());

                return pdfBytes;
        }

        private CertificadoResponse validarYGenerarDatosCertificado(Integer contraprestacionId)
                        throws ContraprestacionException {
                Contraprestacion contraprestacion = contraprestacionRepository.findById(contraprestacionId)
                                .orElseThrow(() -> new ContraprestacionException(
                                                String.format(IS_NOT_FOUND_F,
                                                                "La contraprestación con ID: " + contraprestacionId)));

                if (!contraprestacion.getAprobada()) {
                        throw new ContraprestacionException("La contraprestación no ha sido aprobada");
                }

                return listarInformacionCertificado(contraprestacionId);
        }

        private void guardarCertificadoEnS3(Integer contraprestacionId, byte[] pdfBytes, String codigoEstudiante)
                        throws IOException {
                // Configurar nombre del archivo
                String nombreArchivo = "certificado_contraprestacion_" + codigoEstudiante + "_"
                                + System.currentTimeMillis() + ".pdf";

                // Convertir byte[] a MultipartFile
                MultipartFile multipartFile = new ByteArrayMultipartFile(
                                nombreArchivo,
                                nombreArchivo,
                                "application/pdf",
                                pdfBytes);

                // Subir a S3
                Soporte soporte = s3Service.uploadFile(multipartFile, "certificados");

                // Actualizar contraprestación
                Contraprestacion contraprestacion = contraprestacionRepository.findById(contraprestacionId)
                                .orElse(null);
                if (contraprestacion != null) {
                        contraprestacion.setCertificadoGenerado(true);
                        contraprestacion.setFechaCertificado(new Date());
                        contraprestacion.setCertificadoId(soporte);
                        contraprestacionRepository.save(contraprestacion);
                }
        }

        private String calcularSemestre(Date fechaMatriculacion) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(fechaMatriculacion);

                int mes = cal.get(Calendar.MONTH) + 1; // Enero = 0
                int anio = cal.get(Calendar.YEAR);

                return anio + "-" + (mes <= 6 ? "I" : "II");
        }
}
