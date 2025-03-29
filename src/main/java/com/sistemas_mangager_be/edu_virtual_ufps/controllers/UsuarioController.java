package com.sistemas_mangager_be.edu_virtual_ufps.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.RoleNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.UserExistException;
import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.IUsuarioService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.requests.DocenteRequest;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.HttpResponse;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    
    @Autowired
    private IUsuarioService iUsuarioService;

    @PostMapping("/profesores/crear")
    public ResponseEntity<HttpResponse> crearProfesor(@RequestBody DocenteRequest docenteRequest) throws UserExistException, RoleNotFoundException{
        iUsuarioService.crearProfesor(docenteRequest);

        return new ResponseEntity<>(
                                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                                                " Docente registrado con exito"),
                                HttpStatus.OK);
    }
}
