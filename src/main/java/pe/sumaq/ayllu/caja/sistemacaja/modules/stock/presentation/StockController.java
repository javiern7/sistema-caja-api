package pe.sumaq.ayllu.caja.sistemacaja.modules.stock.presentation;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pe.sumaq.ayllu.caja.sistemacaja.common.api.ApiResponse;
import pe.sumaq.ayllu.caja.sistemacaja.common.api.ApiResponseFactory;
import pe.sumaq.ayllu.caja.sistemacaja.common.pagination.PageResponse;
import pe.sumaq.ayllu.caja.sistemacaja.common.pagination.PageableFactory;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.application.ListCurrentStockUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.application.ListStockMovementsUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.presentation.dto.StockCurrentResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.presentation.dto.StockMovementResponse;

@RestController
@RequestMapping("/api/v1/stock")
@PreAuthorize("hasAuthority('stock.consultar')")
public class StockController {

    private static final Set<String> ALLOWED_CURRENT_STOCK_SORTS = Set.of(
            "id",
            "code",
            "name",
            "unitOfMeasure",
            "stockControlled",
            "active",
            "minimumStock"
    );

    private static final Set<String> ALLOWED_MOVEMENT_SORTS = Set.of(
            "id",
            "productId",
            "movementType",
            "quantity",
            "occurredAt"
    );

    private final ListCurrentStockUseCase listCurrentStockUseCase;
    private final ListStockMovementsUseCase listStockMovementsUseCase;
    private final PageableFactory pageableFactory;
    private final ApiResponseFactory responseFactory;

    public StockController(
            ListCurrentStockUseCase listCurrentStockUseCase,
            ListStockMovementsUseCase listStockMovementsUseCase,
            PageableFactory pageableFactory,
            ApiResponseFactory responseFactory
    ) {
        this.listCurrentStockUseCase = listCurrentStockUseCase;
        this.listStockMovementsUseCase = listStockMovementsUseCase;
        this.pageableFactory = pageableFactory;
        this.responseFactory = responseFactory;
    }

    @GetMapping
    public ApiResponse<PageResponse<StockCurrentResponse>> listCurrentStock(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) List<String> sort
    ) {
        Pageable pageable = pageableFactory.create(
                page,
                size,
                sort,
                Sort.by(Sort.Direction.ASC, "name"),
                ALLOWED_CURRENT_STOCK_SORTS
        );

        return responseFactory.success(
                "Stock actual obtenido correctamente.",
                PageResponse.from(listCurrentStockUseCase.execute(active, pageable))
        );
    }

    @GetMapping("/movimientos")
    public ApiResponse<PageResponse<StockMovementResponse>> listStockMovements(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) List<String> sort
    ) {
        Pageable pageable = pageableFactory.create(
                page,
                size,
                sort,
                Sort.by(Sort.Direction.DESC, "occurredAt"),
                ALLOWED_MOVEMENT_SORTS
        );

        return responseFactory.success(
                "Movimientos de stock obtenidos correctamente.",
                PageResponse.from(listStockMovementsUseCase.execute(pageable))
        );
    }
}
