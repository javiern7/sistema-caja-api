package pe.sumaq.ayllu.caja.sistemacaja.modules.productos.presentation;

import java.util.List;
import java.util.Set;

import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pe.sumaq.ayllu.caja.sistemacaja.common.api.ApiResponse;
import pe.sumaq.ayllu.caja.sistemacaja.common.api.ApiResponseFactory;
import pe.sumaq.ayllu.caja.sistemacaja.common.pagination.PageResponse;
import pe.sumaq.ayllu.caja.sistemacaja.common.pagination.PageableFactory;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.application.CreateProductUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.application.ListProductsUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.application.ProductMapper;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.application.UpdateProductStatusUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.application.UpdateProductUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.presentation.dto.CreateProductRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.presentation.dto.ProductResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.presentation.dto.UpdateProductRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.presentation.dto.UpdateProductStatusRequest;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductsController {

    private static final Set<String> ALLOWED_SORTS = Set.of(
            "id",
            "code",
            "name",
            "unitOfMeasure",
            "salePrice",
            "referenceCost",
            "minimumStock",
            "active"
    );

    private final ListProductsUseCase listProductsUseCase;
    private final CreateProductUseCase createProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final UpdateProductStatusUseCase updateProductStatusUseCase;
    private final ProductMapper productMapper;
    private final PageableFactory pageableFactory;
    private final ApiResponseFactory responseFactory;

    public ProductsController(
            ListProductsUseCase listProductsUseCase,
            CreateProductUseCase createProductUseCase,
            UpdateProductUseCase updateProductUseCase,
            UpdateProductStatusUseCase updateProductStatusUseCase,
            ProductMapper productMapper,
            PageableFactory pageableFactory,
            ApiResponseFactory responseFactory
    ) {
        this.listProductsUseCase = listProductsUseCase;
        this.createProductUseCase = createProductUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.updateProductStatusUseCase = updateProductStatusUseCase;
        this.productMapper = productMapper;
        this.pageableFactory = pageableFactory;
        this.responseFactory = responseFactory;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('producto.gestionar', 'venta.registrar', 'compra.registrar', 'stock.consultar')")
    public ApiResponse<PageResponse<ProductResponse>> listProducts(
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
                ALLOWED_SORTS
        );

        return responseFactory.success(
                "Productos obtenidos correctamente.",
                PageResponse.from(listProductsUseCase.execute(active, pageable).map(productMapper::toResponse))
        );
    }

    @PostMapping
    @PreAuthorize("hasAuthority('producto.gestionar')")
    public ApiResponse<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        return responseFactory.success(
                "Producto registrado correctamente.",
                productMapper.toResponse(createProductUseCase.execute(request))
        );
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasAuthority('producto.gestionar')")
    public ApiResponse<ProductResponse> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        return responseFactory.success(
                "Producto actualizado correctamente.",
                productMapper.toResponse(updateProductUseCase.execute(productId, request))
        );
    }

    @PatchMapping("/{productId}/estado")
    @PreAuthorize("hasAuthority('producto.gestionar')")
    public ApiResponse<ProductResponse> updateProductStatus(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateProductStatusRequest request
    ) {
        return responseFactory.success(
                "Estado de producto actualizado correctamente.",
                productMapper.toResponse(updateProductStatusUseCase.execute(productId, request.active()))
        );
    }
}
