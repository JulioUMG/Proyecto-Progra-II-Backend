package com.hotel404.service;

import com.hotel404.dto.UsuariosDTO;
import com.hotel404.entity.Perfiles;
import com.hotel404.entity.Usuarios;
import com.hotel404.repository.PerfilesRepository;
import com.hotel404.repository.UsuariosRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuariosService {

    private final UsuariosRepository usuariosRepo;
    private final PerfilesRepository perfilesRepo;
    private final org.springframework.security.crypto.password.PasswordEncoder encoder;

    public UsuariosService(
        UsuariosRepository usuariosRepo,
        PerfilesRepository perfilesRepo,
        org.springframework.security.crypto.password.PasswordEncoder encoder
    ) {
        this.perfilesRepo = perfilesRepo;
        this.encoder = encoder;
        this.usuariosRepo = usuariosRepo;
    }

    // MÃ©todo para convertir a Dto
    private UsuariosDTO convertirDto(Usuarios usuarios) {
        UsuariosDTO dto = new UsuariosDTO();

        dto.setIdUsuario(usuarios.getIdUsuario());
        dto.setUsuario(usuarios.getUsuario());
        dto.setCorreo(usuarios.getCorreo());
        dto.setNombre(usuarios.getNombre());
        dto.setEstado(usuarios.getEstado());

        dto.setIdPerfil(usuarios.getIdPerfil() != null ? usuarios.getIdPerfil().getIdPerfil() : null);
        return dto;
    }

    // MÃ©todo para converitr a Entidad
    private Usuarios convertirEntidad(UsuariosDTO dto) {
        Usuarios usuarios = new Usuarios();

        usuarios.setIdUsuario(dto.getIdUsuario());
        usuarios.setUsuario(dto.getUsuario());
        usuarios.setCorreo(dto.getCorreo());
        usuarios.setNombre(dto.getNombre());
        usuarios.setEstado(dto.getEstado());
        usuarios.setContrasena(encoder.encode(dto.getContrasena()));

        Perfiles perfil = perfilesRepo
            .findById(dto.getIdPerfil())
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Perfil no encontrado con id: " + dto.getIdPerfil()
                )
            );
        if (!perfil.getEstado()) throw new RuntimeException("El perfil está desactivado");
        usuarios.setIdPerfil(perfil);

        return usuarios;
    }

    // listar Usuarios
    @Transactional
    public List<UsuariosDTO> listarUsuarios() {
        return usuariosRepo.findAll().stream().map(this::convertirDto).collect(Collectors.toList());
    }

    // listar Usuarios Activos
    @Transactional(readOnly = true)
    public List<UsuariosDTO> listarUsuariosActivos() {
        return usuariosRepo.findByEstadoTrue().stream().map(this::convertirDto).collect(Collectors.toList());
    }

    // Crear Usuario
    @Transactional
    public UsuariosDTO crearUsuario(UsuariosDTO usuarioDto) {
        if (
            usuarioDto.getNombre() == null ||
            usuarioDto.getNombre().isBlank() ||
            usuarioDto.getUsuario() == null ||
            !usuarioDto.getUsuario().matches("[a-zA-Z0-9._-]{3,50}") ||
            usuarioDto.getCorreo() == null ||
            !usuarioDto.getCorreo().contains("@")
        ) {
            throw new RuntimeException("Revisa nombre, usuario (3 a 50 caracteres) y correo");
        }
        if (
            usuariosRepo.existsByUsuarioIgnoreCase(usuarioDto.getUsuario()) ||
            usuariosRepo.existsByCorreoIgnoreCase(usuarioDto.getCorreo())
        ) {
            throw new RuntimeException("Usuario ya existente");
        }
        if (usuarioDto.getContrasena() == null || usuarioDto.getContrasena().length() < 8) {
            throw new RuntimeException("La contraseña debe tener al menos 8 caracteres");
        }
        Usuarios usuario = convertirEntidad(usuarioDto);
        Usuarios usuarioGuardado = usuariosRepo.save(usuario);
        return convertirDto(usuarioGuardado);
    }

    // Actualizar Usuario
    @Transactional
    public UsuariosDTO actualizarUsuario(Integer idUsuario, UsuariosDTO usuarioDto) {
        Usuarios usuarioExistente = usuariosRepo
            .findById(idUsuario)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Usuario no encontrado con id: " + idUsuario
                )
            );

        if (usuarioDto.getEstado() != null) {
            usuarioExistente.setEstado(usuarioDto.getEstado());
        }

        if (usuarioDto.getCorreo() != null) {
            usuarioExistente.setCorreo(usuarioDto.getCorreo());
        }

        if (usuarioDto.getNombre() != null) {
            usuarioExistente.setNombre(usuarioDto.getNombre());
        }

        if (usuarioDto.getUsuario() != null) {
            usuarioExistente.setUsuario(usuarioDto.getUsuario());
        }

        Usuarios usuarioGuardado = usuariosRepo.save(usuarioExistente);

        return convertirDto(usuarioGuardado);
    }

    // Anular Borrado LÃ³gico
    @Transactional
    public UsuariosDTO anularUsuario(Integer idUsuario) {
        Usuarios usuario = usuariosRepo
            .findById(idUsuario)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "no se encontro el usuario con id" + idUsuario
                )
            );

        usuario.setEstado(false);

        Usuarios usuarioGuardado = usuariosRepo.save(usuario);
        return convertirDto(usuarioGuardado);
    }
}
