package pe.sumaq.ayllu.caja.sistemacaja.modules.stock.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.IdClass;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.springframework.data.domain.Persistable;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.infrastructure.persistence.OperationalContextEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.infrastructure.persistence.ProductEntity;

@Entity
@Table(name = "stock_current")
@IdClass(StockCurrentId.class)
public class StockCurrentEntity implements Persistable<StockCurrentId> {

    @Id
    @Column(name = "operational_context_id")
    private Long operationalContextId;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "operational_context_id", insertable = false, updatable = false)
    private OperationalContextEntity operationalContext;

    @Id
    @Column(name = "product_id")
    private Long productId;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private ProductEntity product;

    @Column(name = "current_stock", nullable = false, precision = 12, scale = 2)
    private BigDecimal currentStock;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Transient
    private boolean isNew = true;

    public Long getOperationalContextId() {
        return operationalContextId;
    }

    public void setOperationalContextId(Long operationalContextId) {
        this.operationalContextId = operationalContextId;
    }

    public OperationalContextEntity getOperationalContext() {
        return operationalContext;
    }

    public void setOperationalContext(OperationalContextEntity operationalContext) {
        this.operationalContext = operationalContext;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    @Override
    public StockCurrentId getId() {
        return new StockCurrentId(operationalContextId, productId);
    }

    public ProductEntity getProduct() {
        return product;
    }

    public void setProduct(ProductEntity product) {
        this.product = product;
    }

    public BigDecimal getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(BigDecimal currentStock) {
        this.currentStock = currentStock;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PostPersist
    @PostLoad
    void markNotNew() {
        this.isNew = false;
    }
}
