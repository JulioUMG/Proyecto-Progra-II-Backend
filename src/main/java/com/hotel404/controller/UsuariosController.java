package com.hotel404.controller;

import com.hotel404.dto.MessageResponse;
import com.hotel404.dto.UsuariosDTO;
import com.hotel404.service.UsuariosService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*")
public class UsuariosController {

    private final UsuariosService usuariosService;

    public UsuariosController(UsuariosService usuariosService) {
        this.usuariosService = usuariosService;
    }

    // get listar todos
    @GetMapping
    public ResponseEntity<List<UsuariosDTO>> listarUsuarios() {
        return ResponseEntity.ok(usuariosService.listarUsuarios());
    }

    // get listar activos
    @GetMapping("/activos")
    public ResponseEntity<List<UsuariosDTO>> listarUsuariosActivos() {
        return ResponseEntity.ok(usuariosService.listarUsuariosActivos());
    }

    // post crear
    @PostMapping
    public ResponseEntity<MessageResponse> crearUsuario(@RequestBody UsuariosDTO usuarioDto) {
        try {
            usuariosService.crearUsuario(usuarioDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                new MessageResponse("usuario creado exitosamente")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new MessageResponse("Error al crear cliente: " + e.getMessage())
            );
        }
    }

    // put actualizar
    @PutMapping("/{idUsuario}")
    public ResponseEntity<MessageResponse> actualizarUsuario(
        @PathVariable Integer idUsuario,
        @RequestBody UsuariosDTO usuariosdto
    ) {
        try {
            usuariosService.actualizarUsuario(idUsuario, usuariosdto);
            return ResponseEntity.ok(new MessageResponse("Usuario actualizado con éxito"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new MessageResponse("Error al actualizar el usuario: " + e.getMessage())
            );
        }
    }

    // put anular
    @PutMapping("/{idUsuario}/anular")
    public ResponseEntity<MessageResponse> anularUsuario(@PathVariable Integer idUsuario) {
        try {
            usuariosService.anularUsuario(idUsuario);
            return ResponseEntity.ok(new MessageResponse("Usuario anulado con éxito"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new MessageResponse("Error al anular el usuario: " + e.getMessage())
            );
        }
    }
}
