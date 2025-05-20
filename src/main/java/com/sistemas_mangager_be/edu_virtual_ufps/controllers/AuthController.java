package com.sistemas_mangager_be.edu_virtual_ufps.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.ChangeNotAllowedException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.UserNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.security.JwtTokenGenerator;

import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.IAdminService;
import com.sistemas_mangager_be.edu_virtual_ufps.services.moodle.MoodleService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.AdminDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.requests.LoginRequest;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.AuthResponse;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.HttpResponse;

import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.GrupoNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.NotasException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.SemestreException;
import com.sistemas_mangager_be.edu_virtual_ufps.services.moodle.MoodleService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

        @Autowired
        private AuthenticationManager authenticationManager;

        @Autowired
        private JwtTokenGenerator jwtTokenGenerator;

        @Autowired
        private IAdminService adminService;

        @Autowired
        private MoodleService moodleService;

        /*
         * Autentica a un usuario y genera un token de acceso y un token de refresco
         * 
         * @param loginRequest objeto que contiene el email y la contraseña del usuario
         * 
         * @return objeto que contiene el token de acceso y el token de refresco si se
         * logueo correctamente
         */
        @PostMapping("/login")
        public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
                // Aquí el manejo de excepciones ocurre automáticamente
                // AuthenticationManager delegará en CustomAuthenticationProvider
                // y los errores serán manejados por JwtAuthenticationEntryPoint
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                loginRequest.getEmail(),
                                                loginRequest.getPassword()));

                SecurityContextHolder.getContext().setAuthentication(authentication);

                String accessToken = jwtTokenGenerator.generarToken(authentication);
                String refreshToken = jwtTokenGenerator.generarRefreshToken(authentication);

                return new ResponseEntity<>(new AuthResponse(accessToken, refreshToken), HttpStatus.OK);
        }

        // Mantener el resto del código igual...
        @PostMapping("/register")
        public ResponseEntity<HttpResponse> registrarUsuario(@RequestBody AdminDTO adminDTO) {
                adminService.registrarAdmin(adminDTO);
                return new ResponseEntity<>(
                                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                                                " Administrador registrado con exito"),
                                HttpStatus.OK);
        }

        @GetMapping("/admins")
        public ResponseEntity<List<AdminDTO>> listarAdmins() {
                List<AdminDTO> adminDTO = adminService.listarAdmins();
                return new ResponseEntity<>(adminDTO, HttpStatus.OK);
        }

        @PutMapping("/admins/{id}")
        public ResponseEntity<HttpResponse> actualizarAdmin(@PathVariable Integer id, @RequestBody AdminDTO adminDTO)
                        throws UserNotFoundException {
                adminService.actualizarAdmin(id, adminDTO);
                return new ResponseEntity<>(
                                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                                                " Administrador actualizado con exito"),
                                HttpStatus.OK);
        }

        @PostMapping("/admins/{id}/desactivar")
        public ResponseEntity<HttpResponse> desactivarAdmin(@PathVariable Integer id)
                        throws UserNotFoundException, ChangeNotAllowedException {
                adminService.desactivarAdmin(id);
                return new ResponseEntity<>(
                                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                                                " Administrador desactivado con exito"),
                                HttpStatus.OK);
        }

        @PostMapping("/admins/{id}/activar")
        public ResponseEntity<HttpResponse> activarAdmin(@PathVariable Integer id)
                        throws UserNotFoundException, ChangeNotAllowedException {
                adminService.activarAdmin(id);
                return new ResponseEntity<>(
                                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                                                " Administrador activado con exito"),
                                HttpStatus.OK);
        }

        /**
 * Termina el semestre de un programa y crea los registros históricos correspondientes
 * 
 * @param programaId ID del programa académico
 * @param semestre Semestre a terminar (formato "YYYY-I" o "YYYY-II")
 * @param authentication Información del usuario autenticado
 * @return Respuesta HTTP con el resultado de la operación
 */
@PostMapping("/terminar-semestre/{programaId}/{semestre}")
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