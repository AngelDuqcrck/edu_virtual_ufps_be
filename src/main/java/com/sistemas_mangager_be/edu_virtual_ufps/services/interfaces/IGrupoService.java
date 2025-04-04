package com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces;

import java.util.List;

import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.CohorteNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.GrupoNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.MateriaNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.RoleNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.UserNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.GrupoDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.GrupoResponse;

public interface IGrupoService {
      public GrupoDTO crearGrupo(GrupoDTO grupoDTO)
            throws MateriaNotFoundException, CohorteNotFoundException, UserNotFoundException, RoleNotFoundException;

            public GrupoDTO actualizarGrupo(GrupoDTO grupoDTO, Integer id)
            throws MateriaNotFoundException, CohorteNotFoundException,
            UserNotFoundException, RoleNotFoundException, GrupoNotFoundException;

            public GrupoResponse listarGrupo(Integer id) throws GrupoNotFoundException;

            public List<GrupoResponse> listarGrupos();

            public List<GrupoResponse> listarGruposPorDocente(Integer docenteId)
            throws UserNotFoundException, RoleNotFoundException;

            public List<GrupoResponse> listarGruposPorCohorte(Integer cohorteId) throws CohorteNotFoundException;

            public List<GrupoResponse> listarGruposPorMateria(Integer materiaId) throws MateriaNotFoundException;

            public void desactivarGrupo(Integer id) throws GrupoNotFoundException;

            public void activarGrupo(Integer id) throws GrupoNotFoundException;
}
