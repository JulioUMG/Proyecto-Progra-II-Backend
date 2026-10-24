package com.hotel404.service;

import com.hotel404.dto.ReservacionesDTO;
import com.hotel404.entity.Huespedes;
import com.hotel404.entity.Reservaciones;
import com.hotel404.entity.Usuarios;
import com.hotel404.repository.HuespedesRepository;
import com.hotel404.repository.ReservacionesRepository;
import com.hotel404.repository.UsuariosRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ReservacionesService {

    private final ReservacionesRepository reservacionesRepo;
    private final HuespedesRepository huespedesRepo;
    private final UsuariosRepository usuariosRepo;

    public ReservacionesService(
        ReservacionesRepository reservacionesRepo,
        HuespedesRepository huespedesRepo,
        UsuariosRepository usuariosRepo
    ) {
        this.reservacionesRepo = reservacionesRepo;
        this.huespedesRepo = huespedesRepo;
        this.usuariosRepo = usuariosRepo;
    }

    // Entidad -> DTO
    private ReservacionesDTO convertirDto(Reservaciones reservacion) {
        ReservacionesDTO dto = new ReservacionesDTO();
        dto.setIdReservacion(reservacion.getIdReservacion());
        dto.setFechaReservacion(reservacion.getFechaReservacion());
        dto.setFechaIngreso(reservacion.getFechaIngreso());
        dto.setFechaSalida(reservacion.getFechaSalida());
        dto.setTotal(reservacion.getTotal());
        dto.setEstado(reservacion.getEstado());
        dto.setEstadoReservacion(reservacion.getEstadoReservacion());
        dto.setIdHuesped(
            reservacion.getIdHuesped() != null ? reservacion.getIdHuesped().getIdHuesped() : null
        );
        dto.setIdUsuario(
            reservacion.getIdUsuario() != null ? reservacion.getIdUsuario().getIdUsuario() : null
        );

        return dto;
    }

    // DTO - Entidad
    private Reservaciones convertirEntidad(ReservacionesDTO dto) {
        Reservaciones reservacion = new Reservaciones();
        reservacion.setIdReservacion(dto.getIdReservacion());
        reservacion.setFechaReservacion(dto.getFechaReservacion());
        reservacion.setFechaIngreso(dto.getFechaIngreso());
        reservacion.setFechaSalida(dto.getFechaSalida());
        reservacion.setTotal(dto.getTotal());
        reservacion.setEstado(dto.isEstado());
        reservacion.setEstadoReservacion(dto.getEstadoReservacion());

        // Buscar huésped
        Huespedes huesped = huespedesRepo
            .findById(dto.getIdHuesped())
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Huesped no encontrado con id: " + dto.getIdHuesped()
                )
            );

        reservacion.setIdHuesped(huesped);

        // Buscar usuario
        Usuarios usuario = usuariosRepo
            .findById(dto.getIdUsuario())
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Usuario no encontrado con id: " + dto.getIdUsuario()
                )
            );

        reservacion.setIdUsuario(usuario);

        return reservacion;
    }

    // Listar
    @Transactional(readOnly = true)
    public List<ReservacionesDTO> listarReservaciones() {
        return reservacionesRepo.findAll().stream().map(this::convertirDto).collect(Collectors.toList());
    }

    // Activas
    @Transactional(readOnly = true)
    public List<ReservacionesDTO> listarActivas() {
        return reservacionesRepo
            .findByEstadoTrue()
            .stream()
            .map(this::convertirDto)
            .collect(Collectors.toList());
    }

    // Buscar por ID
    @Transactional(readOnly = true)
    public ReservacionesDTO buscarPorId(Integer idReservacion) {
        Reservaciones reservacion = reservacionesRepo
            .findById(idReservacion)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Reservacion no encontrada con id: " + idReservacion
                )
            );

        return convertirDto(reservacion);
    }

    // Buscar por huésped
    @Transactional(readOnly = true)
    public List<ReservacionesDTO> buscarPorHuesped(Integer idHuesped) {
        return reservacionesRepo
            .findByIdHuesped_IdHuesped(idHuesped)
            .stream()
            .map(this::convertirDto)
            .collect(Collectors.toList());
    }

    // Buscar por estado de reservación
    @Transactional(readOnly = true)
    public List<ReservacionesDTO> buscarPorEstado(String estadoReservacion) {
        return reservacionesRepo
            .findByEstadoReservacionIgnoreCase(estadoReservacion)
            .stream()
            .map(this::convertirDto)
            .collect(Collectors.toList());
    }

    // Crear
    @Transactional
    public ReservacionesDTO crearReservacion(ReservacionesDTO dto) {
        Reservaciones reservacion = convertirEntidad(dto);
        Reservaciones reservacionGuardada = reservacionesRepo.save(reservacion);
        return convertirDto(reservacionGuardada);
    }

    // Actualizar
    @Transactional
    public ReservacionesDTO actualizarReservacion(Integer idReservacion, ReservacionesDTO dto) {
        Reservaciones reservacion = reservacionesRepo
            .findById(idReservacion)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Reservacion no encontrada con id: " + idReservacion
                )
            );

        if (dto.getFechaReservacion() != null) {
            reservacion.setFechaReservacion(dto.getFechaReservacion());
        }
        if (dto.getFechaIngreso() != null) {
            reservacion.setFechaIngreso(dto.getFechaIngreso());
        }

        if (dto.getFechaSalida() != null) {
            reservacion.setFechaSalida(dto.getFechaSalida());
        }

        if (dto.getTotal() != null) {
            reservacion.setTotal(dto.getTotal());
        }

        if (dto.getEstadoReservacion() != null) {
            reservacion.setEstadoReservacion(dto.getEstadoReservacion());
        }

        reservacion.setEstado(dto.isEstado());

        if (dto.getIdHuesped() != null) {
            Huespedes huesped = huespedesRepo
                .findById(dto.getIdHuesped())
                .orElseThrow(() ->
                    new RuntimeException("Huesped no encontrado con id: " + dto.getIdHuesped())
                );

            reservacion.setIdHuesped(huesped);
        }

        if (dto.getIdUsuario() != null) {
            Usuarios usuario = usuariosRepo
                .findById(dto.getIdUsuario())
                .orElseThrow(() ->
                    new RuntimeException("Usuario no encontrado con id: " + dto.getIdUsuario())
                );

            reservacion.setIdUsuario(usuario);
        }
        Reservaciones guardada = reservacionesRepo.save(reservacion);
        return convertirDto(guardada);
    }

    // Anular
    @Transactional
    public ReservacionesDTO anularReservacion(Integer idReservacion) {
        Reservaciones reservacion = reservacionesRepo
            .findById(idReservacion)
            .orElseThrow(() -> new RuntimeException("Reservacion no encontrada con id: " + idReservacion));
        reservacion.setEstado(false);
        Reservaciones guardada = reservacionesRepo.save(reservacion);
        return convertirDto(guardada);
    }

    // Activar
    @Transactional
    public ReservacionesDTO activarReservacion(Integer idReservacion) {
        Reservaciones reservacion = reservacionesRepo
            .findById(idReservacion)
            .orElseThrow(() -> new RuntimeException("Reservacion no encontrada con id: " + idReservacion));
        reservacion.setEstado(true);
        return convertirDto(reservacionesRepo.save(reservacion));
    }

    // Eliminar
    @Transactional
    public void eliminarReservacion(Integer idReservacion) {
        if (!reservacionesRepo.existsById(idReservacion)) {
            throw new RuntimeException("Reservacion no encontrada con id: " + idReservacion);
        }

        reservacionesRepo.deleteById(idReservacion);
    }
}
