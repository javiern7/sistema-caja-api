package pe.sumaq.ayllu.caja.sistemacaja.modules.auditoria.presentation;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pe.sumaq.ayllu.caja.sistemacaja.common.api.ApiResponse;
import pe.sumaq.ayllu.caja.sistemacaja.common.api.ApiResponseFactory;
import pe.sumaq.ayllu.caja.sistemacaja.modules.auditoria.application.ListAuditOperationsUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.auditoria.presentation.dto.AuditOperationResponse;

@RestController
@RequestMapping("/api/v1/auditoria/operaciones")
@PreAuthorize("hasAuthority('auditoria.consultar')")
public class AuditOperationsController {

    private final ListAuditOperationsUseCase listAuditOperationsUseCase;
    private final ApiResponseFactory responseFactory;

    public AuditOperationsController(
            ListAuditOperationsUseCase listAuditOperationsUseCase,
            ApiResponseFactory responseFactory
    ) {
        this.listAuditOperationsUseCase = listAuditOperationsUseCase;
        this.responseFactory = responseFactory;
    }

    @GetMapping
    public ApiResponse<List<AuditOperationResponse>> listAuditOperations(
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String username
    ) {
        return responseFactory.success(
                "Operaciones auditadas obtenidas correctamente.",
                listAuditOperationsUseCase.execute(module, username)
        );
    }
}
