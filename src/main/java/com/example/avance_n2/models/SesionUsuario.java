package com.example.avance_n2.models;

import java.io.Serializable;

public class SesionUsuario implements Serializable {

    private final Integer idUsuario;
    private final String correo;
    private final String nombre;
    private final String rol;

    public SesionUsuario(Integer idUsuario, String correo,
            String nombre, String rol) {
        this.idUsuario = idUsuario;
        this.correo = correo;
        this.nombre = nombre;
        this.rol = rol;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public String getCorreo() {
        return correo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRol() {
        return rol;
    }

    public String getInicial() {
        if (nombre == null || nombre.isBlank()) {
            return "U";
        }
        return nombre.substring(0, 1).toUpperCase();
    }
}
