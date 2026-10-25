package com.example.avance_n2.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller 
public class ColeccionController {

    @GetMapping("/coleccion")
    public String coleccion() {
        return "coleccion";
    }
}
