/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hotel404.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author 10jul
 */
@Entity
@Table(name = "detalle_reservacion")
@NamedQueries({
    @NamedQuery(name = "DetalleReservacion.findAll", query = "SELECT d FROM DetalleReservacion d"),
    @NamedQuery(
        name = "DetalleReservacion.findByIdDetalleReservacion",
        query = "SELECT d FROM DetalleReservacion d WHERE d.idDetalleReservacion = :idDetalleReservacion"
    ),
    @NamedQuery(
        name = "DetalleReservacion.findByPrecioPactado",
        query = "SELECT d FROM DetalleReservacion d WHERE d.precioPactado = :precioPactado"
    ),
    @NamedQuery(
        name = "DetalleReservacion.findByEstado",
        query = "SELECT d FROM DetalleReservacion d WHERE d.estado = :estado"
    ),
})
public class DetalleReservacion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_detalle_reservacion")
    private Integer idDetalleReservacion;

    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "precio_pactado")
    private BigDecimal precioPactado;

    @Basic(optional = false)
    @Column(name = "estado")
    private boolean estado;

    @JoinColumn(name = "id_habitacion", referencedColumnName = "id_habitacion")
    @ManyToOne(optional = false)
    private Habitaciones idHabitacion;

    @JoinColumn(name = "id_reservacion", referencedColumnName = "id_reservacion")
    @ManyToOne(optional = false)
    private Reservaciones idReservacion;

    public DetalleReservacion() {}

    public DetalleReservacion(Integer idDetalleReservacion) {
        this.idDetalleReservacion = idDetalleReservacion;
    }

    public DetalleReservacion(Integer idDetalleReservacion, BigDecimal precioPactado, boolean estado) {
        this.idDetalleReservacion = idDetalleReservacion;
        this.precioPactado = precioPactado;
        this.estado = estado;
    }

    public Integer getIdDetalleReservacion() {
        return idDetalleReservacion;
    }

    public void setIdDetalleReservacion(Integer idDetalleReservacion) {
        this.idDetalleReservacion = idDetalleReservacion;
    }

    public BigDecimal getPrecioPactado() {
        return precioPactado;
    }

    public void setPrecioPactado(BigDecimal precioPactado) {
        this.precioPactado = precioPactado;
    }

    public boolean getEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public Habitaciones getIdHabitacion() {
        return idHabitacion;
    }

    public void setIdHabitacion(Habitaciones idHabitacion) {
        this.idHabitacion = idHabitacion;
    }

    public Reservaciones getIdReservacion() {
        return idReservacion;
    }

    public void setIdReservacion(Reservaciones idReservacion) {
        this.idReservacion = idReservacion;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += idDetalleReservacion != null ? idDetalleReservacion.hashCode() : 0;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DetalleReservacion)) {
            return false;
        }
        DetalleReservacion other = (DetalleReservacion) object;
        if (
            (this.idDetalleReservacion == null && other.idDetalleReservacion != null) ||
            (this.idDetalleReservacion != null &&
                !this.idDetalleReservacion.equals(other.idDetalleReservacion))
        ) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.hotel404.entity.DetalleReservacion[ idDetalleReservacion=" + idDetalleReservacion + " ]";
    }
}
