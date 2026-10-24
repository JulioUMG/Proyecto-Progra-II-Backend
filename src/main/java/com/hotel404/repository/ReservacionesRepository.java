package com.hotel404.repository;

import com.hotel404.entity.Reservaciones;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservacionesRepository extends JpaRepository<Reservaciones, Integer> {
    List<Reservaciones> findByEstadoTrue();

    List<Reservaciones> findByEstadoReservacionIgnoreCase(String estadoReservacion);

    List<Reservaciones> findByIdHuesped_IdHuesped(Integer idHuesped);
}
