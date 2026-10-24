package com.hotel404.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class SolicitudReservaDTO {

    private String nombre;
    private String dpi;
    private String telefono;
    private LocalDate entrada;
    private LocalDate salida;
    private List<Integer> habitaciones;
}
