package com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces;

import java.util.List;

import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.ChangeNotAllowedException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.EmailNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.PasswordNotEqualsException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.TokenNotValidException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.UserNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.AdminDTO;

public interface IAdminService {
    
    public AdminDTO registrarAdmin(AdminDTO adminDTO);

    public List<AdminDTO> listarAdmins();

    public AdminDTO actualizarAdmin(Integer id, AdminDTO adminDTO) throws UserNotFoundException;

    public void activarAdmin(Integer id) throws UserNotFoundException, ChangeNotAllowedException;

    public void desactivarAdmin(Integer id) throws UserNotFoundException, ChangeNotAllowedException;

    public void enviarTokenRecuperacion(String email) throws UserNotFoundException;
    
     public void restablecerpassword(String token, String nuevaPassword, String nuevaPassword2)
            throws TokenNotValidException, EmailNotFoundException, PasswordNotEqualsException;
}
