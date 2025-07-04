package desi.tp.servicios;

import desi.tp.entidades.Preparacion;

import java.time.LocalDate;
import java.util.List;

public interface PreparacionService {

    List<Preparacion> listarPreparacionesActivas();

    Preparacion buscarPorId(Integer id);

    void crearPreparacion(Preparacion preparacion);

    void eliminarPreparacion(Integer id);

    void modificarFecha(Integer id, LocalDate nuevaFecha);

	List<Preparacion> filtrar(String nombreReceta, LocalDate fecha);
	
	//Metodo para guardar y actualizar las preparaciones disponibles:
	
	Preparacion guardar(Preparacion preparacion);
	
}