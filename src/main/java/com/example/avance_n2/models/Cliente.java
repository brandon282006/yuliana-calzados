
package com.example.avance_n2.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "Cliente")
public class Cliente {

    @Id
    @Column(name = "id_cliente")
    private Integer idCliente;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id_cliente")
    private Usuario usuario;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    @Column(name = "nombre_cli", nullable = false, length = 100)
    private String nombreCli;

    @Size(max = 100, message = "El apellido no puede superar los 100 caracteres")
    @Column(name = "apellido_cli", length = 100)
    private String apellidoCli;

    @Size(max = 9, message = "El teléfono no puede superar los 9 caracteres")
    @Column(name = "telefono_cli", length = 9)
    private String telefonoCli;

    public Cliente() {}

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getNombreCli() {
        return nombreCli;
    }

    public void setNombreCli(String nombreCli) {
        this.nombreCli = nombreCli;
    }

    public String getApellidoCli() {
        return apellidoCli;
    }

    public void setApellidoCli(String apellidoCli) {
        this.apellidoCli = apellidoCli;
    }

    public String getTelefonoCli() {
        return telefonoCli;
    }

    public void setTelefonoCli(String telefonoCli) {
        this.telefonoCli = telefonoCli;
    }
}
