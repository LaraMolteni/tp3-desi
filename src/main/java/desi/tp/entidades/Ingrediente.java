package desi.tp.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Ingrediente {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String nombre;
	private Integer stockDisponible;
	private Float precioActual;
	@Column(columnDefinition = "TINYINT(1)")
	private boolean activo = true;

	// NOTA: al manejar las calorias desde el ItemReceta, no es necesario tener este
	// campo acá. De todas formas se podría hacer algún alta con caloria por kg, y
	// desp. hacer calculo automático en la receta?
	public Integer getId() {
		return id;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
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

	public Integer getStockDisponible() {
		return stockDisponible;
	}

	public void setStockDisponible(Integer stockDisponible) {
		this.stockDisponible = stockDisponible;
	}

	public Float getPrecioActual() {
		return precioActual;
	}

	public void setPrecioActual(Float precioActual) {
		this.precioActual = precioActual;
	}
}
