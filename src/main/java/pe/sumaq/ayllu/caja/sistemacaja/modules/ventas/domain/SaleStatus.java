package pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.domain;

public enum SaleStatus {
    REGISTRADA,
    ANULADA;

    public boolean isRegistered() {
        return this == REGISTRADA;
    }
}
