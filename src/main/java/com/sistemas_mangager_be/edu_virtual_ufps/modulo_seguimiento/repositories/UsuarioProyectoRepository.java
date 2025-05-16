package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.id_compuesto.UsuarioProyectoId;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.intermedias.UsuarioProyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioProyectoRepository extends JpaRepository<UsuarioProyecto, UsuarioProyectoId> {

    void deleteByIdUsuarioAndIdProyecto(Integer idUsuario, Integer idProyecto);

    boolean existsByIdUsuarioAndIdProyecto(Integer idUsuario, Integer idProyecto);

    List<UsuarioProyecto> findByIdProyecto(Integer idProyecto);
}