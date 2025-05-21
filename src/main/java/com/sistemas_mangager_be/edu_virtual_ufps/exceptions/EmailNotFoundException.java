package com.sistemas_mangager_be.edu_virtual_ufps.exceptions;

import org.springframework.data.jpa.repository.JpaRepository;

public class EmailNotFoundException extends Exception {

    public  EmailNotFoundException(String message) {
        super(message);
    }
    
}
