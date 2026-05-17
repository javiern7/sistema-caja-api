package pe.sumaq.ayllu.caja.sistemacaja.modules.stock.presentation;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.sumaq.ayllu.caja.sistemacaja.common.api.ApiResponse;
import pe.sumaq.ayllu.caja.sistemacaja.common.api.ApiResponseFactory;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.application.ListCurrentStockUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.application.ListStockMovementsUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.presentation.dto.StockCurrentResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.presentation.dto.StockMovementResponse;

@RestController
@RequestMapping("/api/v1/stock")
@PreAuthorize("hasAuthority('stock.consultar')")
public class StockController {

    private final ListCurrentStockUseCase listCurrentStockUseCase;
    private final ListStockMovementsUseCase listStockMovementsUseCase;
    private final ApiResponseFactory responseFactory;

    public StockController(
            ListCurrentStockUseCase listCurrentStockUseCase,
            ListStockMovementsUseCase listStockMovementsUseCase,
            ApiResponseFactory responseFactory
    ) {
        this.listCurrentStockUseCase = listCurrentStockUseCase;
        this.listStockMovementsUseCase = listStockMovementsUseCase;
        this.responseFactory = responseFactory;
    }

    @GetMapping
    public ApiResponse<List<StockCurrentResponse>> listCurrentStock() {
        return responseFactory.success(
                "Stock actual obtenido correctamente.",
                listCurrentStockUseCase.execute()
        );
    }

    @GetMapping("/movimientos")
    public ApiResponse<List<StockMovementResponse>> listStockMovements() {
        return responseFactory.success(
                "Movimientos de stock obtenidos correctamente.",
                listStockMovementsUseCase.execute()
        );
    }
}
