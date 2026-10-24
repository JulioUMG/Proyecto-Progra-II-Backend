package com.hotel404.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

// Datos que necesita recepción para consultar una reserva sin recorrer entidades.
@Data
public class ReservaVistaDTO {

    private Integer idReservacion;
    private String nombre;
    private String dpi;
    private String telefono;
    // Son días de estancia, sin hora ni conversión de zona horaria.
    private LocalDate fechaIngreso;
    private LocalDate fechaSalida;
    private BigDecimal total;
    private String estadoReservacion;
    private List<String> habitaciones;
}
