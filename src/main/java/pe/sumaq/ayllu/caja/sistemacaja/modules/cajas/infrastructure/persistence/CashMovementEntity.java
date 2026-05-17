package pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
import jakarta.persistence.Table;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.domain.CashMovementType;

@Entity
@Table(name = "cash_movements")
public class CashMovementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "cash_box_id", nullable = false)
    private CashBoxEntity cashBox;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 30)
    private CashMovementType movementType;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "reference_type", nullable = false, length = 50)
    private String referenceType;

    @Column(name = "reference_id", length = 100)
    private String referenceId;

    @Column(name = "performed_by", nullable = false, length = 100)
    private String performedBy;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    @Column(length = 255)
    private String observation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CashBoxEntity getCashBox() {
        return cashBox;
    }

    public void setCashBox(CashBoxEntity cashBox) {
        this.cashBox = cashBox;
    }

    public CashMovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(CashMovementType movementType) {
        this.movementType = movementType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }
}
