package pe.sumaq.ayllu.caja.sistemacaja.modules.stock.infrastructure.persistence;

import java.io.Serializable;
import java.util.Objects;

public class StockCurrentId implements Serializable {

    private Long operationalContextId;
    private Long productId;

    public StockCurrentId() {
    }

    public StockCurrentId(Long operationalContextId, Long productId) {
        this.operationalContextId = operationalContextId;
        this.productId = productId;
    }

    public Long getOperationalContextId() {
        return operationalContextId;
    }

    public void setOperationalContextId(Long operationalContextId) {
        this.operationalContextId = operationalContextId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof StockCurrentId that)) {
            return false;
        }
        return Objects.equals(operationalContextId, that.operationalContextId)
                && Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operationalContextId, productId);
    }
}
