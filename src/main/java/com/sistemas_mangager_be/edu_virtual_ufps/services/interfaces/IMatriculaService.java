package com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces;

import java.util.List;

import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.EstudianteNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.GrupoNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.MatriculaException;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.MatriculaDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.MatriculaResponse;

public interface IMatriculaService {

    public MatriculaDTO crearMatricula(MatriculaDTO matriculaDTO)
            throws EstudianteNotFoundException, GrupoNotFoundException, MatriculaException;

    public void anularMatricula(Long idMatricula) throws MatriculaException;

    public List<MatriculaResponse> listarMatriculasEnCursoPorEstudiante(Integer estudianteId) throws EstudianteNotFoundException;
}
