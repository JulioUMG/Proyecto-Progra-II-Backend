package com.hotel404.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class HabitacionesDTO {

    private Integer idHabitacion;
    private String numero;
    private String tipo;
    private BigDecimal precioNoche;
    private boolean estado;
    private String estadoHabitacion;
}
