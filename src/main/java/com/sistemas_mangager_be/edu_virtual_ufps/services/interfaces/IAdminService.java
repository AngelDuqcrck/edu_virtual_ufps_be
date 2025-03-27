package com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces;

import java.util.List;

import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.AdminDTO;

public interface IAdminService {
    
    public AdminDTO registrarAdmin(AdminDTO adminDTO);

    public List<AdminDTO> listarAdmins();
}
