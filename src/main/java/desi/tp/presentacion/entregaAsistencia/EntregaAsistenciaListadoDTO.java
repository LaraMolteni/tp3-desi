package desi.tp.presentacion.entregaAsistencia;

import java.time.LocalDate;

public class EntregaAsistenciaListadoDTO {
    private Integer nroFamilia;
    private String nombreFamilia;
    private LocalDate fechaEntrega;
    private String nombrePlato;
    private Integer cantidadRaciones;

    public Integer getNroFamilia() {
        return nroFamilia;
    }
    public void setNroFamilia(Integer nroFamilia) {
        this.nroFamilia = nroFamilia;
    }
    public String getNombreFamilia() {
        return nombreFamilia;
    }
    public void setNombreFamilia(String nombreFamilia) {
        this.nombreFamilia = nombreFamilia;
    }
    public LocalDate getFechaEntrega() {
        return fechaEntrega;
    }
    public void setFechaEntrega(LocalDate fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }
    public String getNombrePlato() {
        return nombrePlato;
    }
    public void setNombrePlato(String nombrePlato) {
        this.nombrePlato = nombrePlato;
    }
    public Integer getCantidadRaciones() {
        return cantidadRaciones;
    }
    public void setCantidadRaciones(Integer cantidadRaciones) {
        this.cantidadRaciones = cantidadRaciones;
    }
}
