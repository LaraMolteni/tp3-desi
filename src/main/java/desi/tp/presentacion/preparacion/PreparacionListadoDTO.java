package desi.tp.presentacion.preparacion;

import java.time.LocalDate;

public class PreparacionListadoDTO {
    private LocalDate fecha;
    private String nombreReceta;
    private Integer nroRaciones;
    private Integer caloriasPorPlato;

    public LocalDate getFecha() {
        return fecha;
    }
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    public String getNombreReceta() {
        return nombreReceta;
    }
    public void setNombreReceta(String nombreReceta) {
        this.nombreReceta = nombreReceta;
    }
    public Integer getNroRaciones() {
        return nroRaciones;
    }
    public void setNroRaciones(Integer nroRaciones) {
        this.nroRaciones = nroRaciones;
    }
    public Integer getCaloriasPorPlato() {
        return caloriasPorPlato;
    }
    public void setCaloriasPorPlato(Integer caloriasPorPlato) {
        this.caloriasPorPlato = caloriasPorPlato;
    }
}

