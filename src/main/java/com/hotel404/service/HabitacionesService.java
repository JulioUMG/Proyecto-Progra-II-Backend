package com.hotel404.service;

import com.hotel404.dto.HabitacionesDTO;
import com.hotel404.entity.Habitaciones;
import com.hotel404.repository.HabitacionesRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HabitacionesService {

    @Autowired
    private HabitacionesRepository repository;

    private HabitacionesDTO convertirADTO(Habitaciones entidad) {
        HabitacionesDTO dto = new HabitacionesDTO();
        dto.setIdHabitacion(entidad.getIdHabitacion());
        dto.setNumero(entidad.getNumero());
        dto.setTipo(entidad.getTipo());
        dto.setPrecioNoche(entidad.getPrecioNoche());
        dto.setEstado(entidad.getEstado());
        dto.setEstadoHabitacion(entidad.getEstadoHabitacion());
        return dto;
    }

    private Habitaciones convertirAEntidad(HabitacionesDTO dto) {
        Habitaciones entidad = new Habitaciones();
        entidad.setIdHabitacion(dto.getIdHabitacion());
        entidad.setNumero(dto.getNumero());
        entidad.setTipo(dto.getTipo());
        entidad.setPrecioNoche(dto.getPrecioNoche());
        entidad.setEstado(dto.isEstado());
        entidad.setEstadoHabitacion(dto.getEstadoHabitacion()); // Ya no llegarÃ¡ null
        return entidad;
    }

    public List<HabitacionesDTO> obtenerTodas() {
        return repository.findAll().stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public HabitacionesDTO obtenerPorId(Integer id) {
        return repository.findById(id).map(this::convertirADTO).orElse(null);
    }

    public HabitacionesDTO guardar(HabitacionesDTO dto) {
        validarDatos(dto, null);
        Habitaciones entidad = convertirAEntidad(dto);
        Habitaciones guardada = repository.save(entidad);
        return convertirADTO(guardada);
    }

    public HabitacionesDTO actualizar(Integer id, HabitacionesDTO dto) {
        if (repository.existsById(id)) {
            Habitaciones entidad = convertirAEntidad(dto);
            entidad.setIdHabitacion(id);
            return convertirADTO(repository.save(entidad));
        }
        return null;
    }

    public void eliminar(Integer id) {
        repository.deleteById(id);
    }

    // Cambia los datos comerciales y conserva el estado operativo de la habitación.
    @org.springframework.transaction.annotation.Transactional
    public HabitacionesDTO actualizarDatos(Integer id, HabitacionesDTO dto) {
        Habitaciones habitacion = repository
            .bloquearPorIds(java.util.List.of(id))
            .stream()
            .findFirst()
            .orElseThrow(() ->
                new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND,
                    "Habitación no encontrada"
                )
            );
        validarDatos(dto, id);
        habitacion.setNumero(dto.getNumero().trim());
        habitacion.setTipo(dto.getTipo().trim());
        habitacion.setPrecioNoche(dto.getPrecioNoche());
        return convertirADTO(repository.save(habitacion));
    }

    // Revisa el número, el tipo y el precio antes de guardar.
    private void validarDatos(HabitacionesDTO dto, Integer id) {
        if (
            dto.getNumero() == null ||
            dto.getNumero().isBlank() ||
            dto.getNumero().length() > 10 ||
            dto.getTipo() == null ||
            dto.getTipo().isBlank() ||
            dto.getTipo().length() > 40 ||
            dto.getPrecioNoche() == null ||
            dto.getPrecioNoche().signum() <= 0 ||
            dto.getPrecioNoche().compareTo(new java.math.BigDecimal("99999999.99")) > 0
        ) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.BAD_REQUEST,
                "Revisa el número, tipo y precio de la habitación"
            );
        }
        boolean repetido = repository
            .findAll()
            .stream()
            .anyMatch(
                h -> h.getNumero().equalsIgnoreCase(dto.getNumero().trim()) && !h.getIdHabitacion().equals(id)
            );
        if (repetido) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.CONFLICT,
                "Ese número de habitación ya existe"
            );
        }
    }
}
