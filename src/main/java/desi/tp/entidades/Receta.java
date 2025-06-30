package desi.tp.entidades;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Size;


@Entity
public class Receta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String nombre;
	private String descripcion;
	private boolean activa = true;
	
	@OneToMany(mappedBy = "receta")
	private List<Preparacion> preparaciones;
	
	@OneToMany(mappedBy="receta", cascade = CascadeType.ALL, orphanRemoval = true)
	@Size(min =1) //Validacion para asegurar al menos un elemento
	private List<ItemReceta> itemsReceta;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public List<Preparacion> getPreparaciones() {
		return preparaciones;
	}

	public void setPreparaciones(List<Preparacion> preparaciones) {
		this.preparaciones = preparaciones;
	}

	public List<ItemReceta> getItemsReceta() {
		return itemsReceta;
	}

	public void setItemsReceta(List<ItemReceta> itemsReceta) {
		this.itemsReceta = itemsReceta;
	}

	public boolean isActiva() {
		return activa;
	}

	public void setActiva(boolean activa) {
		this.activa = activa;
	}

	public int getCaloriasTotales() {
		if (itemsReceta == null) return 0;
		return itemsReceta.stream()
			.filter(i -> i != null && i.getCalorias() != null)
			.mapToInt(ItemReceta::getCalorias)
			.sum();
	}
	
	
	
}
