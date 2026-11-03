package com.example.unla.grupo7.services.implementation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.openpdf.text.Chunk;
import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Font;
import org.openpdf.text.FontFactory;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import com.example.unla.grupo7.entities.CierreCaja;
import com.example.unla.grupo7.entities.Pedido;
import com.example.unla.grupo7.entities.UnidadDeVenta;

    @Service 
public class CierreCajaPdfService {

    private static final Font TITULO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
    private static final Font NEGRITA = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
    private static final Font NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 10);

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    public byte[] generar(CierreCaja cierre, List<Pedido> pedidos) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 40, 40, 40, 40);
            PdfWriter.getInstance(doc, out);
            doc.open();

            doc.add(new Paragraph("Cierre de caja", TITULO));
            doc.add(new Paragraph(" "));

            UnidadDeVenta unidad = cierre.getUnidadDeVenta();
            doc.add(linea("Unidad de venta: ", unidad.getNombreComercial()));
            doc.add(linea("Código: ", unidad.getCodigo()));
            doc.add(linea("Festival: ", unidad.getFestival().getNombre()));
            doc.add(linea("Fecha: ", cierre.getFecha().format(FECHA)));
            doc.add(linea("Cajero: ", cierre.getCajero().getNombre() + " " + cierre.getCajero().getApellido()));
            doc.add(linea("Cantidad de pedidos: ", String.valueOf(cierre.getCantidadPedidos())));
            doc.add(linea("Total vendido: ", dinero(cierre.getTotalVentas())));
            doc.add(new Paragraph(" "));

            doc.add(new Paragraph("Detalle de pedidos", NEGRITA));
            doc.add(new Paragraph(" "));

            PdfPTable tabla = new PdfPTable(new float[]{1, 1.2f, 5, 2});
            tabla.setWidthPercentage(100);
            for (String h : List.of("N°", "Hora", "Detalle", "Total")) {
                tabla.addCell(new PdfPCell(new Phrase(h, NEGRITA)));
            }
            for (Pedido p : pedidos) {
                String detalle = p.getDetallePedidos().stream()
                        .map(d -> d.getCantidad() + " x " + d.getPlato().getNombre())
                        .collect(Collectors.joining(", "));
                tabla.addCell(new Phrase("#" + p.getIdPedido(), NORMAL));
                tabla.addCell(new Phrase(p.getCreatedAt().format(HORA), NORMAL));
                tabla.addCell(new Phrase(detalle, NORMAL));
                tabla.addCell(new Phrase(dinero(p.getPrecioTotal()), NORMAL));
            }
            doc.add(tabla);

            doc.close();
            return out.toByteArray();
        } catch (DocumentException | IOException e) {
            throw new IllegalStateException("No se pudo generar el PDF del cierre de caja", e);
        }
    }

    private Paragraph linea(String etiqueta, String valor) {
        Paragraph p = new Paragraph();
        p.add(new Chunk(etiqueta, NEGRITA));
        p.add(new Chunk(valor, NORMAL));
        return p;
    }

    private String dinero(BigDecimal monto) {
        return "$ " + String.format(Locale.forLanguageTag("es-AR"), "%,.2f", monto);
    }
}

