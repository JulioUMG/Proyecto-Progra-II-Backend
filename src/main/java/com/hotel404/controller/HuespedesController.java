package com.hotel404.controller;

import com.hotel404.dto.HuespedesDTO;
import com.hotel404.dto.MessageResponse;
import com.hotel404.service.HuespedesService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/huespedes")
@CrossOrigin(origins = "*")
public class HuespedesController {

    private final HuespedesService huespedesService;

    public HuespedesController(HuespedesService huespedesService) {
        this.huespedesService = huespedesService;
    }

    // Listar todos
    @GetMapping
    public ResponseEntity<List<HuespedesDTO>> listarHuespedes() {
        return ResponseEntity.ok(huespedesService.listarHuespedes());
    }

    // Listar activos
    @GetMapping("/activos")
    public ResponseEntity<List<HuespedesDTO>> listarHuespedesActivos() {
        return ResponseEntity.ok(huespedesService.listarHuespedesActivos());
    }

    // Buscar por ID
    @GetMapping("/{idHuesped}")
    public ResponseEntity<HuespedesDTO> buscarPorId(@PathVariable Integer idHuesped) {
        return ResponseEntity.ok(huespedesService.buscarPorId(idHuesped));
    }

    // Crear
    @PostMapping
    public ResponseEntity<MessageResponse> crearHuesped(@RequestBody HuespedesDTO dto) {
        try {
            huespedesService.crearHuesped(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                new MessageResponse("Huesped creado exitosamente")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new MessageResponse("Error al crear huesped: " + e.getMessage())
            );
        }
    }

    // Actualizar
    @PutMapping("/{idHuesped}")
    public ResponseEntity<MessageResponse> actualizarHuesped(
        @PathVariable Integer idHuesped,
        @RequestBody HuespedesDTO dto
    ) {
        try {
            huespedesService.actualizarHuesped(idHuesped, dto);
            return ResponseEntity.ok(new MessageResponse("Huesped actualizado con éxito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new MessageResponse("Error al actualizar huesped: " + e.getMessage())
            );
        }
    }

    // Anular
    @PutMapping("/{idHuesped}/anular")
    public ResponseEntity<MessageResponse> anularHuesped(@PathVariable Integer idHuesped) {
        try {
            huespedesService.anularHuesped(idHuesped);
            return ResponseEntity.ok(new MessageResponse("Huesped anulado con éxito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new MessageResponse("Error al anular huesped: " + e.getMessage())
            );
        }
    }

    // Activar
    @PutMapping("/{idHuesped}/activar")
    public ResponseEntity<MessageResponse> activarHuesped(@PathVariable Integer idHuesped) {
        try {
            huespedesService.activarHuesped(idHuesped);
            return ResponseEntity.ok(new MessageResponse("Huesped activado con éxito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new MessageResponse("Error al activar huesped: " + e.getMessage())
            );
        }
    }

    // Eliminar
    @DeleteMapping("/{idHuesped}")
    public ResponseEntity<MessageResponse> eliminarHuesped(@PathVariable Integer idHuesped) {
        try {
            huespedesService.eliminarHuesped(idHuesped);
            return ResponseEntity.ok(new MessageResponse("Huesped eliminado con éxito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new MessageResponse("Error al eliminar huesped: " + e.getMessage())
            );
        }
    }
}
