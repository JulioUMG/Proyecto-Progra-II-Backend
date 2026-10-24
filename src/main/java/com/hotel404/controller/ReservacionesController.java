package com.hotel404.controller;

import com.hotel404.dto.MessageResponse;
import com.hotel404.dto.ReservacionesDTO;
import com.hotel404.service.ReservacionesService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservaciones")
@CrossOrigin(origins = "*")
public class ReservacionesController {

    private final ReservacionesService reservacionesService;

    public ReservacionesController(ReservacionesService reservacionesService) {
        this.reservacionesService = reservacionesService;
    }

    // Listar
    @GetMapping
    public ResponseEntity<List<ReservacionesDTO>> listarReservaciones() {
        return ResponseEntity.ok(reservacionesService.listarReservaciones());
    }

    // Activas
    @GetMapping("/activas")
    public ResponseEntity<List<ReservacionesDTO>> listarActivas() {
        return ResponseEntity.ok(reservacionesService.listarActivas());
    }

    // Buscar por ID
    @GetMapping("/{idReservacion}")
    public ResponseEntity<ReservacionesDTO> buscarPorId(@PathVariable Integer idReservacion) {
        return ResponseEntity.ok(reservacionesService.buscarPorId(idReservacion));
    }

    // Buscar por huésped
    @GetMapping("/huesped/{idHuesped}")
    public ResponseEntity<List<ReservacionesDTO>> buscarPorHuesped(@PathVariable Integer idHuesped) {
        return ResponseEntity.ok(reservacionesService.buscarPorHuesped(idHuesped));
    }

    // Buscar por estado
    @GetMapping("/estado/{estadoReservacion}")
    public ResponseEntity<List<ReservacionesDTO>> buscarPorEstado(@PathVariable String estadoReservacion) {
        return ResponseEntity.ok(reservacionesService.buscarPorEstado(estadoReservacion));
    }

    // Crear
    @PostMapping
    public ResponseEntity<MessageResponse> crearReservacion(@RequestBody ReservacionesDTO dto) {
        try {
            reservacionesService.crearReservacion(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                new MessageResponse("Reservacion creada exitosamente")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new MessageResponse("Error al crear reservacion: " + e.getMessage())
            );
        }
    }

    // Actualizar
    @PutMapping("/{idReservacion}")
    public ResponseEntity<MessageResponse> actualizarReservacion(
        @PathVariable Integer idReservacion,
        @RequestBody ReservacionesDTO dto
    ) {
        try {
            reservacionesService.actualizarReservacion(idReservacion, dto);

            return ResponseEntity.ok(new MessageResponse("Reservacion actualizada con éxito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new MessageResponse("Error al actualizar reservacion: " + e.getMessage())
            );
        }
    }

    // Anular
    @PutMapping("/{idReservacion}/anular")
    public ResponseEntity<MessageResponse> anularReservacion(@PathVariable Integer idReservacion) {
        try {
            reservacionesService.anularReservacion(idReservacion);

            return ResponseEntity.ok(new MessageResponse("Reservacion anulada con éxito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new MessageResponse("Error al anular reservacion: " + e.getMessage())
            );
        }
    }

    // Activar
    @PutMapping("/{idReservacion}/activar")
    public ResponseEntity<MessageResponse> activarReservacion(@PathVariable Integer idReservacion) {
        try {
            reservacionesService.activarReservacion(idReservacion);
            return ResponseEntity.ok(new MessageResponse("Reservacion activada con éxito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new MessageResponse("Error al activar reservacion: " + e.getMessage())
            );
        }
    }

    // Eliminar
    @DeleteMapping("/{idReservacion}")
    public ResponseEntity<MessageResponse> eliminarReservacion(@PathVariable Integer idReservacion) {
        try {
            reservacionesService.eliminarReservacion(idReservacion);

            return ResponseEntity.ok(new MessageResponse("Reservacion eliminada con éxito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new MessageResponse("Error al eliminar reservacion: " + e.getMessage())
            );
        }
    }
}
