package desi.tp.entidades;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Size;

@Entity
public class Familia {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idFamilia;
	private String nombre;
	private LocalDate fechaRegistro;
	private boolean activo = true;
	
	
	
	@OneToMany(mappedBy = "familia", cascade = CascadeType.ALL)	
	@Size(min =1)
	private List<Asistido> asistidos = new ArrayList<>();


	public Integer getIdFamilia() {
		return idFamilia;
	}


	public void setIdFamilia(Integer idFamilia) {
		this.idFamilia = idFamilia;
	}


	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public LocalDate getFechaRegistro() {
		return fechaRegistro;
	}


	public void setFechaRegistro(LocalDate fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}


	public List<Asistido> getAsistidos() {
		return asistidos;
	}


	public void setAsistidos(List<Asistido> asistidos) {
		this.asistidos = asistidos;
	}
	
	public boolean isActivo() {
		return activo;
	}


	public void setActivo(boolean activo) {
		this.activo = activo;
	}

}
