package desi.tp.entidades;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
public class Asistido extends Persona {
	
	

	private LocalDate fechaRegistro;
	private boolean activo = true;

	@ManyToOne
	@JoinColumn(name = "familia_fk", nullable = false) // Esta es la columna FK en la tabla de Asistido
	private Familia familia;
	

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
	public Familia getFamilia() {
		return familia;
	}

	public void setFamilia(Familia familia) {
		this.familia = familia;
	}
	
	

	
	
}
