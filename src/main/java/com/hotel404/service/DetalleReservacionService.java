package com.hotel404.service;

import com.hotel404.dto.DetalleReservacionDTO;
import com.hotel404.entity.DetalleReservacion;
import com.hotel404.repository.DetalleReservacionRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DetalleReservacionService {

    @Autowired
    private DetalleReservacionRepository repository;

    private DetalleReservacionDTO convertirADTO(DetalleReservacion entidad) {
        DetalleReservacionDTO dto = new DetalleReservacionDTO();
        dto.setIdDetalleReservacion(entidad.getIdDetalleReservacion());
        dto.setPrecioPactado(entidad.getPrecioPactado());
        dto.setEstado(entidad.getEstado()); // <-- Usar getEstado()
        if (entidad.getIdHabitacion() != null) {
            dto.setIdHabitacion(entidad.getIdHabitacion().getIdHabitacion());
        }
        if (entidad.getIdReservacion() != null) {
            dto.setIdReservacion(entidad.getIdReservacion().getIdReservacion());
        }
        return dto;
    }

    private DetalleReservacion convertirAEntidad(DetalleReservacionDTO dto) {
        DetalleReservacion entidad = new DetalleReservacion();
        entidad.setIdDetalleReservacion(dto.getIdDetalleReservacion());
        entidad.setPrecioPactado(dto.getPrecioPactado());
        entidad.setEstado(dto.isEstado());
        entidad.setIdHabitacion(new com.hotel404.entity.Habitaciones(dto.getIdHabitacion()));
        entidad.setIdReservacion(new com.hotel404.entity.Reservaciones(dto.getIdReservacion()));
        return entidad;
    }

    public List<DetalleReservacionDTO> obtenerTodos() {
        return repository.findAll().stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public DetalleReservacionDTO obtenerPorId(Integer id) {
        return repository.findById(id).map(this::convertirADTO).orElse(null);
    }

    public DetalleReservacionDTO guardar(DetalleReservacionDTO dto) {
        DetalleReservacion entidad = convertirAEntidad(dto);
        DetalleReservacion guardado = repository.save(entidad);
        return convertirADTO(guardado);
    }

    public DetalleReservacionDTO actualizar(Integer id, DetalleReservacionDTO dto) {
        if (repository.existsById(id)) {
            DetalleReservacion entidad = convertirAEntidad(dto);
            entidad.setIdDetalleReservacion(id);
            return convertirADTO(repository.save(entidad));
        }
        return null;
    }

    public void eliminar(Integer id) {
        repository.deleteById(id);
    }
}
