package com.sistemas_mangager_be.edu_virtual_ufps.controllers;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


import com.sistemas_mangager_be.edu_virtual_ufps.security.JwtTokenGenerator;

import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.IAdminService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.AdminDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.requests.LoginRequest;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.AuthResponse;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.HttpResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenGenerator jwtTokenGenerator;

   
    @Autowired
    private IAdminService adminService;

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
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                            loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String accessToken = jwtTokenGenerator.generarToken(authentication);
            String refreshToken = jwtTokenGenerator.generarRefreshToken(authentication);

            return new ResponseEntity<>(new AuthResponse(accessToken, refreshToken), HttpStatus.OK);
        } catch (UsernameNotFoundException e) {
            if (e.getMessage().contains("La cuenta está inactiva")) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "La cuenta está inactiva. Contacte al administrador.",
                        e);
            }
            throw e;
        }

    }

    
    //@PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
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
}
