package pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import pe.sumaq.ayllu.caja.sistemacaja.common.pagination.PageResponse;
import pe.sumaq.ayllu.caja.sistemacaja.common.pagination.PageableFactory;
import pe.sumaq.ayllu.caja.sistemacaja.modules.auth.infrastructure.SecurityUserPrincipal;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.application.GetReportHistoryUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.application.ReportExportService;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.application.ReportsQueryService;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.domain.ReportFormat;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.CashReportRowResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.CashReportResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.ExpenseReportRowResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.ExpenseReportResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.PurchaseReportRowResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.PurchaseReportResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.ReportHistoryResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.SalesReportRowResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.SalesReportResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.StockReportRowResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.StockReportResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.UtilityReportResponse;

@RestController
@RequestMapping("/api/v1/reportes")
@Tag(name = "Reportes", description = "Consultas operativas y exportaciones del MVP")
@SecurityRequirement(name = "bearerAuth")
public class ReportsController {

    private static final Set<String> ALLOWED_SALES_REPORT_SORTS = Set.of(
            "id",
            "createdAt",
            "totalAmount",
            "internalReceiptNumber"
    );

    private static final Set<String> ALLOWED_CASH_REPORT_SORTS = Set.of(
            "id",
            "status",
            "openingAmount",
            "expectedAmount",
            "countedAmount",
            "differenceAmount",
            "openedAt",
            "closedAt"
    );

    private static final Set<String> ALLOWED_PURCHASE_REPORT_SORTS = Set.of(
            "id",
            "status",
            "purchaseDate",
            "documentNumber",
            "totalAmount",
            "createdAt"
    );

    private static final Set<String> ALLOWED_EXPENSE_REPORT_SORTS = Set.of(
            "id",
            "expenseDate",
            "expenseType",
            "category",
            "amount",
            "createdAt"
    );

    private static final Set<String> ALLOWED_STOCK_REPORT_SORTS = Set.of(
            "id",
            "code",
            "name",
            "unitOfMeasure",
            "stockControlled",
            "active",
            "minimumStock"
    );

    private static final Set<String> ALLOWED_REPORT_HISTORY_SORTS = Set.of(
            "id",
            "reportType",
            "format",
            "generatedBy",
            "generatedAt"
    );

    private final ReportsQueryService reportsQueryService;
    private final GetReportHistoryUseCase getReportHistoryUseCase;
    private final ReportExportService reportExportService;
    private final PageableFactory pageableFactory;
    private final ApiResponseFactory responseFactory;

    public ReportsController(
            ReportsQueryService reportsQueryService,
            GetReportHistoryUseCase getReportHistoryUseCase,
            ReportExportService reportExportService,
            PageableFactory pageableFactory,
            ApiResponseFactory responseFactory
    ) {
        this.reportsQueryService = reportsQueryService;
        this.getReportHistoryUseCase = getReportHistoryUseCase;
        this.reportExportService = reportExportService;
        this.pageableFactory = pageableFactory;
        this.responseFactory = responseFactory;
    }

    @GetMapping("/ventas")
    @PreAuthorize("hasAnyAuthority('reporte.ver', 'reporte.ventas')")
    @Operation(summary = "Reporte de ventas", description = "Consulta ventas efectivas, excluyendo anuladas por defecto.")
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

    @GetMapping("/ventas/detalle")
    @PreAuthorize("hasAnyAuthority('reporte.ver', 'reporte.ventas')")
    @Operation(summary = "Detalle paginado de ventas", description = "Consulta filas del reporte de ventas con paginacion server-side.")
    public ApiResponse<PageResponse<SalesReportRowResponse>> getSalesReportPage(
            @RequestParam(required = false) LocalDate fechaDesde,
            @RequestParam(required = false) LocalDate fechaHasta,
            @RequestParam(required = false) Long operationalContextId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) List<String> sort
    ) {
        Pageable pageable = pageableFactory.create(
                page,
                size,
                sort,
                Sort.by(Sort.Direction.DESC, "createdAt"),
                ALLOWED_SALES_REPORT_SORTS
        );

        return responseFactory.success(
                "Detalle paginado de ventas obtenido correctamente.",
                PageResponse.from(reportsQueryService.getSalesReportPage(fechaDesde, fechaHasta, operationalContextId, pageable))
        );
    }

    @GetMapping("/caja")
    @PreAuthorize("hasAnyAuthority('reporte.ver', 'reporte.caja')")
    @Operation(summary = "Reporte de caja", description = "Consulta aperturas, cierres, montos esperados y diferencias.")
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

    @GetMapping("/caja/detalle")
    @PreAuthorize("hasAnyAuthority('reporte.ver', 'reporte.caja')")
    @Operation(summary = "Detalle paginado de caja", description = "Consulta filas del reporte de caja con paginacion server-side.")
    public ApiResponse<PageResponse<CashReportRowResponse>> getCashReportPage(
            @RequestParam(required = false) LocalDate fechaDesde,
            @RequestParam(required = false) LocalDate fechaHasta,
            @RequestParam(required = false) Long operationalContextId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) List<String> sort
    ) {
        Pageable pageable = pageableFactory.create(
                page,
                size,
                sort,
                Sort.by(Sort.Direction.DESC, "openedAt"),
                ALLOWED_CASH_REPORT_SORTS
        );

        return responseFactory.success(
                "Detalle paginado de caja obtenido correctamente.",
                PageResponse.from(reportsQueryService.getCashReportPage(fechaDesde, fechaHasta, operationalContextId, pageable))
        );
    }

    @GetMapping("/compras")
    @PreAuthorize("hasAnyAuthority('reporte.ver', 'reporte.compras')")
    @Operation(summary = "Reporte de compras", description = "Consulta compras operativas excluyendo anuladas totales por defecto.")
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

    @GetMapping("/compras/detalle")
    @PreAuthorize("hasAnyAuthority('reporte.ver', 'reporte.compras')")
    @Operation(summary = "Detalle paginado de compras", description = "Consulta filas del reporte de compras con paginacion server-side.")
    public ApiResponse<PageResponse<PurchaseReportRowResponse>> getPurchasesReportPage(
            @RequestParam(required = false) LocalDate fechaDesde,
            @RequestParam(required = false) LocalDate fechaHasta,
            @RequestParam(required = false) Long operationalContextId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) List<String> sort
    ) {
        Pageable pageable = pageableFactory.create(
                page,
                size,
                sort,
                Sort.by(Sort.Direction.DESC, "purchaseDate").and(Sort.by(Sort.Direction.DESC, "id")),
                ALLOWED_PURCHASE_REPORT_SORTS
        );

        return responseFactory.success(
                "Detalle paginado de compras obtenido correctamente.",
                PageResponse.from(reportsQueryService.getPurchasesReportPage(fechaDesde, fechaHasta, operationalContextId, pageable))
        );
    }

    @GetMapping("/egresos")
    @PreAuthorize("hasAnyAuthority('reporte.ver', 'reporte.egresos')")
    @Operation(summary = "Reporte de egresos", description = "Consulta egresos administrativos y egresos de caja.")
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

    @GetMapping("/egresos/detalle")
    @PreAuthorize("hasAnyAuthority('reporte.ver', 'reporte.egresos')")
    @Operation(summary = "Detalle paginado de egresos", description = "Consulta filas del reporte de egresos con paginacion server-side.")
    public ApiResponse<PageResponse<ExpenseReportRowResponse>> getExpensesReportPage(
            @RequestParam(required = false) LocalDate fechaDesde,
            @RequestParam(required = false) LocalDate fechaHasta,
            @RequestParam(required = false) Long operationalContextId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) List<String> sort
    ) {
        Pageable pageable = pageableFactory.create(
                page,
                size,
                sort,
                Sort.by(Sort.Direction.DESC, "expenseDate").and(Sort.by(Sort.Direction.DESC, "id")),
                ALLOWED_EXPENSE_REPORT_SORTS
        );

        return responseFactory.success(
                "Detalle paginado de egresos obtenido correctamente.",
                PageResponse.from(reportsQueryService.getExpensesReportPage(fechaDesde, fechaHasta, operationalContextId, pageable))
        );
    }

    @GetMapping("/stock")
    @PreAuthorize("hasAnyAuthority('reporte.ver', 'reporte.stock', 'stock.consultar')")
    @Operation(summary = "Reporte de stock", description = "Consulta stock actual del contexto operativo solicitado.")
    public ApiResponse<StockReportResponse> getStockReport(
            Authentication authentication,
            @RequestParam(required = false) LocalDate fechaDesde,
            @RequestParam(required = false) LocalDate fechaHasta,
            @RequestParam Long operationalContextId
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

    @GetMapping("/stock/detalle")
    @PreAuthorize("hasAnyAuthority('reporte.ver', 'reporte.stock', 'stock.consultar')")
    @Operation(summary = "Detalle paginado de stock", description = "Consulta filas del reporte de stock del contexto operativo solicitado con paginacion server-side.")
    public ApiResponse<PageResponse<StockReportRowResponse>> getStockReportPage(
            @RequestParam Long operationalContextId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) List<String> sort
    ) {
        Pageable pageable = pageableFactory.create(
                page,
                size,
                sort,
                Sort.by(Sort.Direction.ASC, "name"),
                ALLOWED_STOCK_REPORT_SORTS
        );

        return responseFactory.success(
                "Detalle paginado de stock obtenido correctamente.",
                PageResponse.from(reportsQueryService.getStockReportPage(operationalContextId, pageable))
        );
    }

    @GetMapping("/utilidad")
    @PreAuthorize("hasAnyAuthority('reporte.ver', 'reporte.utilidad')")
    @Operation(summary = "Reporte de utilidad", description = "Calcula utilidad operativa estimada en base a ventas, costos de referencia y egresos.")
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
    @Operation(summary = "Exportar reporte de ventas", description = "Exporta el reporte de ventas en formato xlsx.")
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
                    "El reporte de ventas solo admite formato xlsx.",
                    List.of(
                            "allowedFormat=xlsx",
                            "requestedFormat=" + formato
                    )
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
    @Operation(summary = "Exportar reporte de caja", description = "Exporta el reporte de caja en formato pdf.")
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
                    "El reporte de caja solo admite formato pdf.",
                    List.of(
                            "allowedFormat=pdf",
                            "requestedFormat=" + formato
                    )
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
    @PreAuthorize("hasAnyAuthority('reporte.ver', 'reporte.exportar')")
    @Operation(summary = "Historial de reportes", description = "Consulta trazabilidad de reportes generados por usuario, tipo y filtros.")
    public ApiResponse<PageResponse<ReportHistoryResponse>> getReportHistory(
            @RequestParam(required = false) String reportType,
            @RequestParam(required = false) String generatedBy,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) List<String> sort
    ) {
        Pageable pageable = pageableFactory.create(
                page,
                size,
                sort,
                Sort.by(Sort.Direction.DESC, "generatedAt"),
                ALLOWED_REPORT_HISTORY_SORTS
        );

        return responseFactory.success(
                "Historial de reportes obtenido correctamente.",
                PageResponse.from(getReportHistoryUseCase.execute(reportType, generatedBy, pageable))
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
