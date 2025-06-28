package desi.tp.entidades;
import java.time.LocalDate;

import jakarta.persistence.Entity;

@Entity
public class Asistido extends Persona {

	private LocalDate fechaRegistro;
	private boolean activo = true;


	public LocalDate getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(LocalDate fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}
	
	

	
	
}
