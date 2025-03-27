package com.sistemas_mangager_be.edu_virtual_ufps.services.implementations;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Admin;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.AdminRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.IAdminService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.AdminDTO;


@Service
public class AdminServiceImplementation implements IAdminService {
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public AdminDTO registrarAdmin(AdminDTO adminDTO) {
        Admin admin = new Admin();
        BeanUtils.copyProperties(adminDTO, admin);
        admin.setPassword( passwordEncoder.encode(adminDTO.getPassword()));
        adminRepository.save(admin);

        AdminDTO adminCreado = new AdminDTO();
        BeanUtils.copyProperties(admin, adminCreado);
        return adminCreado;
    }

    public List<AdminDTO> listarAdmins() {
        List<Admin> admins = adminRepository.findAll();
        return admins.stream().map(admin -> {
            AdminDTO adminDTO = new AdminDTO();
            BeanUtils.copyProperties(admin, adminDTO);
            return adminDTO;
        }).toList();
    }
}
