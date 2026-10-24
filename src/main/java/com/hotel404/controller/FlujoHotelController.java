package com.hotel404.controller;

import com.hotel404.dto.*;
import com.hotel404.repository.UsuariosRepository;
import com.hotel404.service.ConsultaHotelService;
import com.hotel404.service.FlujoHotelService;
import com.hotel404.service.HabitacionesService;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class FlujoHotelController {

    private final FlujoHotelService hotel;
    private final UsuariosRepository usuarios;
    private final ConsultaHotelService consultas;
    private final HabitacionesService habitaciones;

    public FlujoHotelController(
        FlujoHotelService hotel,
        UsuariosRepository usuarios,
        ConsultaHotelService consultas,
        HabitacionesService habitaciones
    ) {
        this.hotel = hotel;
        this.usuarios = usuarios;
        this.consultas = consultas;
        this.habitaciones = habitaciones;
    }

    // Permite comprobar el encendido del backend sin consultar datos del hotel.
    @GetMapping("/public/salud")
    public Map<String, String> salud() {
        return Map.of("estado", "OK");
    }

    // Entrega las reservas con los datos que necesita recepción.
    @GetMapping("/admin/reservas")
    public List<ReservaVistaDTO> reservas() {
        return consultas.listar();
    }

    // Consulta el inventario de habitaciones del área privada.
    @GetMapping("/admin/habitaciones")
    public List<HabitacionesDTO> habitaciones() {
        return habitaciones.obtenerTodas();
    }

    // Crea la habitación activa y lista para recibir huéspedes.
    @PostMapping("/admin/habitaciones")
    public HabitacionesDTO crearHabitacion(@RequestBody HabitacionesDTO dto) {
        dto.setIdHabitacion(null);
        dto.setEstado(true);
        dto.setEstadoHabitacion("DISPONIBLE");
        return habitaciones.guardar(dto);
    }

    // Modifica los datos comerciales sin alterar la ocupación.
    @PutMapping("/admin/habitaciones/{id}/datos")
    public HabitacionesDTO editarHabitacion(@PathVariable Integer id, @RequestBody HabitacionesDTO dto) {
        return habitaciones.actualizarDatos(id, dto);
    }

    // Entrega el token que protege el envío de formularios.
    @GetMapping("/auth/csrf")
    public Map<String, String> csrf(CsrfToken token) {
        return Map.of("token", token.getToken(), "headerName", token.getHeaderName());
    }

    // Devuelve la cuenta conectada y su perfil, sin contraseña.
    @GetMapping("/auth/me")
    public Map<String, Object> sesion(Principal principal) {
        var usuario = usuarios.findByUsuarioIgnoreCase(principal.getName()).orElseThrow();
        String rol = usuario.getIdPerfil().getNombre().toLowerCase().contains("admin")
            ? "ADMINISTRADOR"
            : "RECEPCIONISTA";
        return Map.of(
            "idUsuario",
            usuario.getIdUsuario(),
            "nombre",
            usuario.getNombre(),
            "usuario",
            usuario.getUsuario(),
            "rol",
            rol
        );
    }

    // Recibe las fechas y consulta las habitaciones disponibles.
    @GetMapping("/public/disponibilidad")
    public List<Map<String, Object>> disponibilidad(
        @RequestParam LocalDate entrada,
        @RequestParam LocalDate salida
    ) {
        return hotel.disponibilidad(entrada, salida);
    }

    // Registra una solicitud pública pendiente de aprobación.
    @PostMapping("/public/reservas")
    public Map<String, Object> reservar(@RequestBody SolicitudReservaDTO solicitud) {
        return hotel.reservar(solicitud, null);
    }

    // Asocia la reserva presencial con el empleado conectado.
    @PostMapping("/admin/reservas")
    public Map<String, Object> reservarRecepcion(
        @RequestBody SolicitudReservaDTO solicitud,
        Principal principal
    ) {
        return hotel.reservar(solicitud, principal.getName());
    }

    // Solicita el cambio de estado y deja las validaciones al servicio.
    @PutMapping("/admin/reservas/{id}/estado")
    public Map<String, String> estado(@PathVariable Integer id, @RequestBody Map<String, String> cuerpo) {
        hotel.cambiarEstado(id, cuerpo.getOrDefault("estado", ""));
        return Map.of("mensaje", "Reserva actualizada");
    }

    // Actualiza limpieza o mantenimiento de una habitación.
    @PutMapping("/admin/habitaciones/{id}/estado")
    public Map<String, String> habitacion(@PathVariable Integer id, @RequestBody Map<String, String> cuerpo) {
        hotel.estadoHabitacion(id, cuerpo.getOrDefault("estado", ""));
        return Map.of("mensaje", "Habitación actualizada");
    }
}
