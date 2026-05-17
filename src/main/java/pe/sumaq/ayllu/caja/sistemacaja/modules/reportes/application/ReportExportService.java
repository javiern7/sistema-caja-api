package pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.application;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
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
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.SalesReportResponse;

@Service
public class ReportExportService {

    public byte[] exportSalesToExcel(SalesReportResponse report) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.createSheet("Ventas");
            createSalesHeader(sheet.createRow(0));

            int rowIndex = 1;
            for (var item : report.items()) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(item.saleId());
                row.createCell(1).setCellValue(item.createdAt().toString());
                row.createCell(2).setCellValue(item.operationalContextName());
                row.createCell(3).setCellValue(item.soldByUsername());
                row.createCell(4).setCellValue(item.internalReceipt());
                row.createCell(5).setCellValue(item.totalAmount().doubleValue());
                row.createCell(6).setCellValue(item.itemsCount());
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo exportar el reporte de ventas.", exception);
        }
    }

    public byte[] exportCashToPdf(CashReportResponse report) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();
            document.add(new Paragraph("Reporte de caja"));
            document.add(new Paragraph("Total cajas: " + report.totalCashBoxes()));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            addHeaderCell(table, "Caja");
            addHeaderCell(table, "Contexto");
            addHeaderCell(table, "Estado");
            addHeaderCell(table, "Apertura");
            addHeaderCell(table, "Esperado");
            addHeaderCell(table, "Diferencia");
            addHeaderCell(table, "Abierta");

            for (var item : report.items()) {
                table.addCell(item.cashBoxId().toString());
                table.addCell(item.operationalContextName());
                table.addCell(item.status().name());
                table.addCell(item.openingAmount().toPlainString());
                table.addCell(item.expectedAmount().toPlainString());
                table.addCell(item.differenceAmount().toPlainString());
                table.addCell(item.openedAt().toString());
            }

            document.add(table);
            document.close();
            return outputStream.toByteArray();
        } catch (DocumentException | IOException exception) {
            throw new IllegalStateException("No se pudo exportar el reporte de caja.", exception);
        }
    }

    private void createSalesHeader(Row row) {
        row.createCell(0).setCellValue("Venta");
        row.createCell(1).setCellValue("Fecha");
        row.createCell(2).setCellValue("Contexto");
        row.createCell(3).setCellValue("Usuario");
        row.createCell(4).setCellValue("Comprobante");
        row.createCell(5).setCellValue("Total");
        row.createCell(6).setCellValue("Items");
    }

    private void addHeaderCell(PdfPTable table, String value) {
        PdfPCell header = new PdfPCell(new Phrase(value));
        table.addCell(header);
    }
}
