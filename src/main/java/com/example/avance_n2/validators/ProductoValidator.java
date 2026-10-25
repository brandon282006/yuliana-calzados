package com.example.avance_n2.validators;

import com.example.avance_n2.models.Producto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ProductoValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Producto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        Producto producto = (Producto) target;

        if (producto.getModeloProd() == null ||
                producto.getModeloProd().trim().isEmpty()) {

            if (!errors.hasFieldErrors("modeloProd")) {
                errors.rejectValue(
                    "modeloProd",
                    "modelo.vacio",
                    "El modelo del producto es obligatorio"
                );
            }

        } else if (producto.getModeloProd().length() > 50) {

            if (!errors.hasFieldErrors("modeloProd")) {
                errors.rejectValue(
                    "modeloProd",
                    "modelo.longitud",
                    "El modelo no puede superar los 50 caracteres"
                );
            }
        }

        if (producto.getPrecioProd() != null &&
                producto.getPrecioProd().signum() <= 0) {

            if (!errors.hasFieldErrors("precioProd")) {
                errors.rejectValue(
                    "precioProd",
                    "precio.invalido",
                    "El precio debe ser mayor que cero"
                );
            }
        }

        if (producto.getCategoria() == null ||
                producto.getCategoria().getIdCategoria() == null) {

            if (!errors.hasFieldErrors("categoria")) {
                errors.rejectValue(
                    "categoria",
                    "categoria.vacia",
                    "Debes seleccionar una categoría"
                );
            }
        }
    }
}