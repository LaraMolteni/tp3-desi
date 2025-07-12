package desi.tp.servicios;

import java.util.List;

import desi.tp.entidades.Familia;
import desi.tp.exepciones.Excepcion;

public interface FamiliaService {

	public Familia crearFamilia(Familia familia) throws Excepcion;

	public Familia modificarFamilia(Integer id, Familia datos);

	public List<Familia> listarFamilias();

	public void eliminarFamilia(Integer id);

	public Familia obtenerFamiliaSiExiste(Integer idFamilia) throws Excepcion;

	// Metodo para contar integrantes activos
	public int contarIntegrantesActivos(Integer idFamilia);

}
