package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.enums;

public enum EstadoProyecto {
    FASE_0("Completado"),
    FASE_1("Inicio del proyecto"),
    FASE_2("Presentación inicial del proyecto"),
    FASE_3("Primera entrega del anteproyecto"),
    FASE_4("Sustentación del anteproyecto"),
    FASE_5("Planifiación de hitos del proyecto"),
    FASE_6("Seguimiento de hitos"),
    FASE_7("Entrega de documentos finales"),
    FASE_8("Aprobación de sustentación final"),
    FASE_9("Sustentación final");

    private final String descripcion;

    EstadoProyecto(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getCode() {
        return this.ordinal();
    }

    public static EstadoProyecto fromCode(int code) {
        return EstadoProyecto.values()[code];
    }
}
