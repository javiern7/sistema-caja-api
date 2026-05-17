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
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.domain.CashBoxStatus;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.infrastructure.persistence.OperationalContextEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.infrastructure.persistence.UserEntity;

@Entity
@Table(name = "cash_boxes")
public class CashBoxEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "operational_context_id", nullable = false)
    private OperationalContextEntity operationalContext;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "opened_by_user_id", nullable = false)
    private UserEntity openedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CashBoxStatus status;

    @Column(name = "opening_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal openingAmount;

    @Column(name = "opening_observation", length = 255)
    private String openingObservation;

    @Column(name = "opened_at", nullable = false)
    private LocalDateTime openedAt;

    @Column(name = "expected_amount", precision = 12, scale = 2)
    private BigDecimal expectedAmount;

    @Column(name = "counted_amount", precision = 12, scale = 2)
    private BigDecimal countedAmount;

    @Column(name = "difference_amount", precision = 12, scale = 2)
    private BigDecimal differenceAmount;

    @Column(name = "closing_observation", length = 255)
    private String closingObservation;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "closed_by_user_id")
    private UserEntity closedBy;

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

    public UserEntity getOpenedBy() {
        return openedBy;
    }

    public void setOpenedBy(UserEntity openedBy) {
        this.openedBy = openedBy;
    }

    public CashBoxStatus getStatus() {
        return status;
    }

    public void setStatus(CashBoxStatus status) {
        this.status = status;
    }

    public BigDecimal getOpeningAmount() {
        return openingAmount;
    }

    public void setOpeningAmount(BigDecimal openingAmount) {
        this.openingAmount = openingAmount;
    }

    public String getOpeningObservation() {
        return openingObservation;
    }

    public void setOpeningObservation(String openingObservation) {
        this.openingObservation = openingObservation;
    }

    public LocalDateTime getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(LocalDateTime openedAt) {
        this.openedAt = openedAt;
    }

    public BigDecimal getExpectedAmount() {
        return expectedAmount;
    }

    public void setExpectedAmount(BigDecimal expectedAmount) {
        this.expectedAmount = expectedAmount;
    }

    public BigDecimal getCountedAmount() {
        return countedAmount;
    }

    public void setCountedAmount(BigDecimal countedAmount) {
        this.countedAmount = countedAmount;
    }

    public BigDecimal getDifferenceAmount() {
        return differenceAmount;
    }

    public void setDifferenceAmount(BigDecimal differenceAmount) {
        this.differenceAmount = differenceAmount;
    }

    public String getClosingObservation() {
        return closingObservation;
    }

    public void setClosingObservation(String closingObservation) {
        this.closingObservation = closingObservation;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public UserEntity getClosedBy() {
        return closedBy;
    }

    public void setClosedBy(UserEntity closedBy) {
        this.closedBy = closedBy;
    }
}
