package com.hotel404.hotel404;

import static org.junit.jupiter.api.Assertions.*;

import com.hotel404.dto.SolicitudReservaDTO;
import com.hotel404.repository.*;
import com.hotel404.service.FlujoHotelService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
@ActiveProfiles("demo")
@Transactional
// Estas pruebas usan la base temporal y no necesitan herramientas de simulación.
@org.springframework.test.context.TestExecutionListeners(
    listeners = {
        org.springframework.test.context.support.DependencyInjectionTestExecutionListener.class,
        org.springframework.test.context.transaction.TransactionalTestExecutionListener.class
    },
    mergeMode = org.springframework.test.context.TestExecutionListeners.MergeMode.REPLACE_DEFAULTS
)
class Hotel404ApplicationTests {

    @Autowired
    FlujoHotelService hotel;

    @Autowired
    HabitacionesRepository habitaciones;

    @Autowired
    DetalleReservacionRepository detalles;

    @Autowired
    ReservacionesRepository reservas;

    @Autowired
    HuespedesRepository huespedes;

    @Autowired
    com.hotel404.service.ConsultaHotelService consultas;

    @Autowired
    tools.jackson.databind.ObjectMapper json;

    @Autowired
    jakarta.persistence.EntityManager entidades;

    // La API debe devolver los mismos días aunque el servidor utilice UTC.
    @Test
    void conservaFechasAlConsultarReserva() throws Exception {
        var dto = solicitud(1, 2);
        var entrada = LocalDate.of(LocalDate.now().getYear() + 1, 11, 1);
        dto.setEntrada(entrada);
        dto.setSalida(entrada.plusDays(1));
        var resultado = hotel.reservar(dto, null);
        entidades.flush();
        entidades.clear();

        var reserva = consultas.listar().stream()
            .filter(r -> r.getIdReservacion().equals(resultado.get("idReservacion")))
            .findFirst().orElseThrow();
        var respuesta = json.readTree(json.writeValueAsString(reserva));
        assertEquals(entrada.toString(), respuesta.get("fechaIngreso").asText());
        assertEquals(entrada.plusDays(1).toString(), respuesta.get("fechaSalida").asText());
    }

    // Prepara una solicitud válida sin depender de la fecha en que se ejecuta la prueba.
    private SolicitudReservaDTO solicitud(int entrada, int salida) {
        SolicitudReservaDTO dto = new SolicitudReservaDTO();
        dto.setNombre("Huésped de prueba");
        dto.setDpi("1234567890123");
        dto.setTelefono("55551234");
        dto.setEntrada(LocalDate.now(java.time.ZoneId.of("America/Guatemala")).plusDays(entrada));
        dto.setSalida(LocalDate.now(java.time.ZoneId.of("America/Guatemala")).plusDays(salida));
        dto.setHabitaciones(List.of(habitaciones.findAll().get(0).getIdHabitacion()));
        return dto;
    }

    // El servidor calcula el precio y conserva la tarifa en cada detalle.
    @Test
    void calculaTotalYPrecioPactado() {
        var dto = solicitud(1, 4);
        var tarifa = habitaciones.findById(dto.getHabitaciones().get(0)).orElseThrow().getPrecioNoche();
        var resultado = hotel.reservar(dto, null);
        assertEquals(
            0,
            tarifa
                .multiply(java.math.BigDecimal.valueOf(3))
                .compareTo((java.math.BigDecimal) resultado.get("total"))
        );
        var detalle = detalles
            .findByIdReservacion_IdReservacion((Integer) resultado.get("idReservacion"))
            .get(0);
        assertEquals(0, tarifa.compareTo(detalle.getPrecioPactado()));
        assertEquals("PENDIENTE", resultado.get("estadoReservacion"));
    }

    // Dos estancias no pueden ocupar las mismas noches de una habitación.
    @Test
    void rechazaCrucesDeFechas() {
        hotel.reservar(solicitud(1, 4), null);
        assertThrows(ResponseStatusException.class, () -> hotel.reservar(solicitud(2, 5), null));
    }

    // Una salida y una entrada pueden compartir el mismo día.
    @Test
    void permiteEstanciasConsecutivas() {
        hotel.reservar(solicitud(1, 3), null);
        assertDoesNotThrow(() -> hotel.reservar(solicitud(3, 5), null));
    }

    // Cancelar libera las noches sin borrar el historial de la reserva.
    @Test
    void cancelarLiberaDisponibilidad() {
        var resultado = hotel.reservar(solicitud(1, 3), null);
        hotel.cambiarEstado((Integer) resultado.get("idReservacion"), "CANCELADA");
        assertDoesNotThrow(() -> hotel.reservar(solicitud(1, 3), null));
    }

    // Una solicitud pendiente no puede pasar directamente a una estancia.
    @Test
    void impideSaltarEstados() {
        var resultado = hotel.reservar(solicitud(1, 3), null);
        assertThrows(ResponseStatusException.class, () ->
            hotel.cambiarEstado((Integer) resultado.get("idReservacion"), "HOSPEDADO")
        );
    }

    // Una habitación en mantenimiento no se ofrece para reservar.
    @Test
    void bloqueaMantenimiento() {
        var dto = solicitud(1, 3);
        hotel.estadoHabitacion(dto.getHabitaciones().get(0), "MANTENIMIENTO");
        assertThrows(ResponseStatusException.class, () -> hotel.reservar(dto, null));
    }

    // Mantenimiento no puede dejar sin habitación a una reservación pendiente.
    @Test
    void protegeReservasDelMantenimiento() {
        var dto = solicitud(1, 3);
        hotel.reservar(dto, null);
        assertThrows(ResponseStatusException.class, () ->
            hotel.estadoHabitacion(dto.getHabitaciones().get(0), "MANTENIMIENTO")
        );
    }

    // Las fechas iguales no representan una noche de alojamiento.
    @Test
    void rechazaFechasInvalidas() {
        assertThrows(ResponseStatusException.class, () -> hotel.reservar(solicitud(1, 1), null));
    }

    // Un DPI conocido no permite reemplazar los datos del huésped desde la web pública.
    @Test
    void conservaDatosDelHuesped() {
        hotel.reservar(solicitud(1, 2), null);
        var dto = solicitud(3, 4);
        dto.setNombre("Otro nombre");
        assertThrows(ResponseStatusException.class, () -> hotel.reservar(dto, null));
        assertEquals("Huésped de prueba", huespedes.findByDpi(dto.getDpi()).orElseThrow().getNombre());
    }

    // No se permite cobrar dos veces una habitación repetida dentro de la selección.
    @Test
    void rechazaHabitacionesRepetidas() {
        var dto = solicitud(1, 3);
        dto.setHabitaciones(List.of(dto.getHabitaciones().get(0), dto.getHabitaciones().get(0)));
        assertThrows(ResponseStatusException.class, () -> hotel.reservar(dto, null));
    }

    // La salida deja limpieza pendiente y recepción puede preparar de nuevo la habitación.
    @Test
    void completaEntradaSalidaYLimpieza() {
        var dto = solicitud(1, 3);
        var resultado = hotel.reservar(dto, null);
        Integer id = (Integer) resultado.get("idReservacion");
        hotel.cambiarEstado(id, "CONFIRMADA");
        var reserva = reservas.findById(id).orElseThrow();
        var hoy = LocalDate.now(java.time.ZoneId.of("America/Guatemala"));
        reserva.setFechaIngreso(java.sql.Date.valueOf(hoy.minusDays(1)));
        reserva.setFechaSalida(java.sql.Date.valueOf(hoy.plusDays(1)));
        reservas.saveAndFlush(reserva);
        hotel.cambiarEstado(id, "HOSPEDADO");
        assertEquals(
            "OCUPADA",
            habitaciones.findById(dto.getHabitaciones().get(0)).orElseThrow().getEstadoHabitacion()
        );
        hotel.cambiarEstado(id, "FINALIZADA");
        assertEquals(
            "LIMPIEZA",
            habitaciones.findById(dto.getHabitaciones().get(0)).orElseThrow().getEstadoHabitacion()
        );
        hotel.estadoHabitacion(dto.getHabitaciones().get(0), "DISPONIBLE");
        assertEquals(
            "DISPONIBLE",
            habitaciones.findById(dto.getHabitaciones().get(0)).orElseThrow().getEstadoHabitacion()
        );
    }
}

