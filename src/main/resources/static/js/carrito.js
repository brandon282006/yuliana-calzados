document.addEventListener("DOMContentLoaded", function () {

    // ============================================
    // 1. CONFIGURACIÓN DE LA API
    // ============================================

    const ID_USUARIO = 2;
    const API_CARRITO = "/api/carrito/" + ID_USUARIO;
    const COSTO_ENVIO = 15;

    // Los productos se mantienen en memoria.
    // La fuente principal de datos es Spring Boot y MySQL.
    let carritoActual = [];
    let productoSeleccionado = null;

    // Guardamos las imágenes de los productos de la colección.
    const imagenesProductos = {
        "1": "/img/stilleto_clasico.jpeg",
        "2": "/img/sandalia_glam.jpeg",
        "3": "/img/sneaker_urbana.jpeg",
        "4": "/img/botin_cafe.jpeg",
        "5": "/img/mocasin.jpeg",
        "6": "/img/botin_cafe.avif",
        "7": "/img/sandalia_siena.jpeg",
        "8": "/img/stilleto_verona.jpeg",
        "9": "/img/mule_paris.jpeg",
        "10": "/img/tacon_victoria.jpeg",
        "11": "/img/sandalia_glamour.jpeg",
        "12": "/img/tacon_elegance.jpeg",
        "13": "/img/loafer_classic.jpeg",
        "14": "/img/sneaker_bloom.jpeg",
        "15": "/img/ballerina_soft.jpeg",
        "16": "/img/mocasin_comfort.jpeg",
        "17": "/img/sandalia_breeze.jpeg",
        "18": "/img/plataforma_street.jpeg"
    };

    // ============================================
    // 2. FUNCIONES GENERALES
    // ============================================

    function formatearPrecio(valor) {
        return "S/ " + Number(valor || 0).toFixed(2);
    }

    function escaparHTML(valor) {
        return String(valor ?? "").replace(/[&<>"']/g, function (caracter) {
            const equivalencias = {
                "&": "&amp;",
                "<": "&lt;",
                ">": "&gt;",
                '"': "&quot;",
                "'": "&#39;"
            };

            return equivalencias[caracter];
        });
    }

    function calcularSubtotal(carrito) {
        return carrito.reduce(function (total, producto) {
            return total + Number(producto.precio) * Number(producto.cantidad);
        }, 0);
    }

    async function procesarRespuesta(respuesta) {
        if (!respuesta.ok) {
            let mensaje = "Ocurrió un error al comunicarse con el servidor.";

            try {
                const error = await respuesta.json();
                mensaje = error.message || error.mensaje || error.detail || mensaje;
            } catch (e) {
                // La respuesta de error podría no contener JSON.
            }

            throw new Error(mensaje);
        }

        if (respuesta.status === 204) {
            return null;
        }

        return respuesta.json();
    }

    function mostrarError(error) {
        console.error("Error en el carrito:", error);
        alert(error.message || "No se pudo completar la operación.");
    }

    // ============================================
    // 3. CONSULTAR EL CARRITO EN SPRING BOOT
    // ============================================

    async function cargarCarrito() {
        try {
            const respuesta = await fetch(API_CARRITO);
            const detalles = await procesarRespuesta(respuesta);

            // Adaptamos los nombres de la respuesta de Java
            // a los que utiliza la interfaz de la página.
            carritoActual = detalles.map(function (detalle) {
                return {
                    idDetalle: detalle.idDetalle,
                    id: detalle.idProducto,
                    nombre: detalle.modelo,
                    precio: Number(detalle.precio),
                    imagen: imagenesProductos[String(detalle.idProducto)] || "",
                    talla: detalle.talla,
                    cantidad: Number(detalle.cantidad),
                    subtotal: Number(detalle.subtotal)
                };
            });

            renderizarCarrito();
            renderizarOffcanvas();
            renderizarCheckout();

        } catch (error) {
            mostrarError(error);
        }
    }

    // ============================================
    // 4. AGREGAR PRODUCTOS
    // ============================================


    async function agregarAlCarrito(producto) {
        try {
            const respuesta = await fetch(API_CARRITO + "/productos", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    idProducto: Number(producto.id),
                    cantidad: 1,
                    talla: producto.talla,
                    color: producto.color
                })
            });

            await procesarRespuesta(respuesta);

            await cargarCarrito();

            const mensaje = document.getElementById("mensajeCarrito");

            if (mensaje) {
                mensaje.textContent = "✓ Añadido al carrito";

                setTimeout(function () {
                    mensaje.textContent = "";
                }, 2000);
            }

        } catch (error) {
            mostrarError(error);
        }
    }


    // ============================================
    // 5. CAMBIAR CANTIDAD
    // ============================================

    async function cambiarCantidad(idDetalle, cambio) {
        const producto = carritoActual.find(function (item) {
            return String(item.idDetalle) === String(idDetalle);
        });

        if (!producto) {
            return;
        }

        const nuevaCantidad = producto.cantidad + cambio;

        try {
            // Si la cantidad llega a cero, eliminamos el detalle.
            if (nuevaCantidad <= 0) {
                await eliminarProducto(idDetalle);
                return;
            }

            const respuesta = await fetch(
                API_CARRITO + "/productos/" +
                encodeURIComponent(idDetalle) +
                "?cantidad=" + nuevaCantidad,
                {
                    method: "PUT"
                }
            );

            await procesarRespuesta(respuesta);
            await cargarCarrito();

        } catch (error) {
            mostrarError(error);
        }
    }

    // ============================================
    // 6. ELIMINAR PRODUCTOS
    // ============================================

    async function eliminarProducto(idDetalle) {
        try {
            const respuesta = await fetch(
                API_CARRITO + "/productos/" +
                encodeURIComponent(idDetalle),
                {
                    method: "DELETE"
                }
            );

            await procesarRespuesta(respuesta);
            await cargarCarrito();

        } catch (error) {
            mostrarError(error);
        }
    }

    // ============================================
    // 7. ACTUALIZAR RESUMEN DEL CARRITO
    // ============================================

    function actualizarResumenCarrito(subtotal) {
        const envio = subtotal > 0 ? COSTO_ENVIO : 0;
        const total = subtotal + envio;

        const subtotalElemento = document.getElementById("subtotalCarrito");
        const envioElemento = document.getElementById("envioCarrito");
        const totalElemento = document.getElementById("totalCarrito");

        if (subtotalElemento) {
            subtotalElemento.textContent = formatearPrecio(subtotal);
        }

        if (envioElemento) {
            envioElemento.textContent = formatearPrecio(envio);
        }

        if (totalElemento) {
            totalElemento.textContent = formatearPrecio(total);
        }
    }

    // ============================================
    // 8. MOSTRAR CARRITO PRINCIPAL
    // ============================================

    function renderizarCarrito() {
        const contenedor = document.getElementById("listaCarrito");

        if (!contenedor) {
            return;
        }

        if (carritoActual.length === 0) {
            contenedor.innerHTML = `
                <div class="alert text-center">
                    <i class="bi bi-cart-x fs-1"></i>
                    <p class="mt-2 mb-3">Tu carrito está vacío.</p>
                    <a href="/coleccion" class="btn btn-dark">
                        Ver colección
                    </a>
                </div>
            `;

            actualizarResumenCarrito(0);
            return;
        }

        contenedor.innerHTML = carritoActual.map(function (producto) {
            const imagen = escaparHTML(producto.imagen);
            const nombre = escaparHTML(producto.nombre);
            const talla = escaparHTML(producto.talla);
            const idDetalle = escaparHTML(producto.idDetalle);

            return `
                <div class="card mb-3 shadow-sm border-0">
                    <div class="row g-0 align-items-center">

                        <div class="col-4 col-md-3">
                            ${imagen
                    ? `<img src="${imagen}"
                                           class="img-fluid rounded-start w-100 h-100"
                                           style="object-fit: cover;"
                                           alt="${nombre}">`
                    : ""
                }
                        </div>

                        <div class="col-8 col-md-9">
                            <div class="card-body">

                                <div class="d-flex justify-content-between align-items-start">
                                    <div>
                                        <h5 class="card-title fw-bold mb-1">
                                            ${nombre}
                                        </h5>

                                        <p class="card-text text-muted small mb-2">
                                            Talla: ${talla}
                                        </p>
                                    </div>

                                    <button type="button"
                                            class="btn btn-danger btn-sm text-white"
                                            data-accion="eliminar"
                                            data-id="${idDetalle}">
                                        <i class="bi bi-trash p-0 fs-6 text-white"></i>
                                    </button>
                                </div>

                                <div class="d-flex justify-content-between align-items-center mt-3">

                                    <div class="input-group" style="max-width: 130px;">
                                        <button type="button"
                                                class="input-group-text bg-light"
                                                data-accion="restar"
                                                data-id="${idDetalle}">
                                            -
                                        </button>

                                        <input type="text"
                                               class="form-control text-center bg-white"
                                               value="${producto.cantidad}"
                                               readonly>

                                        <button type="button"
                                                class="input-group-text bg-light"
                                                data-accion="sumar"
                                                data-id="${idDetalle}">
                                            +
                                        </button>
                                    </div>

                                    <div class="text-end">
                                        <span class="precio-actual">
                                            ${formatearPrecio(
                    producto.precio * producto.cantidad
                )}
                                        </span>
                                    </div>

                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            `;
        }).join("");

        actualizarResumenCarrito(calcularSubtotal(carritoActual));
    }

    // ============================================
    // 9. MOSTRAR CARRITO LATERAL (OFFCANVAS)
    // ============================================

    function renderizarOffcanvas() {
        const contenedor = document.getElementById("listaCarritoOffcanvas");
        const totalElemento = document.getElementById("totalOffcanvas");

        if (!contenedor) {
            return;
        }

        if (carritoActual.length === 0) {
            contenedor.innerHTML = `
                <div class="text-center text-muted py-4">
                    <i class="bi bi-cart-x fs-1"></i>
                    <p class="mt-2">Tu carrito está vacío.</p>
                </div>
            `;

            if (totalElemento) {
                totalElemento.textContent = "S/ 0.00";
            }

            return;
        }

        contenedor.innerHTML = carritoActual.map(function (producto) {
            const nombre = escaparHTML(producto.nombre);
            const talla = escaparHTML(producto.talla);
            const idDetalle = escaparHTML(producto.idDetalle);

            return `
                <div class="border-bottom pb-3 mb-3">

                    <div class="d-flex justify-content-between">
                        <div>
                            <h6 class="mb-1">${nombre}</h6>
                            <small class="text-muted">Talla: ${talla}</small>
                        </div>

                        <button type="button"
                                class="btn btn-sm btn-outline-danger"
                                data-accion="eliminar"
                                data-id="${idDetalle}">
                            <i class="bi bi-trash"></i>
                        </button>
                    </div>

                    <div class="d-flex justify-content-between align-items-center mt-2">

                        <div class="input-group input-group-sm" style="max-width: 110px;">
                            <button type="button"
                                    class="input-group-text"
                                    data-accion="restar"
                                    data-id="${idDetalle}">
                                -
                            </button>

                            <span class="form-control text-center">
                                ${producto.cantidad}
                            </span>

                            <button type="button"
                                    class="input-group-text"
                                    data-accion="sumar"
                                    data-id="${idDetalle}">
                                +
                            </button>
                        </div>

                        <strong>
                            ${formatearPrecio(
                producto.precio * producto.cantidad
            )}
                        </strong>
                    </div>
                </div>
            `;
        }).join("");

        if (totalElemento) {
            totalElemento.textContent =
                formatearPrecio(calcularSubtotal(carritoActual));
        }
    }

    // ============================================
    // 10. DETECTAR BOTONES +, - Y ELIMINAR
    // ============================================

    document.addEventListener("click", function (evento) {
        const boton = evento.target.closest("[data-accion]");

        if (!boton) {
            return;
        }

        const accion = boton.dataset.accion;
        const idDetalle = boton.dataset.id;

        if (accion === "sumar") {
            cambiarCantidad(idDetalle, 1);
        }

        if (accion === "restar") {
            cambiarCantidad(idDetalle, -1);
        }

        if (accion === "eliminar") {
            eliminarProducto(idDetalle);
        }
    });

    // ============================================
    // 11. SELECCIONAR PRODUCTO EN LA COLECCIÓN
    // ============================================

    document.querySelectorAll(".btn-detalle").forEach(function (boton) {
        boton.addEventListener("click", function () {
            productoSeleccionado = {
                id: boton.dataset.id,
                nombre: boton.dataset.producto,
                precio: Number(boton.dataset.precio),
                imagen: boton.dataset.imagen || ""
            };

            const titulo = document.getElementById("modalDetalleLabel");

            if (titulo) {
                titulo.textContent = productoSeleccionado.nombre;
            }
        });
    });

    // ============================================
    // 12. BOTÓN AÑADIR AL CARRITO
    // ============================================

    const btnAgregar = document.getElementById("btnAgregarCarrito");

    if (btnAgregar) {
        btnAgregar.addEventListener("click", async function () {
            if (!productoSeleccionado) {
                alert("Primero selecciona un producto.");
                return;
            }

            const selectTalla = document.getElementById("selectTalla");
            const selectColor = document.getElementById("selectColor");

            if (!selectTalla || !selectTalla.value) {
                alert("Debes seleccionar una talla.");
                return;
            }

            if (!selectColor || !selectColor.value) {
                alert("Debes seleccionar un color.");
                return;
            }

            await agregarAlCarrito({
                id: productoSeleccionado.id,
                nombre: productoSeleccionado.nombre,
                precio: productoSeleccionado.precio,
                imagen: productoSeleccionado.imagen,
                talla: selectTalla.value,
                color: selectColor.value
            });
        });
    }

    // ============================================
    // 13. MOSTRAR RESUMEN EN EL CHECKOUT
    // ============================================

    function renderizarCheckout() {
        const contenedor = document.getElementById("resumenCheckout");

        if (!contenedor) {
            return;
        }

        if (carritoActual.length === 0) {
            contenedor.innerHTML = `
                <div class="alert text-center">
                    <i class="bi bi-cart-x fs-1"></i>
                    <p class="mt-2">Tu carrito está vacío.</p>
                    <a href="/coleccion" class="btn btn-dark">
                        Ver colección
                    </a>
                </div>
            `;

            actualizarTotalesCheckout(0);
            return;
        }

        contenedor.innerHTML = carritoActual.map(function (producto) {
            const nombre = escaparHTML(producto.nombre);
            const imagen = escaparHTML(producto.imagen);
            const talla = escaparHTML(producto.talla);

            return `
                <div class="d-flex align-items-center mb-3">

                    ${imagen
                    ? `<img src="${imagen}"
                                   class="rounded me-3"
                                   style="width: 60px; height: 60px; object-fit: cover;"
                                   alt="${nombre}">`
                    : ""
                }

                    <div class="flex-grow-1">
                        <h6 class="mb-0 fw-semibold">${nombre}</h6>
                        <small class="text-muted">
                            Cant: ${producto.cantidad} | Talla: ${talla}
                        </small>
                    </div>

                    <span class="fw-bold">
                        ${formatearPrecio(
                    producto.precio * producto.cantidad
                )}
                    </span>
                </div>
            `;
        }).join("");

        actualizarTotalesCheckout(calcularSubtotal(carritoActual));
    }

    function actualizarTotalesCheckout(subtotal) {
        const envio = subtotal > 0 ? COSTO_ENVIO : 0;
        const total = subtotal + envio;

        const subtotalElemento = document.getElementById("subtotalCheckout");
        const envioElemento = document.getElementById("envioCheckout");
        const totalElemento = document.getElementById("totalCheckout");

        if (subtotalElemento) {
            subtotalElemento.textContent = formatearPrecio(subtotal);
        }

        if (envioElemento) {
            envioElemento.textContent = formatearPrecio(envio);
        }

        if (totalElemento) {
            totalElemento.textContent = formatearPrecio(total);
        }
    }

    // ============================================
    // 14. CONFIRMAR PEDIDO (FLUJO ACTUAL TEMPORAL)
    // ============================================

    const formularioCheckout = document.getElementById("formCheckout");

    if (formularioCheckout) {
        formularioCheckout.addEventListener("submit", async function (evento) {
            evento.preventDefault();

            if (!formularioCheckout.checkValidity()) {
                formularioCheckout.reportValidity();
                return;
            }

            const botonConfirmar =
                document.getElementById("btnConfirmarPedido");

            try {
                // Evitar que se envíe dos veces mientras se procesa
                if (botonConfirmar) {
                    botonConfirmar.disabled = true;
                    botonConfirmar.textContent = "Procesando compra...";
                }

                // Obtener los productos actuales desde Spring Boot
                const respuestaCarrito = await fetch(API_CARRITO);
                const detalles = await procesarRespuesta(respuestaCarrito);

                if (!detalles || detalles.length === 0) {
                    alert("No hay productos en el carrito.");
                    await cargarCarrito();
                    return;
                }

                const productosPedido = detalles.map(function (detalle) {
                    return {
                        id: detalle.idProducto,
                        idDetalle: detalle.idDetalle,
                        nombre: detalle.modelo,
                        precio: Number(detalle.precio),
                        imagen: imagenesProductos[String(detalle.idProducto)] || "",
                        talla: detalle.talla,
                        cantidad: Number(detalle.cantidad)
                    };
                });

                // Obtener los datos del formulario
                const nombre =
                    document.getElementById("nombreCliente").value.trim();

                const telefono =
                    document.getElementById("telefonoCliente").value.trim();

                const correo =
                    document.getElementById("correoCliente").value.trim();

                const departamento =
                    document.getElementById("departamentoCliente").value;

                const direccion =
                    document.getElementById("direccionCliente").value.trim();

                const referencia =
                    document.getElementById("referenciaCliente").value.trim();

                const opcionPago = document.querySelector(
                    'input[name="metodoPagoCheckout"]:checked'
                );

                if (!opcionPago) {
                    alert("Selecciona un método de pago.");
                    return;
                }

                // Enviar la compra al controlador de Spring Boot
                const respuestaCompra = await fetch(
                    "/ventas-web/comprar/" + ID_USUARIO,
                    {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json"
                        },
                        body: JSON.stringify({
                            direccion: direccion,
                            departamento: departamento,
                            referencia: referencia,
                            metodoPago: opcionPago.value
                        })
                    }
                );

                const resultado = await procesarRespuesta(respuestaCompra);

                // Solo llegamos aquí si Spring Boot registró la compra
                const subtotal = calcularSubtotal(productosPedido);
                const envio = subtotal > 0 ? COSTO_ENVIO : 0;

                const pedido = {
                    numero: "YUL-" + resultado.idVentaWeb,
                    idVentaWeb: resultado.idVentaWeb,
                    nombre: nombre,
                    telefono: telefono,
                    correo: correo,
                    departamento: departamento,
                    direccion: direccion,
                    referencia: referencia,
                    metodoPago: opcionPago.value,
                    productos: productosPedido,
                    subtotal: subtotal,
                    envio: envio,
                    total: Number(resultado.total)
                };

                // Guardar los datos para mostrar la página de confirmación
                localStorage.setItem(
                    "pedidoConfirmado",
                    JSON.stringify(pedido)
                );

                // Ir a la confirmación después del registro real
                window.location.href = "/confirmacion";

            } catch (error) {
                mostrarError(error);

            } finally {
                if (botonConfirmar) {
                    botonConfirmar.disabled = false;
                    botonConfirmar.textContent = "Realizar Pedido";
                }
            }
        });
    }

    // ============================================
    // 15. MOSTRAR LA CONFIRMACIÓN DEL PEDIDO
    // ============================================

    function renderizarConfirmacion() {
        const numeroPedido = document.getElementById("numeroPedido");

        if (!numeroPedido) {
            return;
        }

        const pedidoGuardado = localStorage.getItem("pedidoConfirmado");

        if (!pedidoGuardado) {
            numeroPedido.textContent = "---";
            return;
        }

        try {
            const pedido = JSON.parse(pedidoGuardado);

            numeroPedido.textContent = "#" + pedido.numero;

            const contenedorProductos =
                document.getElementById("productosConfirmacion");

            if (contenedorProductos) {
                contenedorProductos.innerHTML = pedido.productos.map(
                    function (producto) {
                        const nombre = escaparHTML(producto.nombre);
                        const imagen = escaparHTML(producto.imagen);
                        const talla = escaparHTML(producto.talla);

                        return `
                            <div class="list-group-item d-flex justify-content-between align-items-center bg-transparent px-0 py-3">

                                <div class="d-flex align-items-center">

                                    ${imagen
                                ? `<img src="${imagen}"
                                                   class="rounded me-3"
                                                   style="width: 65px; height: 65px; object-fit: cover;"
                                                   alt="${nombre}">`
                                : ""
                            }

                                    <div>
                                        <h6 class="mb-1 fw-semibold">
                                            ${nombre}
                                        </h6>

                                        <small class="text-muted d-block">
                                            Talla: ${talla}
                                        </small>

                                        <small class="text-muted">
                                            Cantidad: ${producto.cantidad}
                                        </small>
                                    </div>
                                </div>

                                <span class="fw-bold fs-6">
                                    ${formatearPrecio(
                                producto.precio * producto.cantidad
                            )}
                                </span>
                            </div>
                        `;
                    }
                ).join("");
            }

            const subtotalElemento =
                document.getElementById("subtotalConfirmacion");
            const envioElemento =
                document.getElementById("envioConfirmacion");
            const totalElemento =
                document.getElementById("totalConfirmacion");
            const datosCliente =
                document.getElementById("datosCliente");
            const direccionEntrega =
                document.getElementById("direccionEntrega");
            const metodoPago =
                document.getElementById("metodoPagoConfirmacion");

            if (subtotalElemento) {
                subtotalElemento.textContent =
                    formatearPrecio(pedido.subtotal);
            }

            if (envioElemento) {
                envioElemento.textContent = formatearPrecio(pedido.envio);
            }

            if (totalElemento) {
                totalElemento.textContent = formatearPrecio(pedido.total);
            }

            if (datosCliente) {
                datosCliente.textContent =
                    pedido.nombre + " — " + pedido.telefono;
            }

            if (direccionEntrega) {
                direccionEntrega.textContent =
                    pedido.direccion + ", " + pedido.departamento;
            }

            if (metodoPago) {
                metodoPago.textContent = pedido.metodoPago;
            }

        } catch (error) {
            console.error("No se pudo mostrar la confirmación:", error);
            numeroPedido.textContent = "---";
        }
    }

    // ============================================
    // 16. INICIAR LA PÁGINA
    // ============================================

    renderizarConfirmacion();
    cargarCarrito();

});