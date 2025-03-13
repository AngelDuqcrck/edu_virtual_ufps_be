package com.sistemas_mangager_be.edu_virtual_ufps.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Rol;

public interface RolRepository extends JpaRepository<Rol, Integer> {
    //Metodo para buscar un rol por su nombre en la base de datos
    Optional<Rol> findByNombre(String nombre);
}
