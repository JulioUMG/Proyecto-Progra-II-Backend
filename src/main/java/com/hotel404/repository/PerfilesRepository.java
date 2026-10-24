package com.hotel404.repository;

import com.hotel404.entity.Perfiles;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerfilesRepository extends JpaRepository<Perfiles, Integer> {
    List<Perfiles> findByEstadoTrue();

    boolean existsByNombreIgnoreCase(String nombre);
}
