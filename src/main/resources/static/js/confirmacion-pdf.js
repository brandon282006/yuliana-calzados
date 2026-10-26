document.addEventListener("DOMContentLoaded", function () {

    const boton = document.getElementById("btnExportarPDF");

    if (!boton) {
        return;
    }

    function moneda(valor) {
        return "S/ " + Number(valor || 0).toFixed(2);
    }

    function obtenerPedido() {
        try {
            const guardado = localStorage.getItem("pedidoConfirmado");
            const pedido = guardado ? JSON.parse(guardado) : null;

            if (pedido && Array.isArray(pedido.productos)) {
                return pedido;
            }
        } catch (error) {
            console.error("No se pudo leer el pedido guardado:", error);
        }

        return null;
    }

    function formatearFecha(valor) {
        const fecha = valor ? new Date(valor) : new Date();
        const valida = isNaN(fecha.getTime()) ? new Date() : fecha;

        return valida.toLocaleString("es-PE", {
            day: "2-digit",
            month: "2-digit",
            year: "numeric",
            hour: "2-digit",
            minute: "2-digit"
        });
    }

    function generarPDF(pedido) {
        const { jsPDF } = window.jspdf;
        const doc = new jsPDF({ unit: "mm", format: "a4" });

        const margen = 15;
        const anchoPagina = doc.internal.pageSize.getWidth();
        const derecha = anchoPagina - margen;

        // ---------- Encabezado ----------
        doc.setFillColor(33, 37, 41);
        doc.rect(0, 0, anchoPagina, 32, "F");

        doc.setTextColor(255, 255, 255);
        doc.setFont("helvetica", "bold");
        doc.setFontSize(20);
        doc.text("Yuliana Calzados", margen, 15);

        doc.setFont("helvetica", "normal");
        doc.setFontSize(11);
        doc.text("Comprobante de compra", margen, 23);

        doc.setFont("helvetica", "bold");
        doc.setFontSize(12);
        doc.text("Orden: " + pedido.numero, derecha, 15, { align: "right" });

        doc.setFont("helvetica", "normal");
        doc.setFontSize(10);
        doc.text(formatearFecha(pedido.fecha), derecha, 23, { align: "right" });

        // ---------- Datos del cliente ----------
        doc.setTextColor(33, 37, 41);
        let y = 44;

        doc.setFont("helvetica", "bold");
        doc.setFontSize(11);
        doc.text("Datos del cliente", margen, y);
        doc.text("Entrega y pago", anchoPagina / 2 + 5, y);

        doc.setFont("helvetica", "normal");
        doc.setFontSize(10);

        const colIzq = [
            "Nombre: " + (pedido.nombre || "-"),
            "Teléfono: " + (pedido.telefono || "-"),
            "Correo: " + (pedido.correo || "-")
        ];

        const direccion = (pedido.direccion || "-") +
            (pedido.departamento ? ", " + pedido.departamento : "");

        const colDer = []
            .concat(doc.splitTextToSize("Dirección: " + direccion, anchoPagina / 2 - margen - 5))
            .concat(pedido.referencia
                ? doc.splitTextToSize("Referencia: " + pedido.referencia, anchoPagina / 2 - margen - 5)
                : [])
            .concat(["Método de pago: " + (pedido.metodoPago || "-")]);

        colIzq.forEach(function (linea, i) {
            doc.text(linea, margen, y + 7 + i * 5.5);
        });

        colDer.forEach(function (linea, i) {
            doc.text(linea, anchoPagina / 2 + 5, y + 7 + i * 5.5);
        });

        y += 7 + Math.max(colIzq.length, colDer.length) * 5.5 + 6;

        // ---------- Tabla de productos ----------
        const filas = pedido.productos.map(function (p) {
            const detalle = "Talla: " + (p.talla || "-") +
                (p.color ? "  |  Color: " + p.color : "");

            return [
                p.nombre + "\n" + detalle,
                String(p.cantidad),
                moneda(p.precio),
                moneda(Number(p.precio) * Number(p.cantidad))
            ];
        });

        doc.autoTable({
            startY: y,
            head: [["Producto", "Cant.", "P. unitario", "Subtotal"]],
            body: filas,
            theme: "striped",
            headStyles: { fillColor: [33, 37, 41], textColor: 255 },
            styles: { font: "helvetica", fontSize: 10, cellPadding: 3 },
            columnStyles: {
                1: { halign: "center", cellWidth: 18 },
                2: { halign: "right", cellWidth: 32 },
                3: { halign: "right", cellWidth: 32 }
            },
            margin: { left: margen, right: margen }
        });

        // ---------- Totales ----------
        let yTotales = doc.lastAutoTable.finalY + 10;

        // Si no entran los totales en la página, pasar a una nueva
        if (yTotales > doc.internal.pageSize.getHeight() - 45) {
            doc.addPage();
            yTotales = 25;
        }

        const xEtiqueta = derecha - 70;

        doc.setFont("helvetica", "normal");
        doc.setFontSize(10);
        doc.text("Subtotal", xEtiqueta, yTotales);
        doc.text(moneda(pedido.subtotal), derecha, yTotales, { align: "right" });

        doc.text("Envío a domicilio", xEtiqueta, yTotales + 6);
        doc.text(moneda(pedido.envio), derecha, yTotales + 6, { align: "right" });

        doc.setDrawColor(150);
        doc.line(xEtiqueta, yTotales + 9, derecha, yTotales + 9);

        doc.setFont("helvetica", "bold");
        doc.setFontSize(12);
        doc.text("Total pagado", xEtiqueta, yTotales + 16);
        doc.text(moneda(pedido.total), derecha, yTotales + 16, { align: "right" });

        // ---------- Pie de página ----------
        doc.setFont("helvetica", "normal");
        doc.setFontSize(9);
        doc.setTextColor(120);
        doc.text(
            "Gracias por tu compra en Yuliana Calzados.",
            anchoPagina / 2,
            doc.internal.pageSize.getHeight() - 12,
            { align: "center" }
        );

        doc.save("Comprobante-" + pedido.numero + ".pdf");
    }

    boton.addEventListener("click", function () {
        const pedido = obtenerPedido();

        if (!pedido) {
            alert("No se encontró una compra reciente para exportar.");
            return;
        }

        if (!window.jspdf || !window.jspdf.jsPDF) {
            alert("No se pudo cargar la librería de PDF. Revisa tu conexión a internet e inténtalo de nuevo.");
            return;
        }

        try {
            generarPDF(pedido);
        } catch (error) {
            console.error("Error al generar el PDF:", error);
            alert("Ocurrió un error al generar el PDF.");
        }
    });
});
