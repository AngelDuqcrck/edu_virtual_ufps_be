package com.sistemas_mangager_be.edu_virtual_ufps.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Admin;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
    // Metodo para buscar un admin mediante su correo
    Optional<Admin> findByEmail(String email);

    // Metodo para verificar si el admin existe en la base de datos
    Boolean existsByEmail(String email);
}
