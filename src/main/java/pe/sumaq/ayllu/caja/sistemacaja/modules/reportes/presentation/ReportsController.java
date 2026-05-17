package pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pe.sumaq.ayllu.caja.sistemacaja.common.api.ApiResponse;
import pe.sumaq.ayllu.caja.sistemacaja.common.api.ApiResponseFactory;
import pe.sumaq.ayllu.caja.sistemacaja.common.exception.BusinessException;
import pe.sumaq.ayllu.caja.sistemacaja.common.exception.ErrorCode;
import pe.sumaq.ayllu.caja.sistemacaja.modules.auth.infrastructure.SecurityUserPrincipal;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.application.GetReportHistoryUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.application.ReportExportService;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.application.ReportsQueryService;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.domain.ReportFormat;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.CashReportResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.ExpenseReportResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.PurchaseReportResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.ReportHistoryResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.SalesReportResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.StockReportResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.UtilityReportResponse;

@RestController
@RequestMapping("/api/v1/reportes")
@PreAuthorize("hasAnyAuthority('reporte.ver', 'reporte.ventas', 'reporte.caja', 'reporte.compras', 'reporte.egresos', 'reporte.stock', 'reporte.utilidad')")
public class ReportsController {

    private final ReportsQueryService reportsQueryService;
    private final GetReportHistoryUseCase getReportHistoryUseCase;
    private final ReportExportService reportExportService;
    private final ApiResponseFactory responseFactory;

    public ReportsController(
            ReportsQueryService reportsQueryService,
            GetReportHistoryUseCase getReportHistoryUseCase,
            ReportExportService reportExportService,
            ApiResponseFactory responseFactory
    ) {
        this.reportsQueryService = reportsQueryService;
        this.getReportHistoryUseCase = getReportHistoryUseCase;
        this.reportExportService = reportExportService;
        this.responseFactory = responseFactory;
    }

    @GetMapping("/ventas")
    public ApiResponse<SalesReportResponse> getSalesReport(
            Authentication authentication,
            @RequestParam(required = false) LocalDate fechaDesde,
            @RequestParam(required = false) LocalDate fechaHasta,
            @RequestParam(required = false) Long operationalContextId
    ) {
        return responseFactory.success(
                "Reporte de ventas obtenido correctamente.",
                reportsQueryService.getSalesReport(
                        fechaDesde,
                        fechaHasta,
                        operationalContextId,
                        extractPrincipal(authentication).getUsername(),
                        ReportFormat.JSON
                )
        );
    }

    @GetMapping("/caja")
    public ApiResponse<CashReportResponse> getCashReport(
            Authentication authentication,
            @RequestParam(required = false) LocalDate fechaDesde,
            @RequestParam(required = false) LocalDate fechaHasta,
            @RequestParam(required = false) Long operationalContextId
    ) {
        return responseFactory.success(
                "Reporte de caja obtenido correctamente.",
                reportsQueryService.getCashReport(
                        fechaDesde,
                        fechaHasta,
                        operationalContextId,
                        extractPrincipal(authentication).getUsername(),
                        ReportFormat.JSON
                )
        );
    }

    @GetMapping("/compras")
    public ApiResponse<PurchaseReportResponse> getPurchasesReport(
            Authentication authentication,
            @RequestParam(required = false) LocalDate fechaDesde,
            @RequestParam(required = false) LocalDate fechaHasta,
            @RequestParam(required = false) Long operationalContextId
    ) {
        return responseFactory.success(
                "Reporte de compras obtenido correctamente.",
                reportsQueryService.getPurchasesReport(
                        fechaDesde,
                        fechaHasta,
                        operationalContextId,
                        extractPrincipal(authentication).getUsername(),
                        ReportFormat.JSON
                )
        );
    }

    @GetMapping("/egresos")
    public ApiResponse<ExpenseReportResponse> getExpensesReport(
            Authentication authentication,
            @RequestParam(required = false) LocalDate fechaDesde,
            @RequestParam(required = false) LocalDate fechaHasta,
            @RequestParam(required = false) Long operationalContextId
    ) {
        return responseFactory.success(
                "Reporte de egresos obtenido correctamente.",
                reportsQueryService.getExpensesReport(
                        fechaDesde,
                        fechaHasta,
                        operationalContextId,
                        extractPrincipal(authentication).getUsername(),
                        ReportFormat.JSON
                )
        );
    }

    @GetMapping("/stock")
    public ApiResponse<StockReportResponse> getStockReport(
            Authentication authentication,
            @RequestParam(required = false) LocalDate fechaDesde,
            @RequestParam(required = false) LocalDate fechaHasta,
            @RequestParam(required = false) Long operationalContextId
    ) {
        return responseFactory.success(
                "Reporte de stock obtenido correctamente.",
                reportsQueryService.getStockReport(
                        fechaDesde,
                        fechaHasta,
                        operationalContextId,
                        extractPrincipal(authentication).getUsername(),
                        ReportFormat.JSON
                )
        );
    }

    @GetMapping("/utilidad")
    public ApiResponse<UtilityReportResponse> getUtilityReport(
            Authentication authentication,
            @RequestParam(required = false) LocalDate fechaDesde,
            @RequestParam(required = false) LocalDate fechaHasta,
            @RequestParam(required = false) Long operationalContextId
    ) {
        return responseFactory.success(
                "Reporte de utilidad obtenido correctamente.",
                reportsQueryService.getUtilityReport(
                        fechaDesde,
                        fechaHasta,
                        operationalContextId,
                        extractPrincipal(authentication).getUsername(),
                        ReportFormat.JSON
                )
        );
    }

    @GetMapping("/ventas/exportar")
    @PreAuthorize("hasAnyAuthority('reporte.exportar', 'reporte.ventas')")
    public ResponseEntity<byte[]> exportSalesReport(
            Authentication authentication,
            @RequestParam String formato,
            @RequestParam(required = false) LocalDate fechaDesde,
            @RequestParam(required = false) LocalDate fechaHasta,
            @RequestParam(required = false) Long operationalContextId
    ) {
        if (!"xlsx".equalsIgnoreCase(formato)) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "El reporte de ventas solo admite formato xlsx."
            );
        }

        SalesReportResponse report = reportsQueryService.getSalesReport(
                fechaDesde,
                fechaHasta,
                operationalContextId,
                extractPrincipal(authentication).getUsername(),
                ReportFormat.XLSX
        );
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("reporte-ventas.xlsx").build().toString())
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(reportExportService.exportSalesToExcel(report));
    }

    @GetMapping("/caja/exportar")
    @PreAuthorize("hasAnyAuthority('reporte.exportar', 'reporte.caja')")
    public ResponseEntity<byte[]> exportCashReport(
            Authentication authentication,
            @RequestParam String formato,
            @RequestParam(required = false) LocalDate fechaDesde,
            @RequestParam(required = false) LocalDate fechaHasta,
            @RequestParam(required = false) Long operationalContextId
    ) {
        if (!"pdf".equalsIgnoreCase(formato)) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "El reporte de caja solo admite formato pdf."
            );
        }

        CashReportResponse report = reportsQueryService.getCashReport(
                fechaDesde,
                fechaHasta,
                operationalContextId,
                extractPrincipal(authentication).getUsername(),
                ReportFormat.PDF
        );
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("reporte-caja.pdf").build().toString())
                .contentType(MediaType.APPLICATION_PDF)
                .body(reportExportService.exportCashToPdf(report));
    }

    @GetMapping("/historial")
    public ApiResponse<List<ReportHistoryResponse>> getReportHistory(
            @RequestParam(required = false) String reportType,
            @RequestParam(required = false) String generatedBy
    ) {
        return responseFactory.success(
                "Historial de reportes obtenido correctamente.",
                getReportHistoryUseCase.execute(reportType, generatedBy)
        );
    }

    private SecurityUserPrincipal extractPrincipal(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof SecurityUserPrincipal principal)) {
            throw new BusinessException(
                    ErrorCode.AUTH_INVALID_TOKEN,
                    org.springframework.http.HttpStatus.UNAUTHORIZED,
                    "No se pudo recuperar la sesion autenticada."
            );
        }

        return principal;
    }
}
