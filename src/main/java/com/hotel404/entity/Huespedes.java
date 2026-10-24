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
import java.util.Collection;

/**
 *
 * @author 10jul
 */
@Entity
@Table(name = "huespedes")
@NamedQueries({
    @NamedQuery(name = "Huespedes.findAll", query = "SELECT h FROM Huespedes h"),
    @NamedQuery(
        name = "Huespedes.findByIdHuesped",
        query = "SELECT h FROM Huespedes h WHERE h.idHuesped = :idHuesped"
    ),
    @NamedQuery(
        name = "Huespedes.findByNombre",
        query = "SELECT h FROM Huespedes h WHERE h.nombre = :nombre"
    ),
    @NamedQuery(name = "Huespedes.findByDpi", query = "SELECT h FROM Huespedes h WHERE h.dpi = :dpi"),
    @NamedQuery(
        name = "Huespedes.findByTelefono",
        query = "SELECT h FROM Huespedes h WHERE h.telefono = :telefono"
    ),
    @NamedQuery(
        name = "Huespedes.findByEstado",
        query = "SELECT h FROM Huespedes h WHERE h.estado = :estado"
    ),
})
public class Huespedes implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_huesped")
    private Integer idHuesped;

    @Basic(optional = false)
    @Column(name = "nombre")
    private String nombre;

    @Basic(optional = false)
    @Column(name = "dpi")
    private String dpi;

    @Basic(optional = false)
    @Column(name = "telefono")
    private String telefono;

    @Basic(optional = false)
    @Column(name = "estado")
    private boolean estado;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idHuesped")
    private Collection<Reservaciones> reservacionesCollection;

    public Huespedes() {}

    public Huespedes(Integer idHuesped) {
        this.idHuesped = idHuesped;
    }

    public Huespedes(Integer idHuesped, String nombre, String dpi, String telefono, boolean estado) {
        this.idHuesped = idHuesped;
        this.nombre = nombre;
        this.dpi = dpi;
        this.telefono = telefono;
        this.estado = estado;
    }

    public Integer getIdHuesped() {
        return idHuesped;
    }

    public void setIdHuesped(Integer idHuesped) {
        this.idHuesped = idHuesped;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDpi() {
        return dpi;
    }

    public void setDpi(String dpi) {
        this.dpi = dpi;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public boolean getEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public Collection<Reservaciones> getReservacionesCollection() {
        return reservacionesCollection;
    }

    public void setReservacionesCollection(Collection<Reservaciones> reservacionesCollection) {
        this.reservacionesCollection = reservacionesCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += idHuesped != null ? idHuesped.hashCode() : 0;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Huespedes)) {
            return false;
        }
        Huespedes other = (Huespedes) object;
        if (
            (this.idHuesped == null && other.idHuesped != null) ||
            (this.idHuesped != null && !this.idHuesped.equals(other.idHuesped))
        ) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.hotel404.entity.Huespedes[ idHuesped=" + idHuesped + " ]";
    }
}
