package desi.tp.servicios;

import java.util.List;

import desi.tp.entidades.Preparacion;
import desi.tp.exepciones.Excepcion;

public interface PreparacionService {

	public Preparacion crearPreparacion(Preparacion preparacion) throws Excepcion;

	public Preparacion modificarPreparcion(Integer id, Preparacion datos);

	public List<Preparacion> listarPreparaciones();

	public void eliminarPreparacion(Integer id);

}
