package com.hotel404.service;

import com.hotel404.dto.SolicitudReservaDTO;
import com.hotel404.entity.*;
import com.hotel404.repository.*;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FlujoHotelService {

    private final HabitacionesRepository habitaciones;
    private final DetalleReservacionRepository detalles;
    private final ReservacionesRepository reservas;
    private final HuespedesRepository huespedes;
    private final UsuariosRepository usuarios;
    private final EntityManager entidades;
    private static final ZoneId ZONA = ZoneId.of("America/Guatemala");

    public FlujoHotelService(
        HabitacionesRepository habitaciones,
        DetalleReservacionRepository detalles,
        ReservacionesRepository reservas,
        HuespedesRepository huespedes,
        UsuariosRepository usuarios,
        EntityManager entidades
    ) {
        this.habitaciones = habitaciones;
        this.detalles = detalles;
        this.reservas = reservas;
        this.huespedes = huespedes;
        this.usuarios = usuarios;
        this.entidades = entidades;
    }

    // Convierte la fecha del formulario al tipo que utiliza la entidad.
    private Date fecha(LocalDate valor) {
        return java.sql.Date.valueOf(valor);
    }

    // Rechaza fechas pasadas y estancias sin al menos una noche.
    private void validarFechas(LocalDate entrada, LocalDate salida) {
        if (
            entrada == null ||
            salida == null ||
            entrada.isBefore(LocalDate.now(ZONA)) ||
            !salida.isAfter(entrada)
        ) {
            throw error("Selecciona fechas válidas: la salida debe ser posterior a la entrada.");
        }
    }

    // Devuelve un error de formulario que el frontend puede mostrar al usuario.
    private ResponseStatusException error(String mensaje) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensaje);
    }

    // Limpia espacios y revisa la longitud de un dato obligatorio.
    private String texto(String valor, int maximo, String campo) {
        if (valor == null || valor.isBlank() || valor.trim().length() > maximo) {
            throw error("Revisa el campo " + campo);
        }
        return valor.trim();
    }

    // Consulta habitaciones por fechas sin exponer información de otros huéspedes.
    @Transactional(readOnly = true)
    public List<Map<String, Object>> disponibilidad(LocalDate entrada, LocalDate salida) {
        validarFechas(entrada, salida);
        Set<Integer> ocupadas = new HashSet<>(detalles.buscarOcupadas(fecha(entrada), fecha(salida)));
        return habitaciones
            .findAll()
            .stream()
            .filter(Habitaciones::getEstado)
            .map(habitacion -> {
                String estado = "DISPONIBLE";
                if ("MANTENIMIENTO".equalsIgnoreCase(habitacion.getEstadoHabitacion())) estado =
                    "MANTENIMIENTO";
                else if (ocupadas.contains(habitacion.getIdHabitacion())) estado = "OCUPADA";
                Map<String, Object> fila = new LinkedHashMap<>();
                fila.put("idHabitacion", habitacion.getIdHabitacion());
                fila.put("numero", habitacion.getNumero());
                fila.put("tipo", habitacion.getTipo());
                fila.put("precioNoche", habitacion.getPrecioNoche());
                fila.put("disponibilidad", estado);
                return fila;
            })
            .toList();
    }

    // Guarda huésped, reserva y detalles juntos. Si algo falla, no queda una reserva incompleta.
    @Transactional
    public Map<String, Object> reservar(SolicitudReservaDTO solicitud, String nombreUsuario) {
        validarFechas(solicitud.getEntrada(), solicitud.getSalida());
        String nombre = texto(solicitud.getNombre(), 100, "nombre");
        String dpi = texto(solicitud.getDpi(), 20, "DPI");
        String telefono = texto(solicitud.getTelefono(), 20, "teléfono");
        if (!dpi.matches("[0-9]{13}") || nombre.length() < 3 || telefono.length() < 8) {
            throw error("Ingresa nombre completo, DPI de 13 dígitos y teléfono válido.");
        }
        List<Integer> ids = solicitud.getHabitaciones();
        if (
            ids == null ||
            ids.isEmpty() ||
            ids.size() > 30 ||
            ids.stream().anyMatch(Objects::isNull) ||
            new HashSet<>(ids).size() != ids.size()
        ) {
            throw error("Selecciona entre 1 y 30 habitaciones sin repetirlas.");
        }

        // Bloquea las habitaciones durante el guardado para evitar dos reservas simultáneas.
        List<Habitaciones> seleccion = habitaciones.bloquearPorIds(ids);
        List<Integer> ocupadas = detalles.buscarOcupadas(
            fecha(solicitud.getEntrada()),
            fecha(solicitud.getSalida())
        );
        if (
            seleccion.size() != ids.size() ||
            seleccion
                .stream()
                .anyMatch(
                    h ->
                        !h.getEstado() ||
                        "MANTENIMIENTO".equalsIgnoreCase(h.getEstadoHabitacion()) ||
                        ocupadas.contains(h.getIdHabitacion())
                )
        ) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Una habitación ya no está disponible. Consulta de nuevo."
            );
        }

        // El modelo exige responsable: una solicitud web se asigna al administrador activo.
        Usuarios responsable = buscarResponsable(nombreUsuario);
        Huespedes huesped = huespedes.findByDpi(dpi).orElseGet(() -> {
            Huespedes nuevo = new Huespedes();
            nuevo.setNombre(nombre);
            nuevo.setDpi(dpi);
            nuevo.setTelefono(telefono);
            nuevo.setEstado(true);
            return huespedes.save(nuevo);
        });
        if (
            !huesped.getEstado() ||
            !huesped.getNombre().equalsIgnoreCase(nombre) ||
            !huesped.getTelefono().equals(telefono)
        ) {
            throw error("Contacta a recepción para verificar los datos del huésped registrado.");
        }

        // El precio enviado desde el navegador no se utiliza: se toman las tarifas guardadas.
        long noches = ChronoUnit.DAYS.between(solicitud.getEntrada(), solicitud.getSalida());
        BigDecimal total = seleccion
            .stream()
            .map(Habitaciones::getPrecioNoche)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .multiply(BigDecimal.valueOf(noches));
        if (total.compareTo(new BigDecimal("99999999.99")) > 0) throw error(
            "El total supera el límite de la reservación."
        );
        Reservaciones reserva = new Reservaciones();
        reserva.setIdHuesped(huesped);
        reserva.setIdUsuario(responsable);
        reserva.setEstado(true);
        reserva.setFechaReservacion(fecha(LocalDate.now(ZONA)));
        reserva.setFechaIngreso(fecha(solicitud.getEntrada()));
        reserva.setFechaSalida(fecha(solicitud.getSalida()));
        reserva.setEstadoReservacion("PENDIENTE");
        reserva.setTotal(total);
        reservas.save(reserva);
        for (Habitaciones habitacion : seleccion) {
            DetalleReservacion detalle = new DetalleReservacion();
            detalle.setIdReservacion(reserva);
            detalle.setIdHabitacion(habitacion);
            detalle.setPrecioPactado(habitacion.getPrecioNoche());
            detalle.setEstado(true);
            detalles.save(detalle);
        }
        return Map.of(
            "idReservacion",
            reserva.getIdReservacion(),
            "total",
            total,
            "estadoReservacion",
            "PENDIENTE"
        );
    }

    // Asigna el empleado conectado o el administrador que recibe solicitudes públicas.
    private Usuarios buscarResponsable(String nombreUsuario) {
        if (nombreUsuario != null) return usuarios.findByUsuarioIgnoreCase(nombreUsuario).orElseThrow();
        return usuarios
            .findByEstadoTrue()
            .stream()
            .filter(
                u ->
                    u.getIdPerfil() != null &&
                    u.getIdPerfil().getEstado() &&
                    u.getIdPerfil().getNombre().toLowerCase().contains("admin")
            )
            .findFirst()
            .orElseThrow(() -> error("El hotel necesita un administrador activo para recibir solicitudes."));
    }

    // Avanza la reserva por sus estados válidos y actualiza las habitaciones al entrar o salir.
    @Transactional
    public void cambiarEstado(Integer id, String nuevoEstado) {
        Reservaciones reserva = reservas.findById(id).orElseThrow(() -> error("Reserva no encontrada"));
        List<DetalleReservacion> lineas = detalles.findByIdReservacion_IdReservacion(id);
        habitaciones.bloquearPorIds(
            lineas
                .stream()
                .map(d -> d.getIdHabitacion().getIdHabitacion())
                .toList()
        );
        entidades.refresh(reserva);
        Map<String, List<String>> permitidos = Map.of(
            "PENDIENTE",
            List.of("CONFIRMADA", "CANCELADA", "RECHAZADA"),
            "CONFIRMADA",
            List.of("HOSPEDADO", "CANCELADA"),
            "HOSPEDADO",
            List.of("FINALIZADA")
        );
        if (
            !reserva.getEstado() ||
            !permitidos
                .getOrDefault(reserva.getEstadoReservacion().toUpperCase(), List.of())
                .contains(nuevoEstado)
        ) {
            throw error("Ese cambio de estado no está permitido.");
        }
        LocalDate hoy = LocalDate.now(ZONA);
        LocalDate entrada = new java.sql.Date(reserva.getFechaIngreso().getTime()).toLocalDate();
        LocalDate salida = new java.sql.Date(reserva.getFechaSalida().getTime()).toLocalDate();
        if (nuevoEstado.equals("CONFIRMADA") && !salida.isAfter(hoy)) throw error("La estancia ya venció.");
        if (nuevoEstado.equals("HOSPEDADO")) {
            if (hoy.isBefore(entrada) || !hoy.isBefore(salida)) throw error(
                "La entrada debe registrarse durante las fechas reservadas."
            );
            if (hoy.equals(entrada) && LocalTime.now(ZONA).isBefore(LocalTime.of(15, 0))) throw error(
                "La entrada está disponible desde las 15:00."
            );
            for (DetalleReservacion detalle : lineas) {
                Habitaciones habitacion = detalle.getIdHabitacion();
                entidades.refresh(habitacion);
                if (
                    !habitacion.getEstado() ||
                    !"DISPONIBLE".equalsIgnoreCase(habitacion.getEstadoHabitacion())
                ) {
                    throw error(
                        "Prepara las habitaciones antes de registrar la entrada: deben estar disponibles y limpias."
                    );
                }
                habitacion.setEstadoHabitacion("OCUPADA");
            }
        }
        if (nuevoEstado.equals("FINALIZADA")) {
            for (DetalleReservacion detalle : lineas)
                detalle.getIdHabitacion().setEstadoHabitacion("LIMPIEZA");
        }
        reserva.setEstadoReservacion(nuevoEstado);
        if (
            nuevoEstado.equals("CONFIRMADA") &&
            lineas
                .stream()
                .anyMatch(d -> "MANTENIMIENTO".equalsIgnoreCase(d.getIdHabitacion().getEstadoHabitacion()))
        ) {
            throw error("Una habitación está en mantenimiento.");
        }
    }

    // Cambia limpieza o mantenimiento sin interrumpir estancias ni bloquear reservas futuras.
    @Transactional
    public void estadoHabitacion(Integer id, String estado) {
        if (!List.of("DISPONIBLE", "MANTENIMIENTO", "LIMPIEZA").contains(estado)) throw error(
            "Estado no permitido."
        );
        Habitaciones habitacion = habitaciones
            .bloquearPorIds(List.of(id))
            .stream()
            .findFirst()
            .orElseThrow(() -> error("Habitación no encontrada"));
        List<DetalleReservacion> lineas = detalles
            .findAll()
            .stream()
            .filter(
                d ->
                    d.getEstado() &&
                    d.getIdHabitacion().getIdHabitacion().equals(id) &&
                    d.getIdReservacion().getEstado()
            )
            .toList();
        if (
            lineas
                .stream()
                .anyMatch(d -> "HOSPEDADO".equalsIgnoreCase(d.getIdReservacion().getEstadoReservacion()))
        ) {
            throw error("Registra la salida antes de cambiar esta habitación.");
        }
        if (
            estado.equals("MANTENIMIENTO") &&
            lineas.stream().anyMatch(
                d ->
                    List.of("PENDIENTE", "CONFIRMADA").contains(
                        d.getIdReservacion().getEstadoReservacion().toUpperCase()
                    ) &&
                    !d
                        .getIdReservacion()
                        .getFechaSalida()
                        .before(fecha(LocalDate.now(ZONA)))
            )
        ) {
            throw error(
                "Esta habitación tiene reservas pendientes o confirmadas. Cancélalas antes de marcar mantenimiento."
            );
        }
        habitacion.setEstadoHabitacion(estado);
    }
}
