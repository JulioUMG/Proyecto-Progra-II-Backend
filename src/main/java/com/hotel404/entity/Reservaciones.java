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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author 10jul
 */
@Entity
@Table(name = "reservaciones")
@NamedQueries({
    @NamedQuery(name = "Reservaciones.findAll", query = "SELECT r FROM Reservaciones r"),
    @NamedQuery(
        name = "Reservaciones.findByIdReservacion",
        query = "SELECT r FROM Reservaciones r WHERE r.idReservacion = :idReservacion"
    ),
    @NamedQuery(
        name = "Reservaciones.findByFechaReservacion",
        query = "SELECT r FROM Reservaciones r WHERE r.fechaReservacion = :fechaReservacion"
    ),
    @NamedQuery(
        name = "Reservaciones.findByFechaIngreso",
        query = "SELECT r FROM Reservaciones r WHERE r.fechaIngreso = :fechaIngreso"
    ),
    @NamedQuery(
        name = "Reservaciones.findByFechaSalida",
        query = "SELECT r FROM Reservaciones r WHERE r.fechaSalida = :fechaSalida"
    ),
    @NamedQuery(
        name = "Reservaciones.findByTotal",
        query = "SELECT r FROM Reservaciones r WHERE r.total = :total"
    ),
    @NamedQuery(
        name = "Reservaciones.findByEstado",
        query = "SELECT r FROM Reservaciones r WHERE r.estado = :estado"
    ),
    @NamedQuery(
        name = "Reservaciones.findByEstadoReservacion",
        query = "SELECT r FROM Reservaciones r WHERE r.estadoReservacion = :estadoReservacion"
    ),
})
public class Reservaciones implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_reservacion")
    private Integer idReservacion;

    @Basic(optional = false)
    @Column(name = "fecha_reservacion")
    @Temporal(TemporalType.DATE)
    private Date fechaReservacion;

    @Basic(optional = false)
    @Column(name = "fecha_ingreso")
    @Temporal(TemporalType.DATE)
    private Date fechaIngreso;

    @Basic(optional = false)
    @Column(name = "fecha_salida")
    @Temporal(TemporalType.DATE)
    private Date fechaSalida;

    // @Max(value=?) @Min(value=?)//if you know range of your decimal fields
    // consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "total")
    private BigDecimal total;

    @Basic(optional = false)
    @Column(name = "estado")
    private boolean estado;

    @Basic(optional = false)
    @Column(name = "estado_reservacion")
    private String estadoReservacion;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idReservacion")
    private Collection<DetalleReservacion> detalleReservacionCollection;

    @JoinColumn(name = "id_huesped", referencedColumnName = "id_huesped")
    @ManyToOne(optional = false)
    private Huespedes idHuesped;

    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    @ManyToOne(optional = false)
    private Usuarios idUsuario;

    public Reservaciones() {}

    public Reservaciones(Integer idReservacion) {
        this.idReservacion = idReservacion;
    }

    public Reservaciones(
        Integer idReservacion,
        Date fechaReservacion,
        Date fechaIngreso,
        Date fechaSalida,
        BigDecimal total,
        boolean estado,
        String estadoReservacion
    ) {
        this.idReservacion = idReservacion;
        this.fechaReservacion = fechaReservacion;
        this.fechaIngreso = fechaIngreso;
        this.fechaSalida = fechaSalida;
        this.total = total;
        this.estado = estado;
        this.estadoReservacion = estadoReservacion;
    }

    public Integer getIdReservacion() {
        return idReservacion;
    }

    public void setIdReservacion(Integer idReservacion) {
        this.idReservacion = idReservacion;
    }

    public Date getFechaReservacion() {
        return fechaReservacion;
    }

    public void setFechaReservacion(Date fechaReservacion) {
        this.fechaReservacion = fechaReservacion;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public Date getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(Date fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public boolean getEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public String getEstadoReservacion() {
        return estadoReservacion;
    }

    public void setEstadoReservacion(String estadoReservacion) {
        this.estadoReservacion = estadoReservacion;
    }

    public Collection<DetalleReservacion> getDetalleReservacionCollection() {
        return detalleReservacionCollection;
    }

    public void setDetalleReservacionCollection(Collection<DetalleReservacion> detalleReservacionCollection) {
        this.detalleReservacionCollection = detalleReservacionCollection;
    }

    public Huespedes getIdHuesped() {
        return idHuesped;
    }

    public void setIdHuesped(Huespedes idHuesped) {
        this.idHuesped = idHuesped;
    }

    public Usuarios getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Usuarios idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += idReservacion != null ? idReservacion.hashCode() : 0;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Reservaciones)) {
            return false;
        }
        Reservaciones other = (Reservaciones) object;
        if (
            (this.idReservacion == null && other.idReservacion != null) ||
            (this.idReservacion != null && !this.idReservacion.equals(other.idReservacion))
        ) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.hotel404.entity.Reservaciones[ idReservacion=" + idReservacion + " ]";
    }
}
