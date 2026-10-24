package com.hotel404.service;

import com.hotel404.dto.ReservaVistaDTO;
import com.hotel404.repository.DetalleReservacionRepository;
import com.hotel404.repository.ReservacionesRepository;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsultaHotelService {

    private final ReservacionesRepository reservas;
    private final DetalleReservacionRepository detalles;

    public ConsultaHotelService(ReservacionesRepository reservas, DetalleReservacionRepository detalles) {
        this.reservas = reservas;
        this.detalles = detalles;
    }

    // Conserva el día de la columna DATE al preparar la respuesta del navegador.
    private LocalDate fechaSinHora(Date fecha) {
        return new java.sql.Date(fecha.getTime()).toLocalDate();
    }

    // Une los datos del huésped y las habitaciones para mostrar la tabla de recepción.
    @Transactional(readOnly = true)
    public List<ReservaVistaDTO> listar() {
        return reservas
            .findAll()
            .stream()
            .map(reserva -> {
                ReservaVistaDTO dto = new ReservaVistaDTO();
                dto.setIdReservacion(reserva.getIdReservacion());
                dto.setNombre(reserva.getIdHuesped().getNombre());
                dto.setDpi(reserva.getIdHuesped().getDpi());
                dto.setTelefono(reserva.getIdHuesped().getTelefono());
                dto.setFechaIngreso(fechaSinHora(reserva.getFechaIngreso()));
                dto.setFechaSalida(fechaSinHora(reserva.getFechaSalida()));
                dto.setTotal(reserva.getTotal());
                dto.setEstadoReservacion(reserva.getEstadoReservacion());
                dto.setHabitaciones(
                    detalles
                        .findByIdReservacion_IdReservacion(reserva.getIdReservacion())
                        .stream()
                        .filter(d -> d.getEstado())
                        .map(d -> d.getIdHabitacion().getNumero())
                        .toList()
                );
                return dto;
            })
            .sorted(java.util.Comparator.comparing(ReservaVistaDTO::getIdReservacion).reversed())
            .toList();
    }
}
