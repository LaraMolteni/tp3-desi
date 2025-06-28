package desi.tp.entidades;



import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;


@Entity
public class EntregaAsistencia {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private LocalDate fecha;
	private Integer cantidadRaciones;
	private boolean activo = true;
	
	@ManyToOne()
	@JoinColumn(name = "voluntario_fk", nullable = false)
	private Voluntario voluntario;
	
	@ManyToOne()
	@JoinColumn(name="familia_fk", nullable = false)
	private Familia familia;
	
	@ManyToOne()
	@JoinColumn(name="preparacion_fk", nullable=false)
	private Preparacion preparacion;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public Integer getCantidadRaciones() {
		return cantidadRaciones;
	}

	public void setCantidadRaciones(Integer cantidadRaciones) {
		this.cantidadRaciones = cantidadRaciones;
	}

	public Voluntario getVoluntario() {
		return voluntario;
	}

	public void setVoluntario(Voluntario voluntario) {
		this.voluntario = voluntario;
	}

	public Familia getFamilia() {
		return familia;
	}

	public void setFamilia(Familia familia) {
		this.familia = familia;
	}

	public Preparacion getPreparacion() {
		return preparacion;
	}

	public void setPreparacion(Preparacion preparacion) {
		this.preparacion = preparacion;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}
	
	
	
}
