//PERFILES CONTROLLER
package com.hotel404.controller;

import com.hotel404.dto.MessageResponse;
import com.hotel404.dto.PerfilesDTO;
import com.hotel404.service.PerfilesService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//Perfil, perfil, Perfiles, perfiles
@RestController
@RequestMapping("/perfiles")
@CrossOrigin(origins = "*")
public class PerfilesController {

    private final PerfilesService perfilesService;

    public PerfilesController(PerfilesService perfilesService) {
        this.perfilesService = perfilesService;
    }

    // get listar todos
    @GetMapping
    public ResponseEntity<List<PerfilesDTO>> listarPerfiles() {
        return ResponseEntity.ok(perfilesService.listarPerfiles());
    }

    // get listar activos
    @GetMapping("/activos")
    public ResponseEntity<List<PerfilesDTO>> listarPerfilesActivos() {
        return ResponseEntity.ok(perfilesService.listarPerfilesActivos());
    }

    // post crear
    @PostMapping
    public ResponseEntity<MessageResponse> crearPerfil(@RequestBody PerfilesDTO perfilDto) {
        try {
            perfilesService.crearPerfil(perfilDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                new MessageResponse("perfil creado exitosamente")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new MessageResponse("Error al crear cliente: " + e.getMessage())
            );
        }
    }

    // put actualizar
    @PutMapping("/{idPerfil}")
    public ResponseEntity<MessageResponse> actualizarPerfil(
        @PathVariable Integer idPerfil,
        @RequestBody PerfilesDTO perfilesdto
    ) {
        try {
            perfilesService.actualizarPerfil(idPerfil, perfilesdto);
            return ResponseEntity.ok(new MessageResponse("Perfil actualizado con éxito"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new MessageResponse("Error al actualizar el perfil: " + e.getMessage())
            );
        }
    }

    // put anular
    @PutMapping("/{idPerfil}/anular")
    public ResponseEntity<MessageResponse> anularPerfil(@PathVariable Integer idPerfil) {
        try {
            perfilesService.anularPerfil(idPerfil);
            return ResponseEntity.ok(new MessageResponse("Perfil anulado con éxito"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new MessageResponse("Error al anular el perfil: " + e.getMessage())
            );
        }
    }
}
