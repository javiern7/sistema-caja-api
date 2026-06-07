package pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.application;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.CashReportResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.ExpenseReportResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.PurchaseReportResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.SalesReportResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.StockReportResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.UtilityReportResponse;

@Service
public class ReportExportService {

    public byte[] exportSalesToExcel(SalesReportResponse report) {
        return createExcel(
                "Ventas",
                List.of("Venta", "Fecha", "Contexto", "Usuario", "Comprobante", "Total", "Items"),
                report.items().stream()
                        .map(item -> List.of(
                                item.saleId(),
                                item.createdAt(),
                                item.operationalContextName(),
                                item.soldByUsername(),
                                item.internalReceipt(),
                                item.totalAmount(),
                                item.itemsCount()
                        ))
                        .toList(),
                List.of(
                        List.of("Total ventas", report.totalSales()),
                        List.of("Monto total", report.totalAmount())
                ),
                "No se pudo exportar el reporte de ventas."
        );
    }

    public byte[] exportSalesToPdf(SalesReportResponse report) {
        return createPdf(
                "Reporte de ventas",
                List.of(
                        "Total ventas: " + report.totalSales(),
                        "Monto total: " + safe(report.totalAmount()).toPlainString()
                ),
                List.of("Venta", "Fecha", "Contexto", "Usuario", "Comprobante", "Total", "Items"),
                report.items().stream()
                        .map(item -> List.of(
                                item.saleId(),
                                item.createdAt(),
                                item.operationalContextName(),
                                item.soldByUsername(),
                                item.internalReceipt(),
                                item.totalAmount(),
                                item.itemsCount()
                        ))
                        .toList(),
                "No se pudo exportar el reporte de ventas."
        );
    }

    public byte[] exportCashToExcel(CashReportResponse report) {
        return createExcel(
                "Caja",
                List.of("Caja", "Contexto", "Estado", "Abierto por", "Cerrado por", "Apertura", "Esperado", "Contado", "Diferencia", "Abierta", "Cerrada"),
                report.items().stream()
                        .map(item -> List.of(
                                item.cashBoxId(),
                                item.operationalContextName(),
                                item.status().name(),
                                item.openedByUsername(),
                                item.closedByUsername(),
                                item.openingAmount(),
                                item.expectedAmount(),
                                item.countedAmount(),
                                item.differenceAmount(),
                                item.openedAt(),
                                item.closedAt()
                        ))
                        .toList(),
                List.of(
                        List.of("Total cajas", report.totalCashBoxes()),
                        List.of("Monto apertura", report.totalOpeningAmount()),
                        List.of("Monto esperado", report.totalExpectedAmount()),
                        List.of("Diferencia total", report.totalDifferenceAmount())
                ),
                "No se pudo exportar el reporte de caja."
        );
    }

    public byte[] exportCashToPdf(CashReportResponse report) {
        return createPdf(
                "Reporte de caja",
                List.of(
                        "Total cajas: " + report.totalCashBoxes(),
                        "Monto apertura: " + safe(report.totalOpeningAmount()).toPlainString(),
                        "Monto esperado: " + safe(report.totalExpectedAmount()).toPlainString(),
                        "Diferencia total: " + safe(report.totalDifferenceAmount()).toPlainString()
                ),
                List.of("Caja", "Contexto", "Estado", "Apertura", "Esperado", "Diferencia", "Abierta"),
                report.items().stream()
                        .map(item -> List.of(
                                item.cashBoxId(),
                                item.operationalContextName(),
                                item.status().name(),
                                item.openingAmount(),
                                item.expectedAmount(),
                                item.differenceAmount(),
                                item.openedAt()
                        ))
                        .toList(),
                "No se pudo exportar el reporte de caja."
        );
    }

    public byte[] exportPurchasesToExcel(PurchaseReportResponse report) {
        return createExcel(
                "Compras",
                List.of("Compra", "Fecha", "Contexto", "Proveedor", "Estado", "Monto efectivo"),
                report.items().stream()
                        .map(item -> List.of(
                                item.purchaseId(),
                                item.purchaseDate(),
                                item.operationalContextName(),
                                item.providerName(),
                                item.status(),
                                item.effectiveAmount()
                        ))
                        .toList(),
                List.of(
                        List.of("Total compras", report.totalPurchases()),
                        List.of("Monto total", report.totalAmount())
                ),
                "No se pudo exportar el reporte de compras."
        );
    }

    public byte[] exportPurchasesToPdf(PurchaseReportResponse report) {
        return createPdf(
                "Reporte de compras",
                List.of(
                        "Total compras: " + report.totalPurchases(),
                        "Monto total: " + safe(report.totalAmount()).toPlainString()
                ),
                List.of("Compra", "Fecha", "Contexto", "Proveedor", "Estado", "Monto"),
                report.items().stream()
                        .map(item -> List.of(
                                item.purchaseId(),
                                item.purchaseDate(),
                                item.operationalContextName(),
                                item.providerName(),
                                item.status(),
                                item.effectiveAmount()
                        ))
                        .toList(),
                "No se pudo exportar el reporte de compras."
        );
    }

    public byte[] exportExpensesToExcel(ExpenseReportResponse report) {
        return createExcel(
                "Egresos",
                List.of("Egreso", "Fecha", "Contexto", "Tipo", "Categoria", "Descripcion", "Monto", "Registrado por"),
                report.items().stream()
                        .map(item -> List.of(
                                item.expenseId(),
                                item.expenseDate(),
                                item.operationalContextName(),
                                item.expenseType(),
                                item.category(),
                                item.description(),
                                item.amount(),
                                item.recordedByUsername()
                        ))
                        .toList(),
                List.of(
                        List.of("Total egresos", report.totalExpenses()),
                        List.of("Monto total", report.totalAmount())
                ),
                "No se pudo exportar el reporte de egresos."
        );
    }

    public byte[] exportExpensesToPdf(ExpenseReportResponse report) {
        return createPdf(
                "Reporte de egresos",
                List.of(
                        "Total egresos: " + report.totalExpenses(),
                        "Monto total: " + safe(report.totalAmount()).toPlainString()
                ),
                List.of("Egreso", "Fecha", "Contexto", "Tipo", "Categoria", "Monto"),
                report.items().stream()
                        .map(item -> List.of(
                                item.expenseId(),
                                item.expenseDate(),
                                item.operationalContextName(),
                                item.expenseType(),
                                item.category(),
                                item.amount()
                        ))
                        .toList(),
                "No se pudo exportar el reporte de egresos."
        );
    }

    public byte[] exportStockToExcel(StockReportResponse report) {
        return createExcel(
                "Stock",
                List.of("Producto", "Codigo", "Unidad", "Activo", "Controlado", "Stock minimo", "Stock actual", "Actualizado"),
                report.items().stream()
                        .map(item -> List.of(
                                item.productName(),
                                item.productCode(),
                                item.unitOfMeasure(),
                                item.active(),
                                item.stockControlled(),
                                item.minimumStock(),
                                item.currentStock(),
                                item.updatedAt()
                        ))
                        .toList(),
                List.of(
                        List.of("Alcance", report.stockScope()),
                        List.of("Contexto", report.requestedOperationalContextId()),
                        List.of("Total productos", report.totalProducts()),
                        List.of("Total unidades", report.totalUnits())
                ),
                "No se pudo exportar el reporte de stock."
        );
    }

    public byte[] exportStockToPdf(StockReportResponse report) {
        return createPdf(
                "Reporte de stock",
                List.of(
                        "Alcance: " + report.stockScope(),
                        "Contexto: " + report.requestedOperationalContextId(),
                        "Total productos: " + report.totalProducts(),
                        "Total unidades: " + safe(report.totalUnits()).toPlainString()
                ),
                List.of("Codigo", "Producto", "Unidad", "Minimo", "Actual"),
                report.items().stream()
                        .map(item -> List.of(
                                item.productCode(),
                                item.productName(),
                                item.unitOfMeasure(),
                                item.minimumStock(),
                                item.currentStock()
                        ))
                        .toList(),
                "No se pudo exportar el reporte de stock."
        );
    }

    public byte[] exportUtilityToExcel(UtilityReportResponse report) {
        return createExcel(
                "Utilidad",
                List.of("Concepto", "Monto"),
                List.of(
                        List.of("Ventas", report.salesAmount()),
                        List.of("Compras", report.purchaseAmount()),
                        List.of("Egresos", report.expenseAmount()),
                        List.of("Costo estimado de ventas", report.estimatedCostOfSales()),
                        List.of("Margen bruto", report.grossMargin()),
                        List.of("Utilidad neta", report.netUtility())
                ),
                List.of(
                        List.of("Contexto", report.operationalContextId()),
                        List.of("Fecha desde", report.fechaDesde()),
                        List.of("Fecha hasta", report.fechaHasta())
                ),
                "No se pudo exportar el reporte de utilidad."
        );
    }

    public byte[] exportUtilityToPdf(UtilityReportResponse report) {
        return createPdf(
                "Reporte de utilidad",
                List.of(
                        "Contexto: " + report.operationalContextId(),
                        "Fecha desde: " + report.fechaDesde(),
                        "Fecha hasta: " + report.fechaHasta()
                ),
                List.of("Concepto", "Monto"),
                List.of(
                        List.of("Ventas", report.salesAmount()),
                        List.of("Compras", report.purchaseAmount()),
                        List.of("Egresos", report.expenseAmount()),
                        List.of("Costo estimado de ventas", report.estimatedCostOfSales()),
                        List.of("Margen bruto", report.grossMargin()),
                        List.of("Utilidad neta", report.netUtility())
                ),
                "No se pudo exportar el reporte de utilidad."
        );
    }

    private byte[] createExcel(
            String sheetName,
            List<String> headers,
            List<? extends List<?>> rows,
            List<? extends List<?>> summaryRows,
            String errorMessage
    ) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(sheetName);
            writeHeaderRow(sheet.createRow(0), headers);

            int rowIndex = 1;
            for (List<?> rowValues : rows) {
                writeDataRow(sheet.createRow(rowIndex++), rowValues);
            }

            if (!summaryRows.isEmpty()) {
                rowIndex++;
                for (List<?> summary : summaryRows) {
                    writeDataRow(sheet.createRow(rowIndex++), summary);
                }
            }

            autoSizeColumns(sheet, headers.size());
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException(errorMessage, exception);
        }
    }

    private byte[] createPdf(
            String title,
            List<String> summaryLines,
            List<String> headers,
            List<? extends List<?>> rows,
            String errorMessage
    ) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();
            document.add(new Paragraph(title));
            for (String line : summaryLines) {
                document.add(new Paragraph(line));
            }
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(100);
            headers.forEach(header -> addHeaderCell(table, header));
            for (List<?> row : rows) {
                for (Object value : row) {
                    table.addCell(stringValue(value));
                }
            }

            document.add(table);
            document.close();
            return outputStream.toByteArray();
        } catch (DocumentException | IOException exception) {
            throw new IllegalStateException(errorMessage, exception);
        }
    }

    private void writeHeaderRow(Row row, List<String> headers) {
        for (int i = 0; i < headers.size(); i++) {
            row.createCell(i).setCellValue(headers.get(i));
        }
    }

    private void writeDataRow(Row row, List<?> values) {
        for (int i = 0; i < values.size(); i++) {
            row.createCell(i).setCellValue(stringValue(values.get(i)));
        }
    }

    private void autoSizeColumns(Sheet sheet, int size) {
        for (int i = 0; i < size; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private String stringValue(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof BigDecimal decimal) {
            return decimal.toPlainString();
        }
        return String.valueOf(value);
    }

    private void addHeaderCell(PdfPTable table, String value) {
        PdfPCell header = new PdfPCell(new Phrase(value));
        table.addCell(header);
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
