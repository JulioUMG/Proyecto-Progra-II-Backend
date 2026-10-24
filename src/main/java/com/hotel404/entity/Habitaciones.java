/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hotel404.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;

/**
 *
 * @author 10jul
 */
@Entity
@Table(name = "habitaciones")
@NamedQueries({
    @NamedQuery(name = "Habitaciones.findAll", query = "SELECT h FROM Habitaciones h"),
    @NamedQuery(
        name = "Habitaciones.findByIdHabitacion",
        query = "SELECT h FROM Habitaciones h WHERE h.idHabitacion = :idHabitacion"
    ),
    @NamedQuery(
        name = "Habitaciones.findByNumero",
        query = "SELECT h FROM Habitaciones h WHERE h.numero = :numero"
    ),
    @NamedQuery(
        name = "Habitaciones.findByTipo",
        query = "SELECT h FROM Habitaciones h WHERE h.tipo = :tipo"
    ),
    @NamedQuery(
        name = "Habitaciones.findByPrecioNoche",
        query = "SELECT h FROM Habitaciones h WHERE h.precioNoche = :precioNoche"
    ),
    @NamedQuery(
        name = "Habitaciones.findByEstado",
        query = "SELECT h FROM Habitaciones h WHERE h.estado = :estado"
    ),
    @NamedQuery(
        name = "Habitaciones.findByEstadoHabitacion",
        query = "SELECT h FROM Habitaciones h WHERE h.estadoHabitacion = :estadoHabitacion"
    ),
})
public class Habitaciones implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_habitacion")
    private Integer idHabitacion;

    @Basic(optional = false)
    @Column(name = "numero")
    private String numero;

    @Basic(optional = false)
    @Column(name = "tipo")
    private String tipo;

    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "precio_noche")
    private BigDecimal precioNoche;

    @Basic(optional = false)
    @Column(name = "estado")
    private boolean estado;

    @Basic(optional = false)
    @Column(name = "estado_habitacion")
    private String estadoHabitacion;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idHabitacion")
    private Collection<DetalleReservacion> detalleReservacionCollection;

    public Habitaciones() {}

    public Habitaciones(Integer idHabitacion) {
        this.idHabitacion = idHabitacion;
    }

    public Habitaciones(
        Integer idHabitacion,
        String numero,
        String tipo,
        BigDecimal precioNoche,
        boolean estado,
        String estadoHabitacion
    ) {
        this.idHabitacion = idHabitacion;
        this.numero = numero;
        this.tipo = tipo;
        this.precioNoche = precioNoche;
        this.estado = estado;
        this.estadoHabitacion = estadoHabitacion;
    }

    public Integer getIdHabitacion() {
        return idHabitacion;
    }

    public void setIdHabitacion(Integer idHabitacion) {
        this.idHabitacion = idHabitacion;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getPrecioNoche() {
        return precioNoche;
    }

    public void setPrecioNoche(BigDecimal precioNoche) {
        this.precioNoche = precioNoche;
    }

    public boolean getEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public String getEstadoHabitacion() {
        return estadoHabitacion;
    }

    public void setEstadoHabitacion(String estadoHabitacion) {
        this.estadoHabitacion = estadoHabitacion;
    }

    public Collection<DetalleReservacion> getDetalleReservacionCollection() {
        return detalleReservacionCollection;
    }

    public void setDetalleReservacionCollection(Collection<DetalleReservacion> detalleReservacionCollection) {
        this.detalleReservacionCollection = detalleReservacionCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += idHabitacion != null ? idHabitacion.hashCode() : 0;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Habitaciones)) {
            return false;
        }
        Habitaciones other = (Habitaciones) object;
        if (
            (this.idHabitacion == null && other.idHabitacion != null) ||
            (this.idHabitacion != null && !this.idHabitacion.equals(other.idHabitacion))
        ) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.hotel404.entity.Habitaciones[ idHabitacion=" + idHabitacion + " ]";
    }
}
