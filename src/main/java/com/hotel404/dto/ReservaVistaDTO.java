package com.hotel404.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import lombok.Data;

// Datos que necesita recepción para consultar una reserva sin recorrer entidades.
@Data
public class ReservaVistaDTO {

    private Integer idReservacion;
    private String nombre;
    private String dpi;
    private String telefono;
    private Date fechaIngreso;
    private Date fechaSalida;
    private BigDecimal total;
    private String estadoReservacion;
    private List<String> habitaciones;
}
