package com.example.avance_n2.validators;

import com.example.avance_n2.models.Categoria;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CategoriaValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Categoria.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        Categoria categoria = (Categoria) target;

        // Validar el nombre
        if (categoria.getNombreCat() == null ||
                categoria.getNombreCat().trim().isEmpty()) {

            if (!errors.hasFieldErrors("nombreCat")) {
                errors.rejectValue(
                    "nombreCat",
                    "nombre.vacio",
                    "El nombre de la categoría es obligatorio"
                );
            }

        } else if (categoria.getNombreCat().length() > 100) {

            if (!errors.hasFieldErrors("nombreCat")) {
                errors.rejectValue(
                    "nombreCat",
                    "nombre.longitud",
                    "El nombre no puede superar los 100 caracteres"
                );
            }
        }

        // Validar la descripción
        if (categoria.getDescripcionCat() != null &&
                categoria.getDescripcionCat().length() > 500) {

            if (!errors.hasFieldErrors("descripcionCat")) {
                errors.rejectValue(
                    "descripcionCat",
                    "descripcion.longitud",
                    "La descripción no puede superar los 500 caracteres"
                );
            }
        }
    }
}