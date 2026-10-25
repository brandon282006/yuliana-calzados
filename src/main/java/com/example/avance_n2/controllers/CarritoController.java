package com.example.avance_n2.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller 
public class CarritoController {
    
    @GetMapping("/carrito")
    public String carrito(){
        return "carrito";
    }

    @GetMapping("/checkout")
    public String checkout(){
        return "checkout";
    }

    @GetMapping("/confirmacion")
    public String confirmacion(){
        return "confirmacion";
    }
}