package com.example.avance_n2.controllers;

import com.example.avance_n2.models.SesionUsuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class UsuarioSesionAdvice {

    @ModelAttribute("usuarioSesion")
    public SesionUsuario obtenerUsuarioSesion(HttpSession session) {
        return (SesionUsuario) session.getAttribute("usuarioSesion");
    }
}