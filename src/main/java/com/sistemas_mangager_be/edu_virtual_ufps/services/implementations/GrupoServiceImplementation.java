package com.sistemas_mangager_be.edu_virtual_ufps.services.implementations;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Cohorte;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.CohorteGrupo;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Grupo;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Materia;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Rol;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Usuario;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.CohorteNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.GrupoNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.MateriaNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.RoleNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.UserNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.CohorteGrupoRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.CohorteRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.GrupoRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.MateriaRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.RolRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.UsuarioRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.IGrupoService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.GrupoDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.GrupoResponse;


@Service
public class GrupoServiceImplementation implements IGrupoService {

    public static final String IS_ALREADY_USE = "%s ya esta en uso";
    public static final String IS_NOT_FOUND = "%s no fue encontrado";
    public static final String IS_NOT_FOUND_F = "%s no fue encontrada";
    public static final String IS_NOT_ALLOWED = "no esta permitido %s ";
    public static final String IS_NOT_VALID = "%s no es valido";
    public static final String ARE_NOT_EQUALS = "%s no son iguales";
    public static final String IS_NOT_CORRECT = "%s no es correcta";

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired
    private CohorteRepository cohorteRepository;

    @Autowired
    private CohorteGrupoRepository cohorteGrupoRepository;

    @Autowired
    private RolRepository rolRepository;

    @Override
    public GrupoDTO crearGrupo(GrupoDTO grupoDTO)
            throws MateriaNotFoundException, CohorteNotFoundException, UserNotFoundException, RoleNotFoundException {
        Grupo grupo = new Grupo();
        BeanUtils.copyProperties(grupoDTO, grupo);

        Materia materia = materiaRepository.findById(grupoDTO.getMateriaId()).orElse(null);
        if (materia == null) {
            throw new MateriaNotFoundException(
                    String.format(IS_NOT_FOUND_F, "LA MATERIA CON EL ID " + grupoDTO.getMateriaId()).toLowerCase());
        }

        CohorteGrupo cohorteGrupo = cohorteGrupoRepository.findById(grupoDTO.getCohorteId()).orElse(null);
        if (cohorteGrupo == null) {
            throw new CohorteNotFoundException(
                    String.format(IS_NOT_FOUND, "EL GRUPO DE LA COHORTE CON EL ID " + grupoDTO.getCohorteId())
                            .toLowerCase());
        }

        Usuario docente = usuarioRepository.findById(grupoDTO.getDocenteId()).orElse(null);
        if (docente == null) {
            throw new UserNotFoundException(
                    String.format(IS_NOT_FOUND, "EL USUARIO CON EL ID " + grupoDTO.getDocenteId()).toLowerCase());
        }

        if (docente.getRolId().getId() != 2) {
            throw new RoleNotFoundException("El usuario no tiene rol de docente");
        }

        grupo.setMateriaId(materia);
        grupo.setCohorteGrupoId(cohorteGrupo);
        grupo.setCohorteId(cohorteGrupo.getCohorteId());
        grupo.setDocenteId(docente);
        grupo.setActivo(true);

        grupoRepository.save(grupo);

        GrupoDTO grupoCreado = new GrupoDTO();
        BeanUtils.copyProperties(grupo, grupoCreado);
        grupoCreado.setMateriaId(grupo.getMateriaId().getId());
        grupoCreado.setCohorteId(grupo.getCohorteId().getId());
        grupoCreado.setDocenteId(grupo.getDocenteId().getId());
        return grupoCreado;

    }

    @Override
    public GrupoDTO actualizarGrupo(GrupoDTO grupoDTO, Integer id)
            throws MateriaNotFoundException, CohorteNotFoundException,
            UserNotFoundException, RoleNotFoundException, GrupoNotFoundException {

        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new GrupoNotFoundException(
                        String.format(IS_NOT_FOUND, "EL GRUPO CON ID " + id).toLowerCase()));

        BeanUtils.copyProperties(grupoDTO, grupo, "id", "activo"); // Excluimos id y activo de la copia

        Materia materia = materiaRepository.findById(grupoDTO.getMateriaId())
                .orElseThrow(() -> new MateriaNotFoundException(
                        String.format(IS_NOT_FOUND_F, "LA MATERIA CON ID " + grupoDTO.getMateriaId()).toLowerCase()));

        CohorteGrupo cohorteGrupo = cohorteGrupoRepository.findById(grupoDTO.getCohorteId())
                .orElseThrow(() -> new CohorteNotFoundException(
                        String.format(IS_NOT_FOUND, "EL GRUPO DE COHORTE CON ID " + grupoDTO.getCohorteId())
                                .toLowerCase()));

        Usuario docente = usuarioRepository.findById(grupoDTO.getDocenteId())
                .orElseThrow(() -> new UserNotFoundException(
                        String.format(IS_NOT_FOUND, "EL DOCENTE CON ID " + grupoDTO.getDocenteId()).toLowerCase()));

        if (docente.getRolId().getId() != 2) {
            throw new RoleNotFoundException("El usuario asignado no tiene rol de docente");
        }

        grupo.setMateriaId(materia);
        grupo.setCohorteGrupoId(cohorteGrupo);
        grupo.setCohorteId(cohorteGrupo.getCohorteId());
        grupo.setDocenteId(docente);

        grupoRepository.save(grupo);

        GrupoDTO grupoActualizado = new GrupoDTO();
        BeanUtils.copyProperties(grupo, grupoActualizado);
        grupoActualizado.setMateriaId(grupo.getMateriaId().getId());
        grupoActualizado.setCohorteId(grupo.getCohorteId().getId());
        grupoActualizado.setDocenteId(grupo.getDocenteId().getId());

        return grupoActualizado;
    }

    @Override
    public GrupoResponse listarGrupo(Integer id) throws GrupoNotFoundException {
        Grupo grupo = grupoRepository.findById(id).orElse(null);
        if (grupo == null) {
            throw new GrupoNotFoundException(String.format(IS_NOT_FOUND, "EL GRUPO CON EL ID " + id).toLowerCase());
        }

        GrupoResponse grupoResponse = new GrupoResponse();
        BeanUtils.copyProperties(grupo, grupoResponse);
        grupoResponse.setMateriaId(grupo.getMateriaId().getId());
        grupoResponse.setCohorteId(grupo.getCohorteId().getId());
        grupoResponse.setDocenteId(grupo.getDocenteId().getId());
        grupoResponse.setDocenteNombre(grupo.getDocenteId().getNombre());
        grupoResponse.setCohorteNombre(grupo.getCohorteId().getNombre());
        grupoResponse.setMateriaNombre(grupo.getMateriaId().getNombre());
        return grupoResponse;

    }

    @Override
    public List<GrupoResponse> listarGrupos() {
        List<Grupo> grupos = grupoRepository.findAll();
        return grupos.stream().map(grupo -> {
            GrupoResponse grupoResponse = new GrupoResponse();
            BeanUtils.copyProperties(grupo, grupoResponse);
            grupoResponse.setMateriaId(grupo.getMateriaId().getId());
            grupoResponse.setCohorteId(grupo.getCohorteId().getId());
            grupoResponse.setDocenteId(grupo.getDocenteId().getId());
            grupoResponse.setDocenteNombre(grupo.getDocenteId().getNombre());
            grupoResponse.setCohorteNombre(grupo.getCohorteId().getNombre());
            grupoResponse.setMateriaNombre(grupo.getMateriaId().getNombre());
            return grupoResponse;
        }).toList();

    }

    @Override
    public List<GrupoResponse> listarGruposPorDocente(Integer docenteId)
            throws UserNotFoundException, RoleNotFoundException {
        Usuario docente = usuarioRepository.findById(docenteId).orElse(null);

        if (docente == null) {
            throw new UserNotFoundException(
                    String.format(IS_NOT_FOUND, "EL USUARIO CON EL ID " + docenteId).toLowerCase());
        }

        if (docente.getRolId().getId() != 2) {
            throw new RoleNotFoundException("El usuario no tiene rol de docente");
        }

        List<Grupo> grupos = grupoRepository.findByDocenteId(docente);
        return grupos.stream().map(grupo -> {
            GrupoResponse grupoResponse = new GrupoResponse();
            BeanUtils.copyProperties(grupo, grupoResponse);
            grupoResponse.setMateriaId(grupo.getMateriaId().getId());
            grupoResponse.setCohorteId(grupo.getCohorteId().getId());
            grupoResponse.setDocenteId(grupo.getDocenteId().getId());
            grupoResponse.setDocenteNombre(grupo.getDocenteId().getNombre());
            grupoResponse.setCohorteNombre(grupo.getCohorteId().getNombre());
            grupoResponse.setMateriaNombre(grupo.getMateriaId().getNombre());
            return grupoResponse;
        }).toList();

    }

    @Override
    public List<GrupoResponse> listarGruposPorCohorte(Integer cohorteId) throws CohorteNotFoundException {

        Cohorte cohorte = cohorteRepository.findById(cohorteId).orElse(null);
        if (cohorte == null) {
            throw new CohorteNotFoundException(
                    String.format(IS_NOT_FOUND_F, "LA COHORTE CON EL ID " + cohorteId).toLowerCase());
        }
        List<Grupo> grupos = grupoRepository.findByCohorteId(cohorte);
        return grupos.stream().map(grupo -> {
            GrupoResponse grupoResponse = new GrupoResponse();
            BeanUtils.copyProperties(grupo, grupoResponse);
            grupoResponse.setMateriaId(grupo.getMateriaId().getId());
            grupoResponse.setCohorteId(grupo.getCohorteId().getId());
            grupoResponse.setDocenteId(grupo.getDocenteId().getId());
            grupoResponse.setDocenteNombre(grupo.getDocenteId().getNombre());
            grupoResponse.setCohorteNombre(grupo.getCohorteId().getNombre());
            grupoResponse.setMateriaNombre(grupo.getMateriaId().getNombre());
            return grupoResponse;
        }).toList();

    }

    @Override
    public List<GrupoResponse> listarGruposPorMateria(Integer materiaId) throws MateriaNotFoundException {
        Materia materia = materiaRepository.findById(materiaId).orElse(null);
        if (materia == null) {
            throw new MateriaNotFoundException(
                    String.format(IS_NOT_FOUND_F, "LA MATERIA CON EL ID " + materiaId).toLowerCase());
        }
        List<Grupo> grupos = grupoRepository.findByMateriaId(materia);
        return grupos.stream().map(grupo -> {
            GrupoResponse grupoResponse = new GrupoResponse();
            BeanUtils.copyProperties(grupo, grupoResponse);
            grupoResponse.setMateriaId(grupo.getMateriaId().getId());
            grupoResponse.setCohorteId(grupo.getCohorteId().getId());
            grupoResponse.setDocenteId(grupo.getDocenteId().getId());
            grupoResponse.setDocenteNombre(grupo.getDocenteId().getNombre());
            grupoResponse.setCohorteNombre(grupo.getCohorteId().getNombre());
            grupoResponse.setMateriaNombre(grupo.getMateriaId().getNombre());
            return grupoResponse;
        }).toList();

    }

    @Override
    public void activarGrupo(Integer id) throws GrupoNotFoundException {
        Grupo grupo = grupoRepository.findById(id).orElse(null);
        if (grupo == null) {
            throw new GrupoNotFoundException(
                    String.format(IS_NOT_FOUND, "EL GRUPO CON EL ID " + id).toLowerCase());
        }

        grupo.setActivo(true);
        grupoRepository.save(grupo);
    }

    @Override
    public void desactivarGrupo(Integer id) throws GrupoNotFoundException {
        Grupo grupo = grupoRepository.findById(id).orElse(null);
        if (grupo == null) {
            throw new GrupoNotFoundException(
                    String.format(IS_NOT_FOUND, "EL GRUPO CON EL ID " + id).toLowerCase());
        }

        grupo.setActivo(false);
        grupoRepository.save(grupo);
    }
}
