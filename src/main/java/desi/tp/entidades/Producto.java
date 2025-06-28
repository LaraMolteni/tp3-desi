package desi.tp.entidades;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("producto")
public class Producto extends Ingrediente {

	private Integer stockDisponible;
	private Float precioActual;
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
