package com.hotel404.controller;

import com.hotel404.dto.HabitacionesDTO;
import com.hotel404.service.HabitacionesService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/habitaciones")
@CrossOrigin(origins = "*")
public class HabitacionesController {

    @Autowired
    private HabitacionesService service;

    @GetMapping
    public ResponseEntity<List<HabitacionesDTO>> listar() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HabitacionesDTO> obtenerPorId(@PathVariable Integer id) {
        HabitacionesDTO dto = service.obtenerPorId(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<HabitacionesDTO> crear(@RequestBody HabitacionesDTO dto) {
        return new ResponseEntity<>(service.guardar(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HabitacionesDTO> actualizar(
        @PathVariable Integer id,
        @RequestBody HabitacionesDTO dto
    ) {
        HabitacionesDTO actualizado = service.actualizar(id, dto);
        return actualizado != null ? ResponseEntity.ok(actualizado) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
