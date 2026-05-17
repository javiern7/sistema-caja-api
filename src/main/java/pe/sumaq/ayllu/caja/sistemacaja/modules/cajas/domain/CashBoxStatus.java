package pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.domain;

public enum CashBoxStatus {
    ABIERTA,
    CERRADA;

    public boolean isOpen() {
        return this == ABIERTA;
    }
}
