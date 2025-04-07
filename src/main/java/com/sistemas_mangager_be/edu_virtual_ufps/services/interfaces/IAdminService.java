package com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces;

import java.util.List;

import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.ChangeNotAllowedException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.UserNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.AdminDTO;

public interface IAdminService {
    
    public AdminDTO registrarAdmin(AdminDTO adminDTO);

    public List<AdminDTO> listarAdmins();

    public AdminDTO actualizarAdmin(Integer id, AdminDTO adminDTO) throws UserNotFoundException;

    public void activarAdmin(Integer id) throws UserNotFoundException, ChangeNotAllowedException;

    public void desactivarAdmin(Integer id) throws UserNotFoundException, ChangeNotAllowedException;

}
