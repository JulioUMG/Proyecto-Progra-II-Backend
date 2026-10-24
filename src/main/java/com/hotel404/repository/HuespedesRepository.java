package com.hotel404.repository;

import com.hotel404.entity.Huespedes;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HuespedesRepository extends JpaRepository<Huespedes, Integer> {
    // Listar huéspedes activos
    List<Huespedes> findByEstadoTrue();

    // Buscar por DPI
    Optional<Huespedes> findByDpi(String dpi);

    // Buscar por nombre
    List<Huespedes> findByNombreContainingIgnoreCase(String nombre);

    // Validar DPI duplicado
    boolean existsByDpi(String dpi);
}
