package com.hotel404.repository;

import com.hotel404.entity.DetalleReservacion;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface DetalleReservacionRepository extends JpaRepository<DetalleReservacion, Integer> {
    List<DetalleReservacion> findByIdReservacion_IdReservacion(Integer id);

    // La fecha de salida no ocupa la noche de una nueva entrada.
    @Query(
        "select d.idHabitacion.idHabitacion from DetalleReservacion d where d.estado = true and d.idReservacion.estado = true and upper(d.idReservacion.estadoReservacion) in ('PENDIENTE', 'CONFIRMADA', 'HOSPEDADO') and d.idReservacion.fechaIngreso < :salida and d.idReservacion.fechaSalida > :entrada"
    )
    List<Integer> buscarOcupadas(@Param("entrada") Date entrada, @Param("salida") Date salida);
}
