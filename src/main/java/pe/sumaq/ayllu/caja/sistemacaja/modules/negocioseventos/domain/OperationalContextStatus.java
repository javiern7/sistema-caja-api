package pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.domain;

public enum OperationalContextStatus {
    PLANIFICADO,
    EN_CURSO,
    CERRADO,
    CANCELADO;

    public boolean isOperable() {
        return this == EN_CURSO;
    }
}
