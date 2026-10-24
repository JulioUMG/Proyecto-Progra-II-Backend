package com.hotel404.repository;

import com.hotel404.entity.Habitaciones;
import jakarta.persistence.LockModeType;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface HabitacionesRepository extends JpaRepository<Habitaciones, Integer> {
    // Serializa solicitudes sobre las habitaciones seleccionadas.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select h from Habitaciones h where h.idHabitacion in :ids order by h.idHabitacion")
    List<Habitaciones> bloquearPorIds(@Param("ids") List<Integer> ids);
}
