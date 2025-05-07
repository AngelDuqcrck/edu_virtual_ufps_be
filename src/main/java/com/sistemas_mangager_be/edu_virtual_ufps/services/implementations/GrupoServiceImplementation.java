package com.sistemas_mangager_be.edu_virtual_ufps.services.implementations;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Cohorte;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.CohorteGrupo;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.EstadoMatricula;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Estudiante;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Grupo;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.GrupoCohorte;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Materia;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Matricula;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Rol;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Usuario;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.CohorteNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.GrupoExistException;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.GrupoCohorteRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.GrupoNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.MateriaNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.RoleNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.UserNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.VinculacionNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.CohorteGrupoRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.CohorteRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.EstadoMatriculaRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.GrupoRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.MateriaRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.MatriculaRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.RolRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.UsuarioRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.IGrupoService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.GrupoDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.requests.GrupoRequest;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.requests.MoodleRequest;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.EstudianteGrupoResponse;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.GrupoCohorteDocenteResponse;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.GrupoCohorteResponse;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.GrupoResponse;

@Service
public class GrupoServiceImplementation implements IGrupoService {

    public static final String IS_ALREADY_USE = "%s ya esta en uso";
    public static final String IS_NOT_FOUND = "%s no fue encontrado";
    public static final String IS_NOT_FOUND_F = "%s no fue encontrada";
    public static final String IS_NOT_ALLOWED = "no esta permitido %s ";
    public static final String IS_NOT_VALID = "%s no es valido";
    public static final String ARE_NOT_EQUALS = "%s no son iguales";
    public static final String IS_NOT_CORRECT = "%s no es un docente";

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
    private GrupoCohorteRepository grupoCohorteRepository;

    @Autowired
    private MatriculaRepository matriculaRepository;

    @Autowired
    private EstadoMatriculaRepository estadoMatriculaRepository;

    @Override
    public GrupoDTO crearGrupo(GrupoDTO grupoDTO)
            throws MateriaNotFoundException, CohorteNotFoundException, UserNotFoundException, RoleNotFoundException {

        // 1. Obtener la materia
        Materia materia = materiaRepository.findById(grupoDTO.getMateriaId())
                .orElseThrow(() -> new MateriaNotFoundException(
                        String.format(IS_NOT_FOUND_F, "LA MATERIA CON EL ID " + grupoDTO.getMateriaId())
                                .toLowerCase()));

        // 2. Contar grupos existentes para esta materia
        long cantidadGrupos = grupoRepository.countByMateriaId(materia);

        // 3. Generar letra consecutiva (A, B, C,...)
        char letraGrupo = (char) ('A' + cantidadGrupos);

        // 4. Crear el nombre y código del grupo
        String nombreGrupo = materia.getNombre() + " - Grupo " + letraGrupo;
        String codigoGrupo = materia.getCodigo() + "-" + letraGrupo;

        // 5. Crear y guardar el grupo
        Grupo grupo = Grupo.builder()
                .nombre(nombreGrupo)
                .codigo(codigoGrupo)
                .activo(true)
                .materiaId(materia)
                .build();

        grupoRepository.save(grupo);

        GrupoDTO grupoCreado = new GrupoDTO();
        BeanUtils.copyProperties(grupo, grupoCreado);
        grupoCreado.setMateriaId(grupo.getMateriaId().getId());
        return grupoCreado;

    }

    @Override
    public GrupoDTO actualizarGrupo(GrupoDTO grupoDTO, Integer id)
            throws MateriaNotFoundException, CohorteNotFoundException,
            UserNotFoundException, RoleNotFoundException, GrupoNotFoundException {

        // 1. Buscar el grupo existente
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new GrupoNotFoundException(
                        String.format(IS_NOT_FOUND, "EL GRUPO CON ID " + id).toLowerCase()));

        // 2. Verificar si se cambió la materia
        if (!grupo.getMateriaId().getId().equals(grupoDTO.getMateriaId())) {
            Materia nuevaMateria = materiaRepository.findById(grupoDTO.getMateriaId())
                    .orElseThrow(() -> new MateriaNotFoundException(
                            String.format(IS_NOT_FOUND_F, "LA MATERIA CON EL ID " + grupoDTO.getMateriaId())
                                    .toLowerCase()));

            // 3. Si cambió la materia, regenerar nombre y código
            long cantidadGrupos = grupoRepository.countByMateriaId(nuevaMateria);
            char letraGrupo = (char) ('A' + cantidadGrupos);

            grupo.setNombre(nuevaMateria.getNombre() + " - Grupo " + letraGrupo);
            grupo.setCodigo(nuevaMateria.getCodigo() + "-" + letraGrupo);
            grupo.setMateriaId(nuevaMateria);
        }

        // 5. Guardar los cambios
        grupoRepository.save(grupo);

        // 6. Retornar el DTO actualizado
        GrupoDTO grupoActualizado = new GrupoDTO();
        BeanUtils.copyProperties(grupo, grupoActualizado);
        grupoActualizado.setMateriaId(grupo.getMateriaId().getId());
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

    public GrupoCohorteDocenteResponse vincularCohorteDocente(GrupoRequest grupoRequest)
            throws CohorteNotFoundException, GrupoNotFoundException, UserNotFoundException {

        Grupo grupo = grupoRepository.findById(grupoRequest.getGrupoId()).orElse(null);
        if (grupo == null) {
            throw new GrupoNotFoundException(
                    String.format(IS_NOT_FOUND, "EL GRUPO CON EL ID " + grupoRequest.getGrupoId()).toLowerCase());
        }

        CohorteGrupo cohorteGrupo = cohorteGrupoRepository.findById(grupoRequest.getCohorteGrupoId()).orElse(null);
        if (cohorteGrupo == null) {
            throw new CohorteNotFoundException(
                    String.format(IS_NOT_FOUND_F, "LA COHORTE CON EL ID " + grupoRequest.getCohorteGrupoId())
                            .toLowerCase());
        }

        Usuario usuario = usuarioRepository.findById(grupoRequest.getDocenteId()).orElse(null);
        if (usuario == null) {
            throw new UserNotFoundException(
                    String.format(IS_NOT_FOUND, "EL USUARIO CON EL ID " + grupoRequest.getDocenteId()).toLowerCase());
        }

        if (usuario.getRolId().getId() != 2) {
            throw new UserNotFoundException(
                    String.format(IS_NOT_CORRECT, "EL USUARIO CON EL ID " + grupoRequest.getDocenteId()).toLowerCase());
        }

        GrupoCohorte grupoCohorte = new GrupoCohorte();
        grupoCohorte.setCohorteId(cohorteGrupo.getCohorteId());
        grupoCohorte.setCohorteGrupoId(cohorteGrupo);
        grupoCohorte.setDocenteId(usuario);
        grupoCohorte.setGrupoId(grupo);
        grupoCohorte.setFechaCreacion(new Date());

        grupoCohorteRepository.save(grupoCohorte);

        GrupoCohorteDocenteResponse grupoCohorteDocenteResponse = new GrupoCohorteDocenteResponse().builder()
                .id(grupoCohorte.getId())
                .grupoCohorteId(grupoCohorte.getId())
                .grupoId(grupoCohorte.getGrupoId().getId())
                .cohorteGrupoId(grupoCohorte.getCohorteGrupoId().getId())
                .docenteId(grupoCohorte.getDocenteId().getId())
                .docenteNombre(grupoCohorte.getDocenteId().getNombreCompleto())
                .cohorteGrupoNombre(grupoCohorte.getCohorteGrupoId().getNombre())
                .cohorteId(grupoCohorte.getCohorteId().getId())
                .cohorteNombre(grupoCohorte.getCohorteId().getNombre())
                .fechaCreacion(grupoCohorte.getFechaCreacion().toString())
                .grupoNombre(grupoCohorte.getGrupoId().getNombre())
                .codigoGrupo(grupoCohorte.getGrupoId().getCodigo())
                .materia(grupoCohorte.getGrupoId().getMateriaId().getNombre())
                .codigoMateria(grupoCohorte.getGrupoId().getMateriaId().getCodigo())
                .semestreMateria(grupoCohorte.getGrupoId().getMateriaId().getSemestre())
                .moodleId(grupoCohorte.getMoodleId())
                .build();

        return grupoCohorteDocenteResponse;
    }

    public void vincularGrupoMoodle(Long id, String moodleId) throws GrupoNotFoundException, GrupoExistException {

        GrupoCohorte grupoCohorte = grupoCohorteRepository.findById(id).orElse(null);
        if (grupoCohorte == null) {
            throw new GrupoNotFoundException(
                    String.format(IS_NOT_FOUND, "EL GRUPO COHORTE CON EL ID " + id).toLowerCase());
        }
        if(grupoCohorteRepository.existsByMoodleId(moodleId)){
            throw new GrupoExistException(
                    String.format(IS_ALREADY_USE, "EL ID MOODLE " + moodleId).toLowerCase());
        }
        grupoCohorte.setMoodleId(moodleId);
        grupoCohorteRepository.save(grupoCohorte);
        
    }

    public void actualizarVinculacionCohorteDocente(Long vinculacionId, GrupoRequest grupoRequest)
            throws CohorteNotFoundException, GrupoNotFoundException, UserNotFoundException,
            VinculacionNotFoundException {

        // 1. Buscar la vinculación existente
        GrupoCohorte grupoCohorte = grupoCohorteRepository.findById(vinculacionId)
                .orElseThrow(() -> new VinculacionNotFoundException(
                        String.format(IS_NOT_FOUND, "LA VINCULACION CON ID " + vinculacionId).toLowerCase()));

        // 2. Validar y actualizar grupo si es diferente
        if (grupoRequest.getGrupoId() != null && !grupoRequest.getGrupoId().equals(grupoCohorte.getGrupoId().getId())) {
            Grupo grupo = grupoRepository.findById(grupoRequest.getGrupoId())
                    .orElseThrow(() -> new GrupoNotFoundException(
                            String.format(IS_NOT_FOUND, "EL GRUPO CON ID " + grupoRequest.getGrupoId()).toLowerCase()));
            grupoCohorte.setGrupoId(grupo);
        }

        // 3. Validar y actualizar cohorte grupo si es diferente
        if (grupoRequest.getCohorteGrupoId() != null
                && !grupoRequest.getCohorteGrupoId().equals(grupoCohorte.getCohorteGrupoId().getId())) {
            CohorteGrupo cohorteGrupo = cohorteGrupoRepository.findById(grupoRequest.getCohorteGrupoId())
                    .orElseThrow(() -> new CohorteNotFoundException(
                            String.format(IS_NOT_FOUND_F, "LA COHORTE CON ID " + grupoRequest.getCohorteGrupoId())
                                    .toLowerCase()));
            grupoCohorte.setCohorteGrupoId(cohorteGrupo);
            grupoCohorte.setCohorteId(cohorteGrupo.getCohorteId());
        }

        // 4. Validar y actualizar docente si es diferente
        if (grupoRequest.getDocenteId() != null
                && !grupoRequest.getDocenteId().equals(grupoCohorte.getDocenteId().getId())) {
            Usuario docente = usuarioRepository.findById(grupoRequest.getDocenteId())
                    .orElseThrow(() -> new UserNotFoundException(
                            String.format(IS_NOT_FOUND, "EL DOCENTE CON ID " + grupoRequest.getDocenteId())
                                    .toLowerCase()));

            if (docente.getRolId().getId() != 2) {
                throw new UserNotFoundException(
                        String.format(IS_NOT_CORRECT, "EL USUARIO CON ID " + grupoRequest.getDocenteId())
                                .toLowerCase());
            }
            grupoCohorte.setDocenteId(docente);
        }

        // 5. Actualizar fecha de modificación
        grupoCohorte.setFechaCreacion(new Date());

        // 6. Guardar cambios
        grupoCohorteRepository.save(grupoCohorte);
    }

    public GrupoCohorteDocenteResponse listarGrupoCohorteDocente(Long id) throws VinculacionNotFoundException {
        GrupoCohorte grupoCohorteDocente = grupoCohorteRepository.findById(id)
                .orElseThrow(() -> new VinculacionNotFoundException(
                        String.format(IS_NOT_FOUND, "EL GRUPO COHORTE DOCENTE CON ID " + id).toLowerCase()));

        GrupoCohorteDocenteResponse grupoCohorteDocenteResponse = new GrupoCohorteDocenteResponse().builder()
                .id(grupoCohorteDocente.getId())
                .grupoCohorteId(grupoCohorteDocente.getId())
                .grupoId(grupoCohorteDocente.getGrupoId().getId())
                .cohorteGrupoId(grupoCohorteDocente.getCohorteGrupoId().getId())
                .docenteId(grupoCohorteDocente.getDocenteId().getId())
                .docenteNombre(grupoCohorteDocente.getDocenteId().getNombreCompleto())
                .cohorteGrupoNombre(grupoCohorteDocente.getCohorteGrupoId().getNombre())
                .cohorteId(grupoCohorteDocente.getCohorteId().getId())
                .cohorteNombre(grupoCohorteDocente.getCohorteId().getNombre())
                .fechaCreacion(grupoCohorteDocente.getFechaCreacion().toString())
                .grupoNombre(grupoCohorteDocente.getGrupoId().getNombre())
                .codigoGrupo(grupoCohorteDocente.getGrupoId().getCodigo())
                .materia(grupoCohorteDocente.getGrupoId().getMateriaId().getNombre())
                .codigoMateria(grupoCohorteDocente.getGrupoId().getMateriaId().getCodigo())
                .semestreMateria(grupoCohorteDocente.getGrupoId().getMateriaId().getSemestre())
                .moodleId(grupoCohorteDocente.getMoodleId())
                .build();

        return grupoCohorteDocenteResponse;
    }

    public List<GrupoCohorteDocenteResponse> listarGrupoCohorteDocentes() {
        List<GrupoCohorte> grupoCohorteDocentes = grupoCohorteRepository.findAll();
        return grupoCohorteDocentes.stream().map(grupoCohorteDocente -> {
            GrupoCohorteDocenteResponse grupoCohorteDocenteResponse = new GrupoCohorteDocenteResponse().builder()
                    .id(grupoCohorteDocente.getId())
                    .grupoCohorteId(grupoCohorteDocente.getId())
                    .grupoId(grupoCohorteDocente.getGrupoId().getId())
                    .cohorteGrupoId(grupoCohorteDocente.getCohorteGrupoId().getId())
                    .docenteId(grupoCohorteDocente.getDocenteId().getId())
                    .docenteNombre(grupoCohorteDocente.getDocenteId().getNombreCompleto())
                    .cohorteGrupoNombre(grupoCohorteDocente.getCohorteGrupoId().getNombre())
                    .cohorteId(grupoCohorteDocente.getCohorteId().getId())
                    .cohorteNombre(grupoCohorteDocente.getCohorteId().getNombre())
                    .fechaCreacion(grupoCohorteDocente.getFechaCreacion().toString())
                    .grupoNombre(grupoCohorteDocente.getGrupoId().getNombre())
                    .codigoGrupo(grupoCohorteDocente.getGrupoId().getCodigo())
                    .materia(grupoCohorteDocente.getGrupoId().getMateriaId().getNombre())
                    .codigoMateria(grupoCohorteDocente.getGrupoId().getMateriaId().getCodigo())
                    .semestreMateria(grupoCohorteDocente.getGrupoId().getMateriaId().getSemestre())
                    .moodleId(grupoCohorteDocente.getMoodleId())
                    .build();
            return grupoCohorteDocenteResponse;
        }).toList();
    }

    public List<GrupoCohorteDocenteResponse> listarGruposPorGrupo(Integer grupoId) throws GrupoNotFoundException {
        Grupo grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new GrupoNotFoundException(
                        String.format(IS_NOT_FOUND, "EL GRUPO CON ID " + grupoId).toLowerCase()));

        List<GrupoCohorte> grupoCohorte = grupoCohorteRepository.findByGrupoId(grupo);
        return grupoCohorte.stream().map(grupoCohorteDocente -> {
            GrupoCohorteDocenteResponse grupoCohorteDocenteResponse = new GrupoCohorteDocenteResponse().builder()
                    .id(grupoCohorteDocente.getId())
                    .grupoCohorteId(grupoCohorteDocente.getId())
                    .grupoId(grupoCohorteDocente.getGrupoId().getId())
                    .cohorteGrupoId(grupoCohorteDocente.getCohorteGrupoId().getId())
                    .docenteId(grupoCohorteDocente.getDocenteId().getId())
                    .docenteNombre(grupoCohorteDocente.getDocenteId().getNombreCompleto())
                    .cohorteGrupoNombre(grupoCohorteDocente.getCohorteGrupoId().getNombre())
                    .cohorteId(grupoCohorteDocente.getCohorteId().getId())
                    .cohorteNombre(grupoCohorteDocente.getCohorteId().getNombre())
                    .fechaCreacion(grupoCohorteDocente.getFechaCreacion().toString())
                    .grupoNombre(grupoCohorteDocente.getGrupoId().getNombre())
                    .codigoGrupo(grupoCohorteDocente.getGrupoId().getCodigo())
                    .materia(grupoCohorteDocente.getGrupoId().getMateriaId().getNombre())
                    .codigoMateria(grupoCohorteDocente.getGrupoId().getMateriaId().getCodigo())
                    .semestreMateria(grupoCohorteDocente.getGrupoId().getMateriaId().getSemestre())
                    .moodleId(grupoCohorteDocente.getMoodleId())
                    .build();
            return grupoCohorteDocenteResponse;
        }).toList();
    }

    public List<GrupoCohorteDocenteResponse> listarGruposPorDocente(Integer docenteId) throws UserNotFoundException {
        Usuario usuario = usuarioRepository.findById(docenteId)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format(IS_NOT_FOUND, "EL USUARIO CON ID " + docenteId).toLowerCase()));

        List<GrupoCohorte> grupoCohorteDocentes = grupoCohorteRepository.findByDocenteId(usuario);
        return grupoCohorteDocentes.stream().map(grupoCohorteDocente -> {
            GrupoCohorteDocenteResponse grupoCohorteDocenteResponse = new GrupoCohorteDocenteResponse().builder()
                    .id(grupoCohorteDocente.getId())
                    .grupoCohorteId(grupoCohorteDocente.getId())
                    .grupoId(grupoCohorteDocente.getGrupoId().getId())
                    .cohorteGrupoId(grupoCohorteDocente.getCohorteGrupoId().getId())
                    .docenteId(grupoCohorteDocente.getDocenteId().getId())
                    .docenteNombre(grupoCohorteDocente.getDocenteId().getNombreCompleto())
                    .cohorteGrupoNombre(grupoCohorteDocente.getCohorteGrupoId().getNombre())
                    .cohorteId(grupoCohorteDocente.getCohorteId().getId())
                    .cohorteNombre(grupoCohorteDocente.getCohorteId().getNombre())
                    .fechaCreacion(grupoCohorteDocente.getFechaCreacion().toString())
                    .grupoNombre(grupoCohorteDocente.getGrupoId().getNombre())
                    .codigoGrupo(grupoCohorteDocente.getGrupoId().getCodigo())
                    .materia(grupoCohorteDocente.getGrupoId().getMateriaId().getNombre())
                    .codigoMateria(grupoCohorteDocente.getGrupoId().getMateriaId().getCodigo())
                    .semestreMateria(grupoCohorteDocente.getGrupoId().getMateriaId().getSemestre())
                    .moodleId(grupoCohorteDocente.getMoodleId())
                    .build();
            return grupoCohorteDocenteResponse;
        }).toList();

    }

    public List<GrupoCohorteDocenteResponse> listarGruposPorCohorte(Integer cohorteId) throws CohorteNotFoundException {
        Cohorte cohorte = cohorteRepository.findById(cohorteId)
                .orElseThrow(() -> new CohorteNotFoundException(
                        String.format(IS_NOT_FOUND_F, "LA COHORTE CON ID " + cohorteId).toLowerCase()));

        List<GrupoCohorte> grupoCohorteDocentes = grupoCohorteRepository.findByCohorteId(cohorte);
        return grupoCohorteDocentes.stream().map(grupoCohorteDocente -> {
            GrupoCohorteDocenteResponse grupoCohorteDocenteResponse = new GrupoCohorteDocenteResponse().builder()
                    .id(grupoCohorteDocente.getId())
                    .grupoCohorteId(grupoCohorteDocente.getId())
                    .grupoId(grupoCohorteDocente.getGrupoId().getId())
                    .cohorteGrupoId(grupoCohorteDocente.getCohorteGrupoId().getId())
                    .docenteId(grupoCohorteDocente.getDocenteId().getId())
                    .docenteNombre(grupoCohorteDocente.getDocenteId().getNombreCompleto())
                    .cohorteGrupoNombre(grupoCohorteDocente.getCohorteGrupoId().getNombre())
                    .cohorteId(grupoCohorteDocente.getCohorteId().getId())
                    .cohorteNombre(grupoCohorteDocente.getCohorteId().getNombre())
                    .fechaCreacion(grupoCohorteDocente.getFechaCreacion().toString())
                    .grupoNombre(grupoCohorteDocente.getGrupoId().getNombre())
                    .codigoGrupo(grupoCohorteDocente.getGrupoId().getCodigo())
                    .materia(grupoCohorteDocente.getGrupoId().getMateriaId().getNombre())
                    .codigoMateria(grupoCohorteDocente.getGrupoId().getMateriaId().getCodigo())
                    .semestreMateria(grupoCohorteDocente.getGrupoId().getMateriaId().getSemestre())
                    .moodleId(grupoCohorteDocente.getMoodleId())
                    .build();
            return grupoCohorteDocenteResponse;
        }).toList();
    }

    public List<GrupoCohorteResponse> listarGruposPorCohorteGrupo(Integer cohorteGrupoId)
            throws CohorteNotFoundException {
        CohorteGrupo cohorteGrupo = cohorteGrupoRepository.findById(cohorteGrupoId).orElse(null);
        if (cohorteGrupo == null) {
            throw new CohorteNotFoundException("El grupo de cohorte no fue encontrado");

        }
        List<GrupoCohorte> gruposCohorte = grupoCohorteRepository.findByCohorteGrupoId(cohorteGrupo);

        return gruposCohorte.stream().map(grupoCohorte -> {
            GrupoCohorteResponse grupoCohorteResponse = new GrupoCohorteResponse().builder()
                    .id(grupoCohorte.getId())
                    .grupoNombre(grupoCohorte.getGrupoId().getNombre())
                    .grupoId(grupoCohorte.getGrupoId().getId())
                    .grupoCodigo(grupoCohorte.getGrupoId().getCodigo())
                    .cohorteGrupoId(grupoCohorte.getCohorteGrupoId().getId())
                    .cohorteGrupoNombre(grupoCohorte.getCohorteGrupoId().getNombre())
                    .cohorteId(grupoCohorte.getCohorteId().getId())
                    .cohorteNombre(grupoCohorte.getCohorteId().getNombre())
                    .build();
            return grupoCohorteResponse;
        }).toList();
    }

    public EstudianteGrupoResponse listarEstudiantesPorGrupoCohorte(Long grupoCohorteId) {
        // 1. Obtener el grupo cohorte
        GrupoCohorte grupoCohorte = grupoCohorteRepository.findById(grupoCohorteId)
                .orElseThrow(() -> new RuntimeException("Grupo Cohorte no encontrado"));

        // 2. Obtener estado "En curso" (ID 2)
        EstadoMatricula estadoEnCurso = estadoMatriculaRepository.findById(2)
                .orElseThrow(() -> new RuntimeException("Estado de matrícula 'En curso' no configurado"));

        // 3. Obtener matrículas en curso para este grupo cohorte
        List<Matricula> matriculas = matriculaRepository.findByGrupoCohorteIdAndEstadoMatriculaId(
                grupoCohorte, estadoEnCurso);

        // 4. Construir la respuesta
        return EstudianteGrupoResponse.builder()
                .id(grupoCohorte.getId())
                .grupoNombre(grupoCohorte.getGrupoId().getNombre())
                .grupoCodigo(grupoCohorte.getGrupoId().getCodigo())
                .grupoCohorte(grupoCohorte.getCohorteGrupoId().getNombre())
                .grupoCohorteId(grupoCohorte.getCohorteGrupoId().getId())
                .estudiantes(matriculas.stream()
                        .map(matricula -> {
                            Estudiante e = matricula.getEstudianteId();
                            return new EstudianteGrupoResponse.estudianteResponse(
                                    e.getId(),
                                    e.getNombre(),
                                    e.getNombre2(),
                                    e.getApellido(),
                                    e.getApellido2(),
                                    e.getCodigo(),
                                    e.getEmail());
                        })
                        .collect(Collectors.toList()))
                .build();
    }

}
