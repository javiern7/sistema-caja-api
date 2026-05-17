package pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence.CashBoxEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.infrastructure.persistence.OperationalContextEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.infrastructure.persistence.UserEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.domain.SaleStatus;

@Entity
@Table(name = "sales")
public class SaleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "operational_context_id", nullable = false)
    private OperationalContextEntity operationalContext;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "cash_box_id", nullable = false)
    private CashBoxEntity cashBox;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "sold_by_user_id", nullable = false)
    private UserEntity soldBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SaleStatus status;

    @Column(name = "subtotal_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotalAmount;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(length = 255)
    private String observation;

    @Column(name = "internal_receipt_series", nullable = false, length = 20)
    private String internalReceiptSeries;

    @Column(name = "internal_receipt_number", nullable = false)
    private Long internalReceiptNumber;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cancelled_by_user_id")
    private UserEntity cancelledBy;

    @Column(name = "cancellation_reason", length = 255)
    private String cancellationReason;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleItemEntity> items = new ArrayList<>();

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SalePaymentEntity> payments = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OperationalContextEntity getOperationalContext() {
        return operationalContext;
    }

    public void setOperationalContext(OperationalContextEntity operationalContext) {
        this.operationalContext = operationalContext;
    }

    public CashBoxEntity getCashBox() {
        return cashBox;
    }

    public void setCashBox(CashBoxEntity cashBox) {
        this.cashBox = cashBox;
    }

    public UserEntity getSoldBy() {
        return soldBy;
    }

    public void setSoldBy(UserEntity soldBy) {
        this.soldBy = soldBy;
    }

    public SaleStatus getStatus() {
        return status;
    }

    public void setStatus(SaleStatus status) {
        this.status = status;
    }

    public BigDecimal getSubtotalAmount() {
        return subtotalAmount;
    }

    public void setSubtotalAmount(BigDecimal subtotalAmount) {
        this.subtotalAmount = subtotalAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public String getInternalReceiptSeries() {
        return internalReceiptSeries;
    }

    public void setInternalReceiptSeries(String internalReceiptSeries) {
        this.internalReceiptSeries = internalReceiptSeries;
    }

    public Long getInternalReceiptNumber() {
        return internalReceiptNumber;
    }

    public void setInternalReceiptNumber(Long internalReceiptNumber) {
        this.internalReceiptNumber = internalReceiptNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public UserEntity getCancelledBy() {
        return cancelledBy;
    }

    public void setCancelledBy(UserEntity cancelledBy) {
        this.cancelledBy = cancelledBy;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public List<SaleItemEntity> getItems() {
        return items;
    }

    public void setItems(List<SaleItemEntity> items) {
        this.items = items;
    }

    public List<SalePaymentEntity> getPayments() {
        return payments;
    }

    public void setPayments(List<SalePaymentEntity> payments) {
        this.payments = payments;
    }
}
