package com.sistemas_mangager_be.edu_virtual_ufps.controllers;

import java.util.Map;
import org.springframework.http.HttpStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.GrupoNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.NotasException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.SemestreException;
import com.sistemas_mangager_be.edu_virtual_ufps.services.moodle.MoodleService;

import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.HttpResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/semestres")
public class SemestreController {


@Autowired
private MoodleService moodleService;
    /**
 * Termina el semestre de un programa y crea los registros históricos correspondientes
 * 
 * @param programaId ID del programa académico
 * @param semestre Semestre a terminar (formato "YYYY-I" o "YYYY-II")
 * @param authentication Información del usuario autenticado
 * @return Respuesta HTTP con el resultado de la operación
 */
@PostMapping("/terminar/{programaId}/{semestre}")
public ResponseEntity<HttpResponse> terminarSemestre(
        @PathVariable Integer programaId,
        @PathVariable String semestre,
        @RequestHeader(value = "X-Usuario") String usuario) {
    
    try {
        
        
        // Invocar al servicio para terminar el semestre
        moodleService.terminarSemestre(programaId, semestre, usuario);
        
        return new ResponseEntity<>(
            new HttpResponse(
                HttpStatus.OK.value(),
                HttpStatus.OK,
                HttpStatus.OK.getReasonPhrase(),
                "Semestre " + semestre + " terminado exitosamente para el programa ID: " + programaId
            ),
            HttpStatus.OK
        );
        
    } catch (SemestreException e) {
        return new ResponseEntity<>(
            new HttpResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST,
                "Error en el proceso de terminación de semestre",
                e.getMessage()
            ),
            HttpStatus.BAD_REQUEST
        );
        
    } catch (GrupoNotFoundException e) {
        return new ResponseEntity<>(
            new HttpResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND,
                "Grupo no encontrado",
                e.getMessage()
            ),
            HttpStatus.NOT_FOUND
        );
        
    } catch (NotasException e) {
        return new ResponseEntity<>(
            new HttpResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST,
                "Error al cerrar notas",
                e.getMessage()
            ),
            HttpStatus.BAD_REQUEST
        );
        
    } catch (Exception e) {
        return new ResponseEntity<>(
            new HttpResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor",
                "Ocurrió un error inesperado al terminar el semestre: " + e.getMessage()
            ),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
}