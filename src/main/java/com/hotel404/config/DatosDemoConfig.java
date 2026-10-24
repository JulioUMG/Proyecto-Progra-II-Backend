package com.hotel404.config;

import com.hotel404.entity.*;
import com.hotel404.repository.*;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnProperty(name = "hotel.demo", havingValue = "true")
public class DatosDemoConfig implements CommandLineRunner {

    private final PerfilesRepository perfiles;
    private final UsuariosRepository usuarios;
    private final HabitacionesRepository habitaciones;
    private final PasswordEncoder encoder;

    public DatosDemoConfig(
        PerfilesRepository perfiles,
        UsuariosRepository usuarios,
        HabitacionesRepository habitaciones,
        PasswordEncoder encoder
    ) {
        this.perfiles = perfiles;
        this.usuarios = usuarios;
        this.habitaciones = habitaciones;
        this.encoder = encoder;
    }

    // Solo se ejecuta con --hotel.demo=true y nunca reemplaza registros existentes.
    @Override
    @Transactional
    public void run(String... args) {
        if (usuarios.count() == 0) {
            crearUsuario("Administrador", "admin", "Hotel2026!", "Administrador");
            crearUsuario("Recepcionista", "recepcion", "Recepcion2026!", "Recepcionista");
        }
        if (habitaciones.count() == 0) {
            for (int nivel = 1; nivel <= 3; nivel++) {
                for (int numero = 1; numero <= 6; numero++) {
                    Habitaciones h = new Habitaciones();
                    h.setNumero(String.valueOf(nivel * 100 + numero));
                    h.setTipo(
                        numero % 3 == 0
                            ? "Triple · tres camas"
                            : numero % 2 == 0
                              ? "Doble · dos camas"
                              : "Sencilla · una cama"
                    );
                    h.setPrecioNoche(BigDecimal.valueOf(numero % 3 == 0 ? 450 : numero % 2 == 0 ? 350 : 250));
                    h.setEstado(true);
                    h.setEstadoHabitacion("DISPONIBLE");
                    habitaciones.save(h);
                }
            }
        }
        System.out.println("Datos de ejemplo: carga solicitada; no se reemplazaron registros existentes.");
    }

    private void crearUsuario(String nombre, String login, String clave, String rol) {
        Perfiles perfil = perfiles
            .findAll()
            .stream()
            .filter(p -> p.getNombre().equalsIgnoreCase(rol))
            .findFirst()
            .orElseGet(() -> {
                Perfiles p = new Perfiles();
                p.setNombre(rol);
                p.setEstado(true);
                return perfiles.save(p);
            });
        Usuarios usuario = new Usuarios();
        usuario.setNombre(nombre);
        usuario.setUsuario(login);
        usuario.setCorreo(login + "@hotel.example");
        usuario.setContrasena(encoder.encode(clave));
        usuario.setIdPerfil(perfil);
        usuario.setEstado(true);
        usuarios.save(usuario);
    }
}
