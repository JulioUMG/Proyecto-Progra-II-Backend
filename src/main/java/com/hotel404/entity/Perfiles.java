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
@Table(name = "perfiles")
@NamedQueries({
    @NamedQuery(name = "Perfiles.findAll", query = "SELECT p FROM Perfiles p"),
    @NamedQuery(
        name = "Perfiles.findByIdPerfil",
        query = "SELECT p FROM Perfiles p WHERE p.idPerfil = :idPerfil"
    ),
    @NamedQuery(name = "Perfiles.findByNombre", query = "SELECT p FROM Perfiles p WHERE p.nombre = :nombre"),
    @NamedQuery(name = "Perfiles.findByEstado", query = "SELECT p FROM Perfiles p WHERE p.estado = :estado"),
})
public class Perfiles implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_perfil")
    private Integer idPerfil;

    @Basic(optional = false)
    @Column(name = "nombre")
    private String nombre;

    @Basic(optional = false)
    @Column(name = "estado")
    private boolean estado;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idPerfil")
    private Collection<Usuarios> usuariosCollection;

    public Perfiles() {}

    public Perfiles(Integer idPerfil) {
        this.idPerfil = idPerfil;
    }

    public Perfiles(Integer idPerfil, String nombre, boolean estado) {
        this.idPerfil = idPerfil;
        this.nombre = nombre;
        this.estado = estado;
    }

    public Integer getIdPerfil() {
        return idPerfil;
    }

    public void setIdPerfil(Integer idPerfil) {
        this.idPerfil = idPerfil;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean getEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public Collection<Usuarios> getUsuariosCollection() {
        return usuariosCollection;
    }

    public void setUsuariosCollection(Collection<Usuarios> usuariosCollection) {
        this.usuariosCollection = usuariosCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += idPerfil != null ? idPerfil.hashCode() : 0;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Perfiles)) {
            return false;
        }
        Perfiles other = (Perfiles) object;
        if (
            (this.idPerfil == null && other.idPerfil != null) ||
            (this.idPerfil != null && !this.idPerfil.equals(other.idPerfil))
        ) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.hotel404.entity.Perfiles[ idPerfil=" + idPerfil + " ]";
    }
}
