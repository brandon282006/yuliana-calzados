
package com.example.avance_n2.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "Trabajador")
public class Trabajador {

    @Id
    @Column(name = "id_trabajador")
    private Integer idTrabajador;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id_trabajador")
    private Usuario usuario;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    @Column(name = "nombre_tra", nullable = false, length = 100)
    private String nombreTra;

    @Size(max = 100, message = "El apellido no puede superar los 100 caracteres")
    @Column(name = "apellido_tra", length = 100)
    private String apellidoTra;

    @Size(max = 9, message = "El teléfono no puede superar los 9 caracteres")
    @Column(name = "telefono_tra", length = 9)
    private String telefonoTra;

    @Enumerated(EnumType.STRING)
    @Column(name = "cargo_tra", nullable = false)
    private CargoTrabajador cargoTra;

    public Trabajador() {}

    public Integer getIdTrabajador() {
        return idTrabajador;
    }

    public void setIdTrabajador(Integer idTrabajador) {
        this.idTrabajador = idTrabajador;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getNombreTra() {
        return nombreTra;
    }

    public void setNombreTra(String nombreTra) {
        this.nombreTra = nombreTra;
    }

    public String getApellidoTra() {
        return apellidoTra;
    }

    public void setApellidoTra(String apellidoTra) {
        this.apellidoTra = apellidoTra;
    }

    public String getTelefonoTra() {
        return telefonoTra;
    }

    public void setTelefonoTra(String telefonoTra) {
        this.telefonoTra = telefonoTra;
    }

    public CargoTrabajador getCargoTra() {
        return cargoTra;
    }

    public void setCargoTra(CargoTrabajador cargoTra) {
        this.cargoTra = cargoTra;
    }

    public enum CargoTrabajador {
        Administrador,
        Vendedor
    }
}