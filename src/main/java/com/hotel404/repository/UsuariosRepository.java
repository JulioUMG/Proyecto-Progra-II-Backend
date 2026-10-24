package com.hotel404.repository;

import com.hotel404.entity.Usuarios;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuariosRepository extends JpaRepository<Usuarios, Integer> {
    List<Usuarios> findByEstadoTrue();

    // Busca la cuenta que intenta iniciar sesión.
    java.util.Optional<Usuarios> findByUsuarioIgnoreCase(String usuario);
    boolean existsByUsuarioIgnoreCase(String usuario);
    boolean existsByCorreoIgnoreCase(String correo);

    boolean existsByNombreIgnoreCaseAndUsuarioIgnoreCaseAndCorreoIgnoreCase(
        String nombre,
        String correo,
        String usuario
    );
}
