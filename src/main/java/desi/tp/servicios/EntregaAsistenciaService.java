package desi.tp.servicios;

import java.util.List;

import desi.tp.entidades.EntregaAsistencia;
import desi.tp.exepciones.Excepcion;




public interface EntregaAsistenciaService {

	
	public EntregaAsistencia crearEntrega(EntregaAsistencia entrega) throws Excepcion;
	
	public EntregaAsistencia modificarEntrega(Integer id, EntregaAsistencia datos);
	
	public List<EntregaAsistencia> listarEntregas();
	
	public void eliminarEntrega(Integer id);
	
}
