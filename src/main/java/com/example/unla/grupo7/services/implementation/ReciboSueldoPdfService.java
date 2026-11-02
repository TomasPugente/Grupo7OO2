package com.example.unla.grupo7.services.implementation;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.example.unla.grupo7.dtos.LiquidacionDTO;
import com.example.unla.grupo7.entities.Festival;
import com.example.unla.grupo7.entities.Staff;

import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;

@Service
public class ReciboSueldoPdfService {

    private static final Locale LOCALE_AR = Locale.forLanguageTag("es-AR");

    private static final NumberFormat MONEDA =
            NumberFormat.getCurrencyInstance(LOCALE_AR);

    private static final DateTimeFormatter FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] generar(
            Staff staff,
            Festival festival,
            LiquidacionDTO liquidacion) {

        ByteArrayOutputStream salida = new ByteArrayOutputStream();

        Document documento = new Document(PageSize.A4, 50, 50, 45, 45);

        try {
            PdfWriter.getInstance(documento, salida);

            documento.open();

            Font titulo = new Font(Font.HELVETICA, 20, Font.BOLD);
            Font subtitulo = new Font(Font.HELVETICA, 13, Font.BOLD);
            Font normal = new Font(Font.HELVETICA, 11);
            Font negrita = new Font(Font.HELVETICA, 11, Font.BOLD);
            Font totalFont = new Font(Font.HELVETICA, 16, Font.BOLD);

            // Encabezado
            Paragraph encabezado = new Paragraph(
                    "EPICENTRO GOURMET", titulo);
            encabezado.setAlignment(Element.ALIGN_CENTER);
            documento.add(encabezado);

            Paragraph recibo = new Paragraph(
                    "RECIBO DE SUELDO", subtitulo);
            recibo.setAlignment(Element.ALIGN_CENTER);
            recibo.setSpacingBefore(10);
            recibo.setSpacingAfter(20);
            documento.add(recibo);

            // Datos del empleado
            documento.add(new Paragraph("DATOS DEL EMPLEADO", subtitulo));
            documento.add(new Paragraph(" "));

            PdfPTable tablaEmpleado = new PdfPTable(2);
            tablaEmpleado.setWidthPercentage(100);
            tablaEmpleado.setWidths(new float[]{1, 1});

            agregarFila(tablaEmpleado, "Nombre y apellido",
                    liquidacion.getEmpleado(), negrita, normal);

            agregarFila(tablaEmpleado, "DNI",
                    String.valueOf(staff.getDni()), negrita, normal);

            agregarFila(tablaEmpleado, "Email",
                    staff.getEmail(), negrita, normal);

            agregarFila(tablaEmpleado, "Fecha de ingreso",
                    staff.getFechaIngreso() != null
                            ? staff.getFechaIngreso().format(FECHA)
                            : "-", negrita, normal);

            documento.add(tablaEmpleado);

            // Datos del festival
            documento.add(new Paragraph(" "));
            documento.add(new Paragraph("DATOS DEL FESTIVAL", subtitulo));
            documento.add(new Paragraph(" "));

            PdfPTable tablaFestival = new PdfPTable(2);
            tablaFestival.setWidthPercentage(100);

            agregarFila(tablaFestival, "Festival",
                    festival.getNombre(), negrita, normal);

            agregarFila(tablaFestival, "Fecha de inicio",
                    festival.getFechaInicio().format(FECHA), negrita, normal);

            agregarFila(tablaFestival, "Fecha de finalización",
                    festival.getFechaFin().format(FECHA), negrita, normal);

            documento.add(tablaFestival);

            // Liquidación
            documento.add(new Paragraph(" "));
            documento.add(new Paragraph("DETALLE DE LA LIQUIDACIÓN", subtitulo));
            documento.add(new Paragraph(" "));

            PdfPTable tablaLiquidacion = new PdfPTable(2);
            tablaLiquidacion.setWidthPercentage(100);
            tablaLiquidacion.setWidths(new float[]{2, 1});

            agregarFila(tablaLiquidacion, "Categoría",
                    liquidacion.getRol(), negrita, normal);

            agregarFila(tablaLiquidacion, "Días del festival",
                    String.valueOf(liquidacion.getDiasFestival()), negrita, normal);

            agregarFila(tablaLiquidacion, "Porcentaje aplicado",
                    liquidacion.getPorcentaje() + "%", negrita, normal);

            agregarFila(tablaLiquidacion, "Sueldo base",
                    MONEDA.format(liquidacion.getSueldoBase()), negrita, normal);

            String conceptoAdicional =
                    liquidacion.getAntiguedadAnios() > 0
                            ? "Adicional por antigüedad"
                            : "Plus por categoría";

            agregarFila(tablaLiquidacion, conceptoAdicional,
                    MONEDA.format(liquidacion.getAdicional()), negrita, normal);

            documento.add(tablaLiquidacion);

            // Total
            documento.add(new Paragraph(" "));

            PdfPTable tablaTotal = new PdfPTable(2);
            tablaTotal.setWidthPercentage(100);

            PdfPCell celdaTexto = new PdfPCell(
                    new Phrase("TOTAL LIQUIDADO", negrita));

            PdfPCell celdaImporte = new PdfPCell(
                    new Phrase(MONEDA.format(liquidacion.getTotal()), totalFont));

            celdaTexto.setBorder(Rectangle.BOX);
            celdaImporte.setBorder(Rectangle.BOX);
            celdaImporte.setHorizontalAlignment(Element.ALIGN_RIGHT);

            celdaTexto.setPadding(12);
            celdaImporte.setPadding(12);

            tablaTotal.addCell(celdaTexto);
            tablaTotal.addCell(celdaImporte);

            documento.add(tablaTotal);

            // Pie
            Paragraph pie = new Paragraph(
                    "\nComprobante de liquidación generado por Epicentro Gourmet.",
                    normal);
            pie.setAlignment(Element.ALIGN_CENTER);
            pie.setSpacingBefore(25);

            documento.add(pie);

            documento.close();

            return salida.toByteArray();

        } catch (DocumentException e) {
            throw new IllegalStateException(
                    "No se pudo generar el recibo de sueldo.", e);
        }
    }

    private void agregarFila(
            PdfPTable tabla,
            String etiqueta,
            String valor,
            Font negrita,
            Font normal) {

        PdfPCell celdaEtiqueta =
                new PdfPCell(new Phrase(etiqueta, negrita));

        PdfPCell celdaValor =
                new PdfPCell(new Phrase(
                        valor != null ? valor : "-", normal));

        celdaEtiqueta.setPadding(8);
        celdaValor.setPadding(8);

        tabla.addCell(celdaEtiqueta);
        tabla.addCell(celdaValor);
    }
}