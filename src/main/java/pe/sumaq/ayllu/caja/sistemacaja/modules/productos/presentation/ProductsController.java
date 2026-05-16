package pe.sumaq.ayllu.caja.sistemacaja.modules.productos.presentation;

import java.util.List;

import jakarta.validation.Valid;
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

    private final ListProductsUseCase listProductsUseCase;
    private final CreateProductUseCase createProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final UpdateProductStatusUseCase updateProductStatusUseCase;
    private final ProductMapper productMapper;
    private final ApiResponseFactory responseFactory;

    public ProductsController(
            ListProductsUseCase listProductsUseCase,
            CreateProductUseCase createProductUseCase,
            UpdateProductUseCase updateProductUseCase,
            UpdateProductStatusUseCase updateProductStatusUseCase,
            ProductMapper productMapper,
            ApiResponseFactory responseFactory
    ) {
        this.listProductsUseCase = listProductsUseCase;
        this.createProductUseCase = createProductUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.updateProductStatusUseCase = updateProductStatusUseCase;
        this.productMapper = productMapper;
        this.responseFactory = responseFactory;
    }

    @GetMapping
    public ApiResponse<List<ProductResponse>> listProducts(
            @RequestParam(required = false) Boolean active
    ) {
        return responseFactory.success(
                "Productos obtenidos correctamente.",
                listProductsUseCase.execute(active).stream().map(productMapper::toResponse).toList()
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
