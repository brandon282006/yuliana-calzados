package com.example.avance_n2.services;

import com.example.avance_n2.models.Carrito;
import com.example.avance_n2.models.DetalleCarrito;
import com.example.avance_n2.models.Producto;
import com.example.avance_n2.models.Usuario;

import com.example.avance_n2.repository.CarritoRepository;
import com.example.avance_n2.repository.DetalleCarritoRepository;
import com.example.avance_n2.repository.ProductoRepository;
import com.example.avance_n2.repository.UsuarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final DetalleCarritoRepository detalleRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public CarritoService(
            CarritoRepository carritoRepository,
            DetalleCarritoRepository detalleRepository,
            ProductoRepository productoRepository,
            UsuarioRepository usuarioRepository) {

        this.carritoRepository = carritoRepository;
        this.detalleRepository = detalleRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Obtener el carrito activo o crear uno nuevo
    public Carrito obtenerOCrearCarrito(Integer idUsuario) {

        return carritoRepository
            .findByUsuario_IdUsuarioAndEstadoCarrito(
                idUsuario,
                Carrito.EstadoCarrito.Activo
            )
            .orElseGet(() -> {

                Usuario usuario = usuarioRepository
                    .findById(idUsuario)
                    .orElseThrow(() ->
                        new IllegalArgumentException(
                            "El usuario no existe"
                        )
                    );

                Carrito carrito = new Carrito();
                carrito.setUsuario(usuario);
                carrito.setEstadoCarrito(
                    Carrito.EstadoCarrito.Activo
                );

                return carritoRepository.save(carrito);
            });
    }

    // Agregar un producto considerando también su talla y color
     // Agregar un producto considerando su talla y color
    public Carrito agregarProducto(
            Integer idUsuario,
            Integer idProducto,
            Integer cantidad,
            String talla,
            String color) {

        // Validar cantidad
        if (cantidad == null || cantidad < 1) {
            throw new IllegalArgumentException(
                "La cantidad debe ser como mínimo 1"
            );
        }

        // Validar talla
        if (talla == null || talla.isBlank()) {
            throw new IllegalArgumentException(
                "Debes seleccionar una talla"
            );
        }

        talla = talla.trim();

        if (talla.length() > 10) {
            throw new IllegalArgumentException(
                "La talla no puede superar los 10 caracteres"
            );
        }

        // Validar color
        if (color == null || color.isBlank()) {
            throw new IllegalArgumentException(
                "Debes seleccionar un color"
            );
        }

        color = color.trim();

        if (color.length() > 50) {
            throw new IllegalArgumentException(
                "El color no puede superar los 50 caracteres"
            );
        }

        // Buscar el producto
        Producto producto = productoRepository
            .findById(idProducto)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "El producto no existe"
                )
            );

        // Obtener o crear el carrito activo
        Carrito carrito = obtenerOCrearCarrito(idUsuario);

        // Buscar la misma combinación de producto, talla y color
        DetalleCarrito detalle = detalleRepository
            .findByCarrito_IdCarritoAndProducto_IdProductoAndTallaAndColor(
                carrito.getIdCarrito(),
                idProducto,
                talla,
                color
            )
            .orElse(null);

        if (detalle != null) {

            // Si la combinación ya existe, aumentar la cantidad
            detalle.setCantidad(
                detalle.getCantidad() + cantidad
            );

        } else {

            // Si la combinación no existe, crear un detalle nuevo
            detalle = new DetalleCarrito();
            detalle.setCarrito(carrito);
            detalle.setProducto(producto);
            detalle.setCantidad(cantidad);
            detalle.setTalla(talla);
            detalle.setColor(color);
        }

        detalleRepository.save(detalle);

        return carrito;
    }

    // Consultar los productos del carrito
    @Transactional(readOnly = true)
    public List<DetalleCarrito> listarDetalles(
            Integer idUsuario) {

        return carritoRepository
            .findByUsuario_IdUsuarioAndEstadoCarrito(
                idUsuario,
                Carrito.EstadoCarrito.Activo
            )
            .map(carrito ->
                detalleRepository.findByCarrito_IdCarrito(
                    carrito.getIdCarrito()
                )
            )
            .orElse(List.of());
    }

    // Cambiar la cantidad de un producto
    public void actualizarCantidad(
            Integer idUsuario,
            Integer idDetalle,
            Integer nuevaCantidad) {

        if (nuevaCantidad == null || nuevaCantidad < 1) {
            throw new IllegalArgumentException(
                "La cantidad debe ser como mínimo 1"
            );
        }

        Carrito carrito = obtenerCarritoActivo(idUsuario);

        DetalleCarrito detalle = detalleRepository
            .findById(idDetalle)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "El detalle del carrito no existe"
                )
            );

        if (!detalle.getCarrito().getIdCarrito()
                .equals(carrito.getIdCarrito())) {

            throw new IllegalArgumentException(
                "El producto no pertenece a este carrito"
            );
        }

        detalle.setCantidad(nuevaCantidad);
        detalleRepository.save(detalle);
    }

    // Eliminar un producto del carrito
    public void eliminarProducto(
            Integer idUsuario,
            Integer idDetalle) {

        Carrito carrito = obtenerCarritoActivo(idUsuario);

        DetalleCarrito detalle = detalleRepository
            .findById(idDetalle)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "El detalle del carrito no existe"
                )
            );

        if (!detalle.getCarrito().getIdCarrito()
                .equals(carrito.getIdCarrito())) {

            throw new IllegalArgumentException(
                "El producto no pertenece a este carrito"
            );
        }

        detalleRepository.delete(detalle);
    }

    // Calcular el total actual del carrito
    @Transactional(readOnly = true)
    public BigDecimal calcularTotal(Integer idUsuario) {

        List<DetalleCarrito> detalles =
            listarDetalles(idUsuario);

        BigDecimal total = BigDecimal.ZERO;

        for (DetalleCarrito detalle : detalles) {

            BigDecimal precio =
                detalle.getProducto().getPrecioProd();

            BigDecimal subtotal = precio.multiply(
                BigDecimal.valueOf(detalle.getCantidad())
            );

            total = total.add(subtotal);
        }

        return total;
    }

    // Buscar el carrito activo del usuario
    private Carrito obtenerCarritoActivo(Integer idUsuario) {

        return carritoRepository
            .findByUsuario_IdUsuarioAndEstadoCarrito(
                idUsuario,
                Carrito.EstadoCarrito.Activo
            )
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "El usuario todavía no tiene un carrito activo"
                )
            );
    }
}