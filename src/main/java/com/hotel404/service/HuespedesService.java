package com.hotel404.service;

import com.hotel404.dto.HuespedesDTO;
import com.hotel404.entity.Huespedes;
import com.hotel404.repository.HuespedesRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class HuespedesService {

    private final HuespedesRepository huespedesRepo;

    public HuespedesService(HuespedesRepository huespedesRepo) {
        this.huespedesRepo = huespedesRepo;
    }

    // Convertir Entidad -> DTO
    private HuespedesDTO convertirDto(Huespedes huesped) {
        HuespedesDTO dto = new HuespedesDTO();
        dto.setIdHuesped(huesped.getIdHuesped());
        dto.setNombre(huesped.getNombre());
        dto.setDpi(huesped.getDpi());
        dto.setTelefono(huesped.getTelefono());
        dto.setEstado(huesped.getEstado());
        return dto;
    }

    // Convertir DTO -> Entidad
    private Huespedes convertirEntidad(HuespedesDTO dto) {
        Huespedes huesped = new Huespedes();
        huesped.setIdHuesped(dto.getIdHuesped());
        huesped.setNombre(dto.getNombre());
        huesped.setDpi(dto.getDpi());
        huesped.setTelefono(dto.getTelefono());
        huesped.setEstado(dto.isEstado());
        return huesped;
    }

    // Listar todos
    @Transactional(readOnly = true)
    public List<HuespedesDTO> listarHuespedes() {
        return huespedesRepo.findAll().stream().map(this::convertirDto).collect(Collectors.toList());
    }

    // Listar activos
    @Transactional(readOnly = true)
    public List<HuespedesDTO> listarHuespedesActivos() {
        return huespedesRepo.findByEstadoTrue().stream().map(this::convertirDto).collect(Collectors.toList());
    }

    // Buscar por ID
    @Transactional(readOnly = true)
    public HuespedesDTO buscarPorId(Integer idHuesped) {
        Huespedes huesped = huespedesRepo
            .findById(idHuesped)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Huesped no encontrado con id: " + idHuesped
                )
            );
        return convertirDto(huesped);
    }

    // Crear
    @Transactional
    public HuespedesDTO crearHuesped(HuespedesDTO dto) {
        if (huespedesRepo.existsByDpi(dto.getDpi())) {
            throw new RuntimeException("Ya existe un huesped registrado con DPI: " + dto.getDpi());
        }
        Huespedes huesped = convertirEntidad(dto);
        Huespedes huespedGuardado = huespedesRepo.save(huesped);
        return convertirDto(huespedGuardado);
    }

    // Actualizar
    @Transactional
    public HuespedesDTO actualizarHuesped(Integer idHuesped, HuespedesDTO dto) {
        Huespedes huesped = huespedesRepo
            .findById(idHuesped)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Huesped no encontrado con id: " + idHuesped
                )
            );

        if (dto.getNombre() != null) {
            huesped.setNombre(dto.getNombre());
        }

        if (dto.getDpi() != null) {
            if (!dto.getDpi().matches("[0-9]{13}")) throw new RuntimeException(
                "El DPI debe tener 13 dígitos"
            );
            huespedesRepo.findByDpi(dto.getDpi()).ifPresent(otro -> {
                if (!otro.getIdHuesped().equals(idHuesped)) throw new RuntimeException(
                    "El DPI pertenece a otro huésped"
                );
            });
            huesped.setDpi(dto.getDpi());
        }

        if (dto.getTelefono() != null) {
            huesped.setTelefono(dto.getTelefono());
        }
        huesped.setEstado(dto.isEstado());
        Huespedes huespedGuardado = huespedesRepo.save(huesped);
        return convertirDto(huespedGuardado);
    }

    // Anular
    @Transactional
    public HuespedesDTO anularHuesped(Integer idHuesped) {
        Huespedes huesped = huespedesRepo
            .findById(idHuesped)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Huesped no encontrado con id: " + idHuesped
                )
            );
        huesped.setEstado(false);
        Huespedes huespedGuardado = huespedesRepo.save(huesped);
        return convertirDto(huespedGuardado);
    }

    // Activar
    @Transactional
    public HuespedesDTO activarHuesped(Integer idHuesped) {
        Huespedes huesped = huespedesRepo
            .findById(idHuesped)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Huesped no encontrado con id: " + idHuesped
                )
            );
        huesped.setEstado(true);
        return convertirDto(huespedesRepo.save(huesped));
    }

    // Eliminar físicamente
    @Transactional
    public void eliminarHuesped(Integer idHuesped) {
        if (!huespedesRepo.existsById(idHuesped)) {
            throw new RuntimeException("Huesped no encontrado con id: " + idHuesped);
        }
        huespedesRepo.deleteById(idHuesped);
    }
}
