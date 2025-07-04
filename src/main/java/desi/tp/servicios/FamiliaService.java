package desi.tp.servicios;

import java.util.List;

import desi.tp.entidades.Familia;
import desi.tp.exepciones.Excepcion;

import java.util.Optional;

public interface FamiliaService {

	Optional<Familia> findById(Integer id);
	
	//Metodo para contar integrantes activos
	public int contarIntegrantesActivos(Integer idFamilia);
	
	public Familia crearFamilia(Familia familia) throws Excepcion;
	
	public Familia modificarFamilia(Integer id, Familia datos);
	
	public List<Familia> listarFamilias();
	
	public void eliminarFamilia(Integer id);
	
	public Familia obtenerFamiliaPorId(Integer id);
	
	
}
