package pe.sumaq.ayllu.caja.sistemacaja.common.application;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import pe.sumaq.ayllu.caja.sistemacaja.common.exception.BusinessException;
import pe.sumaq.ayllu.caja.sistemacaja.common.exception.ErrorCode;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.presentation.dto.PurchaseResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.presentation.dto.ExpenseResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.presentation.dto.SaleResponse;

@Service
public class OperationalDetailPdfExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public byte[] exportSaleDetailToPdf(SaleResponse sale) {
        LinkedHashMap<String, Object> summary = new LinkedHashMap<>();
        summary.put("Venta", sale.id());
        summary.put("Contexto", sale.operationalContextName());
        summary.put("Caja", sale.cashBoxId());
        summary.put("Vendido por", sale.soldByUsername());
        summary.put("Estado", sale.status());
        summary.put("Comprobante", resolveSaleReference(sale));
        summary.put("Subtotal", sale.subtotalAmount());
        summary.put("Total", sale.totalAmount());
        summary.put("Fecha de registro", sale.createdAt());
        summary.put("Observacion", sale.observation());
        if (sale.cancelledAt() != null) {
            summary.put("Fecha de anulacion", sale.cancelledAt());
            summary.put("Anulado por", sale.cancelledByUsername());
            summary.put("Motivo de anulacion", sale.cancellationReason());
        }

        return createPdf(
                "Detalle operativo de venta",
                summary,
                List.of("Codigo", "Producto", "Cantidad", "Precio unitario", "Subtotal"),
                sale.items().stream()
                        .map(item -> List.of(
                                item.productCode(),
                                item.productName(),
                                format(item.quantity()),
                                format(item.unitPrice()),
                                format(item.subtotalAmount())
                        ))
                        .toList(),
                sale.payments().isEmpty() ? null : List.of("Metodo", "Monto"),
                sale.payments().stream()
                        .map(payment -> List.of(
                                payment.paymentMethod(),
                                format(payment.amount())
                        ))
                        .toList(),
                "No se pudo exportar el detalle de venta."
        );
    }

    public byte[] exportPurchaseDetailToPdf(PurchaseResponse purchase) {
        LinkedHashMap<String, Object> summary = new LinkedHashMap<>();
        summary.put("Compra", purchase.id());
        summary.put("Contexto", purchase.operationalContextName());
        summary.put("Proveedor", purchase.providerName());
        summary.put("Estado", purchase.status());
        summary.put("Fecha de compra", purchase.purchaseDate());
        summary.put("Documento", purchase.documentType());
        summary.put("Numero de documento", purchase.documentNumber());
        summary.put("Metodo de pago", purchase.paymentMethod());
        summary.put("Subtotal", purchase.subtotalAmount());
        summary.put("Total", purchase.totalAmount());
        summary.put("Fecha de registro", purchase.createdAt());
        summary.put("Observacion", purchase.observation());
        if (purchase.cancelledAt() != null) {
            summary.put("Fecha de anulacion", purchase.cancelledAt());
            summary.put("Anulado por", purchase.cancelledByUsername());
            summary.put("Motivo de anulacion", purchase.cancellationReason());
        }

        return createPdf(
                "Detalle operativo de compra",
                summary,
                List.of("Codigo", "Producto", "Cantidad", "Cantidad anulada", "Costo unitario", "Subtotal"),
                purchase.items().stream()
                        .map(item -> List.of(
                                item.productCode(),
                                item.productName(),
                                format(item.quantity()),
                                format(item.cancelledQuantity()),
                                format(item.unitCost()),
                                format(item.subtotalAmount())
                        ))
                        .toList(),
                null,
                List.of(),
                "No se pudo exportar el detalle de compra."
        );
    }

    public byte[] exportExpenseDetailToPdf(ExpenseResponse expense) {
        LinkedHashMap<String, Object> summary = new LinkedHashMap<>();
        summary.put("Egreso", expense.id());
        summary.put("Contexto", expense.operationalContextName());
        summary.put("Caja", expense.cashBoxId());
        summary.put("Tipo", expense.expenseType());
        summary.put("Categoria", expense.category());
        summary.put("Descripcion", expense.description());
        summary.put("Metodo de pago", expense.paymentMethod());
        summary.put("Responsable", expense.responsible());
        summary.put("Monto", expense.amount());
        summary.put("Registrado por", expense.recordedByUsername());
        summary.put("Fecha de egreso", expense.expenseDate());
        summary.put("Fecha de registro", expense.createdAt());
        summary.put("Observacion", expense.observation());

        return createPdf(
                "Detalle operativo de egreso",
                summary,
                null,
                List.of(),
                null,
                List.of(),
                "No se pudo exportar el detalle de egreso."
        );
    }

    public String saleFileName(SaleResponse sale) {
        return "venta-" + sanitize(resolveSaleReference(sale, "id-" + sale.id())) + ".pdf";
    }

    public String purchaseFileName(PurchaseResponse purchase) {
        return "compra-" + sanitize(firstNonBlank(purchase.documentNumber(), "id-" + purchase.id())) + ".pdf";
    }

    public String expenseFileName(ExpenseResponse expense) {
        return "egreso-" + sanitize("id-" + expense.id()) + ".pdf";
    }

    private byte[] createPdf(
            String title,
            LinkedHashMap<String, Object> summary,
            List<String> detailHeaders,
            List<List<String>> detailRows,
            List<String> secondaryHeaders,
            List<List<String>> secondaryRows,
            String errorMessage
    ) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            document.add(new Paragraph(title, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
            document.add(new Paragraph(" "));
            addSummaryTable(document, summary);

            if (detailHeaders != null && !detailHeaders.isEmpty()) {
                document.add(new Paragraph(" "));
                document.add(new Paragraph("Detalle", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
                addDataTable(document, detailHeaders, detailRows);
            }

            if (secondaryHeaders != null && !secondaryHeaders.isEmpty() && secondaryRows != null && !secondaryRows.isEmpty()) {
                document.add(new Paragraph(" "));
                document.add(new Paragraph("Pagos", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
                addDataTable(document, secondaryHeaders, secondaryRows);
            }

            document.close();
            return outputStream.toByteArray();
        } catch (DocumentException | java.io.IOException exception) {
            throw new BusinessException(
                    ErrorCode.INTERNAL_ERROR,
                    org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                    errorMessage,
                    List.of(exception.getMessage())
            );
        }
    }

    private void addSummaryTable(Document document, LinkedHashMap<String, Object> summary) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(4f);
        table.setSpacingAfter(4f);

        for (Map.Entry<String, Object> entry : summary.entrySet()) {
            addHeaderCell(table, entry.getKey());
            addBodyCell(table, format(entry.getValue()));
        }

        document.add(table);
    }

    private void addDataTable(Document document, List<String> headers, List<List<String>> rows) throws DocumentException {
        PdfPTable table = new PdfPTable(headers.size());
        table.setWidthPercentage(100);
        table.setSpacingBefore(4f);

        for (String header : headers) {
            addHeaderCell(table, header);
        }

        if (rows == null || rows.isEmpty()) {
            PdfPCell empty = new PdfPCell(new Phrase("Sin registros"));
            empty.setColspan(headers.size());
            table.addCell(empty);
        } else {
            for (List<String> row : rows) {
                for (String value : row) {
                    addBodyCell(table, value);
                }
            }
        }

        document.add(table);
    }

    private void addHeaderCell(PdfPTable table, String value) {
        PdfPCell header = new PdfPCell(new Phrase(value, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        table.addCell(header);
    }

    private void addBodyCell(PdfPTable table, String value) {
        table.addCell(new Phrase(value, FontFactory.getFont(FontFactory.HELVETICA, 10)));
    }

    private String resolveSaleReference(SaleResponse sale) {
        return resolveSaleReference(sale, String.valueOf(sale.id()));
    }

    private String resolveSaleReference(SaleResponse sale, String fallback) {
        if (sale.internalReceiptSeries() != null && sale.internalReceiptNumber() != null) {
            return sale.internalReceiptSeries() + "-" + sale.internalReceiptNumber();
        }
        return fallback;
    }

    private String firstNonBlank(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private String sanitize(String value) {
        return value.replaceAll("[^A-Za-z0-9._-]+", "-");
    }

    private String format(Object value) {
        if (value == null) {
            return "-";
        }
        if (value instanceof BigDecimal decimal) {
            return decimal.toPlainString();
        }
        if (value instanceof LocalDate date) {
            return date.format(DATE_FORMATTER);
        }
        if (value instanceof LocalDateTime dateTime) {
            return dateTime.format(DATE_TIME_FORMATTER);
        }
        return String.valueOf(value);
    }
}
