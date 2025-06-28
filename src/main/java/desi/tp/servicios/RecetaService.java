package desi.tp.servicios;

import java.util.List;

import desi.tp.entidades.Receta;
import desi.tp.exepciones.Excepcion;


public interface RecetaService {

	public Receta crearReceta(Receta receta) throws Excepcion;

	public Receta modificarReceta(Integer id, Receta datos);

	public List<Receta> listarRecetas();

	public void eliminarReceta(Integer id);

}
