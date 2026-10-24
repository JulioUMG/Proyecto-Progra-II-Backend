package com.hotel404.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class DetalleReservacionDTO {

    private Integer idDetalleReservacion;
    private BigDecimal precioPactado;
    private boolean estado;
    private Integer idHabitacion;
    private Integer idReservacion;
}
