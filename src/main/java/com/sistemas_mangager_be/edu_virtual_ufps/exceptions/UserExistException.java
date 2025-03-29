package com.sistemas_mangager_be.edu_virtual_ufps.exceptions;

public class UserExistException extends Exception {
    
    public UserExistException(String mensaje) {
        super(mensaje);
    }
    
}
