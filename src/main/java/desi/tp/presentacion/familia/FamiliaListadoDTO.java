package desi.tp.presentacion.familia;

import java.time.LocalDate;

import desi.tp.entidades.Asistido;
import desi.tp.entidades.Familia;
import desi.tp.servicios.FamiliaService;

public class FamiliaListadoDTO {
	
    private Integer nroFamilia;
    private String nombreFamilia;
    private LocalDate fechaAlta;
    private LocalDate fechaUltimaAsistencia;
    private Integer nroIntegrantes;

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
    public LocalDate getFechaAlta() {
        return fechaAlta;
    }
    public void setFechaAlta(LocalDate fechaAlta) {
        this.fechaAlta = fechaAlta;
    }
    public LocalDate getFechaUltimaAsistencia() {
        return fechaUltimaAsistencia;
    }
    public void setFechaUltimaAsistencia(LocalDate fechaUltimaAsistencia) {
        this.fechaUltimaAsistencia = fechaUltimaAsistencia;
    }
    public Integer getNroIntegrantes() {
        return nroIntegrantes;
    }
    public void setNroIntegrantes(Integer nroIntegrantes) {
        this.nroIntegrantes = nroIntegrantes;
    }
    
    public static FamiliaListadoDTO from(Familia f, FamiliaService familiaService) {
    	FamiliaListadoDTO dto = new FamiliaListadoDTO();
    	dto.setNroFamilia(f.getIdFamilia());
    	dto.setNombreFamilia(f.getNombre());
    	dto.setFechaAlta(f.getFechaRegistro());
    	dto.setFechaUltimaAsistencia(familiaService.obtenerUltimaAsistenciaDeFamilia(f.getIdFamilia()));
    	dto.setNroIntegrantes((int) f.getAsistidos().stream().filter(Asistido::isActivo).count());
    	return dto;
    }

}

