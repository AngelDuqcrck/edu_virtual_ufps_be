package com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Solicitud;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.EstudianteNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.SolicitudException;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.SolicitudDTO;

public interface ISolicitudService {

    public Solicitud crearSolicitud(SolicitudDTO solicitudDTO, Integer tipoSolicitudId)
            throws SolicitudException, EstudianteNotFoundException;

    public Solicitud actualizarSolicitud(Long solicitudId, Integer tipoSolicitudId, SolicitudDTO solicitudDTO)
            throws SolicitudException, EstudianteNotFoundException;
}
