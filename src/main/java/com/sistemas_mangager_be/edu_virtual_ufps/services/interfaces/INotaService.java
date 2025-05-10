package com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces;

import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.*;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.requests.NotasPosgradoRequest;

public interface INotaService {
     void guardaroActualizarNotaPosgrado(NotasPosgradoRequest notasPosgradoRequest)
            throws MatriculaException, NotasException;
     void cerrarNotasGrupoPosgrado(Long grupoCohorteId, String usuario)
            throws GrupoNotFoundException, NotasException;
     void abrirNotasGrupoPosgrado(Long grupoCohorteId, String usuario) throws GrupoNotFoundException;

}
