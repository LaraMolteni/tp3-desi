package desi.tp.presentacion.entregaAsistencia;



public class RegistrarEntregaRequestDTO {
    private Integer idFamilia;
    private Integer idPreparacion;
    private Integer cantidadRaciones;
    private Integer idVoluntario; 

    // Getters y Setters
    public Integer getIdFamilia() {
        return idFamilia;
    }

    public void setIdFamilia(Integer idFamilia) {
        this.idFamilia = idFamilia;
    }

    public Integer getIdPreparacion() {
        return idPreparacion;
    }

    public void setIdPreparacion(Integer idPreparacion) {
        this.idPreparacion = idPreparacion;
    }

    public Integer getCantidadRaciones() {
        return cantidadRaciones;
    }

    public void setCantidadRaciones(Integer cantidadRaciones) {
        this.cantidadRaciones = cantidadRaciones;
    }

    public Integer getIdVoluntario() {
        return idVoluntario;
    }

    public void setIdVoluntario(Integer idVoluntario) {
        this.idVoluntario = idVoluntario;
    }
}