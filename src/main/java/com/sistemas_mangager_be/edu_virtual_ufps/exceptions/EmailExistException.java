package com.sistemas_mangager_be.edu_virtual_ufps.exceptions;

public class EmailExistException extends Exception {
    
    public EmailExistException(String mensaje) {
        super(mensaje);
    }
}
