package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.services;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos.ActividadDto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos.DashboardDto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos.FaseDto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Coloquio;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Proyecto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Sustentacion;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.enums.EstadoProyecto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories.ColoquioRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories.ProyectoRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories.SustentacionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final ProyectoRepository proyectoRepository;
    private final SustentacionRepository sustentacionRepository;
    private final ColoquioRepository coloquioRepository;

    public DashboardService(ProyectoRepository proyectoRepository,
                            SustentacionRepository sustentacionRepository,
                            ColoquioRepository coloquioRepository) {
        this.proyectoRepository = proyectoRepository;
        this.sustentacionRepository = sustentacionRepository;
        this.coloquioRepository = coloquioRepository;
    }

    public DashboardDto obtenerDashboard(Integer proyectoId, Long usuarioId) {
        DashboardDto dto = new DashboardDto();

        Proyecto proyecto = proyectoRepository.findById(proyectoId).orElseThrow();
        dto.setFaseActual(mapFase(proyecto));

        List<ActividadDto> actividades = new ArrayList<>();
        actividades.addAll(mapSustentaciones(sustentacionRepository.findByProyectoId(proyectoId)));
        actividades.addAll(mapColoquios(coloquioRepository.findColoquiosByUsuarioId(usuarioId)));

        LocalDateTime ahora = LocalDateTime.now();
        Optional<LocalDateTime> fechaMinimaOpt = actividades.stream()
                .map(a -> LocalDateTime.of(a.getFecha(), a.getHora()))
                .filter(fecha -> fecha.isAfter(ahora))
                .min(LocalDateTime::compareTo);

        List<ActividadDto> proximasActividades = new ArrayList<>();
        if (fechaMinimaOpt.isPresent()) {
            LocalDateTime fechaMinima = fechaMinimaOpt.get();
            proximasActividades = actividades.stream()
                    .filter(a -> LocalDateTime.of(a.getFecha(), a.getHora()).equals(fechaMinima))
                    .collect(Collectors.toList());
        }
        dto.setProximaActividad(proximasActividades);

        List<ActividadDto> atrasadas = actividades.stream()
                .filter(a -> LocalDateTime.of(a.getFecha(), a.getHora()).isBefore(ahora))
                .collect(Collectors.toList());
        dto.setTareasAtrasadas(atrasadas);

        return dto;
    }

    private FaseDto mapFase(Proyecto proyecto) {
        String estado = proyecto.getEstadoActual().getDescripcion();
        int numeroFase = proyecto.getEstadoActual().getCode();
        int totalFases = EstadoProyecto.values().length;
        int porcentaje = (proyecto.getEstadoActual() == EstadoProyecto.FASE_0) ? 100 : numeroFase * 10;

        List<String> fasesCompletadas;
        List<String> fasesPendientes;

        if (proyecto.getEstadoActual() == EstadoProyecto.FASE_0) {
            fasesCompletadas = Arrays.stream(EstadoProyecto.values())
                    .filter(f -> f.getCode() != 0)
                    .sorted(Comparator.comparingInt(EstadoProyecto::getCode))
                    .map(EstadoProyecto::getDescripcion)
                    .collect(Collectors.toList());

            fasesPendientes = new ArrayList<>();
        } else {
            fasesCompletadas = Arrays.stream(EstadoProyecto.values())
                    .filter(f -> f.getCode() != 0 && f.getCode() < numeroFase)
                    .sorted(Comparator.comparingInt(EstadoProyecto::getCode))
                    .map(EstadoProyecto::getDescripcion)
                    .collect(Collectors.toList());

            fasesPendientes = Arrays.stream(EstadoProyecto.values())
                    .filter(f -> f.getCode() != 0 && f.getCode() > numeroFase)
                    .sorted(Comparator.comparingInt(EstadoProyecto::getCode))
                    .map(EstadoProyecto::getDescripcion)
                    .collect(Collectors.toList());
            fasesPendientes.add(EstadoProyecto.FASE_0.getDescripcion());
        }

        return new FaseDto(
                numeroFase,
                estado,
                totalFases,
                porcentaje,
                fasesCompletadas,
                fasesPendientes
        );
    }

    private List<ActividadDto> mapSustentaciones(List<Sustentacion> lista) {
        return lista.stream().map(s -> {
            ActividadDto dto = new ActividadDto();
            dto.setTipo(s.getTipoSustentacion().toString());
            dto.setDescripcion(s.getDescripcion());
            dto.setFecha(s.getFecha());
            dto.setHora(s.getHora());
            dto.setHoraFin(s.getHoraFin());
            dto.setLugar(s.getLugar());
            return dto;
        }).collect(Collectors.toList());
    }

    private List<ActividadDto> mapColoquios(List<Coloquio> lista) {
        return lista.stream().map(c -> {
            ActividadDto dto = new ActividadDto();
            dto.setTipo("Coloquio");
            dto.setDescripcion(c.getDescripcion());
            dto.setFecha(c.getFecha());
            dto.setHora(c.getHora());
            dto.setLugar(c.getLugar());
            return dto;
        }).collect(Collectors.toList());
    }
}
