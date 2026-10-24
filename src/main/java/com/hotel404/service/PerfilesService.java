//Perfil Service
package com.hotel404.service;

import com.hotel404.dto.PerfilesDTO;
import com.hotel404.entity.Perfiles;
import com.hotel404.repository.PerfilesRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PerfilesService {

    private final PerfilesRepository perfilesRepo;

    public PerfilesService(PerfilesRepository perfilesRepo) {
        this.perfilesRepo = perfilesRepo;
    }

    // Método para convertir a Dto
    private PerfilesDTO convertirDto(Perfiles perfiles) {
        PerfilesDTO dto = new PerfilesDTO();

        dto.setIdPerfil(perfiles.getIdPerfil());
        dto.setNombre(perfiles.getNombre());
        dto.setEstado(perfiles.getEstado());
        return dto;
    }

    // Método para converitr a Entidad
    private Perfiles convertirEntidad(PerfilesDTO dto) {
        Perfiles perfiles = new Perfiles();

        perfiles.setIdPerfil(dto.getIdPerfil());
        perfiles.setNombre(dto.getNombre());
        perfiles.setEstado(true);
        return perfiles;
    }

    // listar Perfiles
    @Transactional
    public List<PerfilesDTO> listarPerfiles() {
        return perfilesRepo.findAll().stream().map(this::convertirDto).collect(Collectors.toList());
    }

    // listar Perfiles Activos
    @Transactional(readOnly = true)
    public List<PerfilesDTO> listarPerfilesActivos() {
        return perfilesRepo.findByEstadoTrue().stream().map(this::convertirDto).collect(Collectors.toList());
    }

    // Crear Perfil
    @Transactional
    public PerfilesDTO crearPerfil(PerfilesDTO perfilDto) {
        if (perfilesRepo.existsByNombreIgnoreCase(perfilDto.getNombre())) {
            throw new RuntimeException("Perfil ya existente");
        }
        Perfiles perfil = convertirEntidad(perfilDto);
        Perfiles perfilGuardado = perfilesRepo.save(perfil);
        return convertirDto(perfilGuardado);
    }

    // Actualizar Perfil
    @Transactional
    public PerfilesDTO actualizarPerfil(Integer idPerfil, PerfilesDTO perfilDto) {
        Perfiles perfilExistente = perfilesRepo
            .findById(idPerfil)
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil no encontrado con id: " + idPerfil)
            );

        if (perfilDto.getEstado() != null) {
            perfilExistente.setEstado(perfilDto.getEstado());
        }
        if (perfilDto.getNombre() != null) {
            perfilExistente.setNombre(perfilDto.getNombre());
        }

        Perfiles perfilGuardado = perfilesRepo.save(perfilExistente);

        return convertirDto(perfilGuardado);
    }

    // Anular Borrado Lógico
    @Transactional
    public PerfilesDTO anularPerfil(Integer idPerfil) {
        Perfiles perfil = perfilesRepo
            .findById(idPerfil)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "no se encontro el perfil con id" + idPerfil
                )
            );

        perfil.setEstado(false);

        Perfiles perfilGuardado = perfilesRepo.save(perfil);
        return convertirDto(perfilGuardado);
    }
}
