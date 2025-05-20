package com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces;

import java.util.List;

import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.*;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.GrupoDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.requests.GrupoRequest;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.EstudianteGrupoResponse;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.GrupoCohorteDocenteResponse;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.GrupoCohorteResponse;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.GrupoResponse;

public interface IGrupoService {
      public GrupoDTO crearGrupo(GrupoDTO grupoDTO)
                  throws MateriaNotFoundException, CohorteNotFoundException, UserNotFoundException,
                  RoleNotFoundException;

      public GrupoDTO actualizarGrupo(GrupoDTO grupoDTO, Integer id)
                  throws MateriaNotFoundException, CohorteNotFoundException,
                  UserNotFoundException, RoleNotFoundException, GrupoNotFoundException;

      public GrupoResponse listarGrupo(Integer id) throws GrupoNotFoundException;

      public List<GrupoResponse> listarGrupos();

      public List<GrupoResponse> listarGruposPorMateria(Integer materiaId) throws MateriaNotFoundException;

      public void desactivarGrupo(Integer id) throws GrupoNotFoundException;

      public void activarGrupo(Integer id) throws GrupoNotFoundException;

      public GrupoCohorteDocenteResponse vincularCohorteDocente(GrupoRequest grupoRequest)
                  throws CohorteNotFoundException, GrupoNotFoundException, UserNotFoundException;
      public void vincularGrupoMoodle(Long id, String moodleId) throws GrupoNotFoundException, GrupoExistException;
      public void actualizarVinculacionCohorteDocente(Long vinculacionId, GrupoRequest grupoRequest)
                  throws CohorteNotFoundException, GrupoNotFoundException, UserNotFoundException,
                  VinculacionNotFoundException;

      public GrupoCohorteDocenteResponse listarGrupoCohorteDocente(Long id) throws VinculacionNotFoundException;

      public List<GrupoCohorteDocenteResponse> listarGrupoCohorteDocentes();

      public List<GrupoCohorteDocenteResponse> listarGruposPorCohorte (Integer cohorteId) throws CohorteNotFoundException;

      public List<GrupoCohorteDocenteResponse> listarGruposPorGrupo (Integer grupoId) throws GrupoNotFoundException;

      public List<GrupoCohorteDocenteResponse> listarGruposPorDocente (Integer docenteId) throws UserNotFoundException;

      public List<GrupoCohorteResponse> listarGruposPorCohorteGrupo (Integer cohorteGrupoId) throws CohorteNotFoundException;

      public EstudianteGrupoResponse listarEstudiantesPorGrupoCohorte(Long grupoCohorteId);

      public List<GrupoCohorteDocenteResponse> listarGruposPorPrograma (Integer programaId) throws ProgramaNotFoundException;

      public List<GrupoCohorteDocenteResponse> listarGruposCohortePorMateria(Integer materiaId) throws MateriaNotFoundException;
}
