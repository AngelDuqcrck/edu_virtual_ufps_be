package com.sistemas_mangager_be.edu_virtual_ufps.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.TipoContraprestacion;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.ContraprestacionException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.EstudianteNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.TipoContraprestacionRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.services.implementations.PdfGeneratorService;
import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.IContraprestacionService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.ContraprestacionDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.CertificadoResponse;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.ContraprestacionResponse;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.HttpResponse;

@RestController
@RequestMapping("/contraprestaciones")
public class ContraprestacionController {
    
    @Autowired
    private IContraprestacionService  contraprestacionService;

    @Autowired
    private TipoContraprestacionRepository tipoContraprestacionRepository;

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @PostMapping("/crear")
    public ResponseEntity<HttpResponse> crearContraprestacion(@RequestBody ContraprestacionDTO contraprestacionDTO) throws ContraprestacionException, EstudianteNotFoundException{
        contraprestacionService.crearContraprestacion(contraprestacionDTO);
        return new ResponseEntity<>(
                                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                                                " Contraprestacion creada con exito"),
                                HttpStatus.OK);
    }

    @PutMapping("/actualizar/{idContraprestacion}")
    public ResponseEntity<HttpResponse> actualizarContraprestacion(@PathVariable Integer idContraprestacion, @RequestBody ContraprestacionDTO contraprestacionDTO) throws ContraprestacionException, EstudianteNotFoundException{
        contraprestacionService.actualizarContraprestacion(idContraprestacion, contraprestacionDTO);
        return new ResponseEntity<>(
                                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                                                " Contraprestacion actualizada con exito"),
                                HttpStatus.OK);
    }

    @GetMapping("/{idContraprestacion}")
    public ResponseEntity<ContraprestacionResponse> listarContraprestacion(@PathVariable Integer idContraprestacion) throws ContraprestacionException {
        ContraprestacionResponse contraprestacionResponse = contraprestacionService.listarContraprestacion(idContraprestacion);
        return new ResponseEntity<>(contraprestacionResponse, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ContraprestacionResponse>> listarContraprestaciones(){
        List<ContraprestacionResponse> contraprestaciones = contraprestacionService.listarContraprestaciones();
        return new ResponseEntity<>(contraprestaciones, HttpStatus.OK);
    }

    @GetMapping("/tipo/{tipoContraprestacionId}")
    public ResponseEntity<List<ContraprestacionResponse>> listarContraprestacionesPorTipoContraprestacion(@PathVariable Integer tipoContraprestacionId) throws ContraprestacionException {
        List<ContraprestacionResponse> contraprestaciones = contraprestacionService.listarContraprestacionesPorTipoContraprestacion(tipoContraprestacionId);
        return new ResponseEntity<>(contraprestaciones, HttpStatus.OK);
    }

    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<ContraprestacionResponse>> listarContraprestacionesPorEstudiante(@PathVariable Integer estudianteId) throws EstudianteNotFoundException {
        List<ContraprestacionResponse> contraprestaciones = contraprestacionService.listarContraprestacionesPorEstudiante(estudianteId);
        return new ResponseEntity<>(contraprestaciones, HttpStatus.OK);
    }

    @PostMapping("/aprobar/{idContraprestacion}")
    public ResponseEntity<HttpResponse> aprobarContraprestacion(@PathVariable Integer idContraprestacion, @RequestParam("informe") MultipartFile file) throws ContraprestacionException, IOException {
        contraprestacionService.aprobarContraprestacion(idContraprestacion, file);
        return new ResponseEntity<>(
                                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                                                " Contraprestacion aprobada con exito"),
                                HttpStatus.OK);
    }

    @GetMapping("/certificado/{idContraprestacion}")
    public ResponseEntity<CertificadoResponse> listarInformacionCertificado(@PathVariable Integer idContraprestacion) throws ContraprestacionException{
        CertificadoResponse certificadoResponse = contraprestacionService.listarInformacionCertificado(idContraprestacion); 
        return new ResponseEntity<>(certificadoResponse, HttpStatus.OK);
        
    }

    @PostMapping("/generar/certificado/{contraprestacionId}")
    public ResponseEntity<byte[]> generarCertificado(@PathVariable Integer contraprestacionId) 
            throws ContraprestacionException, IOException {
        
        // Generar certificado (el servicio maneja toda la lógica)
        byte[] pdfBytes = contraprestacionService.generarCertificado(contraprestacionId);
        
        // Obtener datos para el nombre del archivo
        CertificadoResponse certificado = contraprestacionService.listarInformacionCertificado(contraprestacionId);
        String nombreArchivo = "certificado_contraprestacion_" + certificado.getCodigoEstudiante() + ".pdf";
        
        // Configurar respuesta
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nombreArchivo)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/tipos")
    public ResponseEntity<List<TipoContraprestacion>> listarTiposContraprestacion() {
        List<TipoContraprestacion> tiposContraprestacion = tipoContraprestacionRepository.findAll();
        return new ResponseEntity<>(tiposContraprestacion, HttpStatus.OK);
    }
}
