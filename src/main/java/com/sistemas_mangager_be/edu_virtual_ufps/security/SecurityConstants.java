package com.sistemas_mangager_be.edu_virtual_ufps.security;

public class SecurityConstants {
    public static final long JWT_EXPIRATION_TIME_TOKEN = 86_400_000; // Tiempo de vida de token default en 1 dia

    public static final long JWT_EXPIRATION_TIME_REFRESH_TOKEN = 259_200_000; // 3 días para el Refresh Token
    public static final String JWT_FIRMA = "EduVirtualUFPS";
    public static final long JWT_EXPIRATION_TIME_PASSWORD_RESET = 900000; // Token de cambio de password 15 min
}
