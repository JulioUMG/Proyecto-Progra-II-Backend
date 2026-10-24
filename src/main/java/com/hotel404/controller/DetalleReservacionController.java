package com.hotel404.controller;

import com.hotel404.dto.DetalleReservacionDTO;
import com.hotel404.service.DetalleReservacionService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/detalle-reservacion")
@CrossOrigin(origins = "*")
public class DetalleReservacionController {

    @Autowired
    private DetalleReservacionService service;

    @GetMapping
    public ResponseEntity<List<DetalleReservacionDTO>> listar() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleReservacionDTO> obtenerPorId(@PathVariable Integer id) {
        DetalleReservacionDTO dto = service.obtenerPorId(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<DetalleReservacionDTO> crear(@RequestBody DetalleReservacionDTO dto) {
        return new ResponseEntity<>(service.guardar(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetalleReservacionDTO> actualizar(
        @PathVariable Integer id,
        @RequestBody DetalleReservacionDTO dto
    ) {
        DetalleReservacionDTO actualizado = service.actualizar(id, dto);
        return actualizado != null ? ResponseEntity.ok(actualizado) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
