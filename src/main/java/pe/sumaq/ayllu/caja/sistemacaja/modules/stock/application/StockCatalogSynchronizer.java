package pe.sumaq.ayllu.caja.sistemacaja.modules.stock.application;

import org.springframework.stereotype.Component;

import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.infrastructure.persistence.ProductEntity;

@Component
public class StockCatalogSynchronizer {

    public StockCatalogSynchronizer() {
    }

    public void ensureCurrentStockRow(ProductEntity productEntity) {
        // El stock actual se materializa por contexto operativo cuando ocurre la primera compra/venta.
    }
}
