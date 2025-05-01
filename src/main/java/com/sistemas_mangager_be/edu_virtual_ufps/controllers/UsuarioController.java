package com.sistemas_mangager_be.edu_virtual_ufps.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.RoleNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.UserExistException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.UserNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.services.implementations.UsuarioServiceImplementation;
import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.IUsuarioService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.requests.DocenteRequest;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.requests.LoginGoogleRequest;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.HttpResponse;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.UsuarioResponse;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    // @Autowired
    // private IUsuarioService iUsuarioService;

    @Autowired
    private UsuarioServiceImplementation iUsuarioService;

    @PostMapping("/google/login")
    public ResponseEntity<HttpResponse> loginGoogle(@RequestBody LoginGoogleRequest loginGoogleRequest) {
        iUsuarioService.registraroActualizarUsuarioGoogle(loginGoogleRequest);

        return new ResponseEntity<>(
                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                        " Usuario registrado con exito"),
                HttpStatus.OK);
    }

    @PostMapping("/profesores/crear")
    public ResponseEntity<HttpResponse> crearProfesor(@RequestBody DocenteRequest docenteRequest)
            throws UserExistException, RoleNotFoundException {
        iUsuarioService.crearProfesor(docenteRequest);

        return new ResponseEntity<>(
                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                        " Docente registrado con exito"),
                HttpStatus.OK);
    }

    @PutMapping("/profesores/{id}")
    public ResponseEntity<HttpResponse> actualizarProfesor(@RequestBody DocenteRequest docenteRequest,
            @PathVariable Integer id) throws UserExistException, RoleNotFoundException, UserNotFoundException {
        iUsuarioService.actualizarProfesor(docenteRequest, id);

        return new ResponseEntity<>(
                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                        " Docente actualizado con exito"),
                HttpStatus.OK);
    }

    @GetMapping("/rol/{rolId}")
    public ResponseEntity<List<UsuarioResponse>> listarUsuariosPorRol(@PathVariable Integer rolId)
            throws RoleNotFoundException {
        List<UsuarioResponse> usuarioResponse = iUsuarioService.listarUsuariosPorRol(rolId);
        return new ResponseEntity<>(usuarioResponse, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> listarUsuario(@PathVariable Integer id) throws UserNotFoundException {
        UsuarioResponse usuarioResponse = iUsuarioService.listarUsuario(id);
        return new ResponseEntity<>(usuarioResponse, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {
        List<UsuarioResponse> usuarioResponse = iUsuarioService.listarUsuarios();
        return new ResponseEntity<>(usuarioResponse, HttpStatus.OK);
    }
}
