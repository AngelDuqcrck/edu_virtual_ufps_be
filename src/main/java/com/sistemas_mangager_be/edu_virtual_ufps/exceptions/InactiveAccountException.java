package com.sistemas_mangager_be.edu_virtual_ufps.exceptions;

import org.springframework.security.core.AuthenticationException;

public class InactiveAccountException extends AuthenticationException {

    public InactiveAccountException(String msg) {
        super(msg);
    }

    public InactiveAccountException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
