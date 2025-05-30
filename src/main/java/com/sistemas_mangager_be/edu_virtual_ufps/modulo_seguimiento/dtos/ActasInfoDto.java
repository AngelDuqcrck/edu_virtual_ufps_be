package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos;

import lombok.Data;

@Data
public class ActasInfoDto {
    String fecha; //Formato <<Mes>> <<Dia>> de <<Año>>
    String hora;
    String sala;
    String descripcionSustentacion;
    String tituloProyecto;
    String estudiante;
    String codigo;
    String director;
    String codirector;
    String juradoInterno;
    String juradoExterno;
    String Observaciones;

    String correo;
    String telefono;
    String dia;
    String mes;
    String year;
}
