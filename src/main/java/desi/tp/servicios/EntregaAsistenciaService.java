package desi.tp.servicios;

import java.util.List;
import java.time.LocalDate;
import desi.tp.entidades.EntregaAsistencia;
import desi.tp.exepciones.Excepcion;




public interface EntregaAsistenciaService {

	EntregaAsistencia registrarEntrega(Integer idFamilia, Integer idPreparacion, Integer cantidadRaciones, Integer idVoluntario) throws Excepcion;
	
	public EntregaAsistencia crearEntrega(EntregaAsistencia entrega) throws Excepcion;
	
	public EntregaAsistencia modificarEntrega(Integer id, EntregaAsistencia datos);
	
	public List<EntregaAsistencia> listarEntregas();
	
	public void eliminarEntrega(Integer id);
	
	List<EntregaAsistencia> filtrarEntregas(LocalDate fecha, Integer idFamilia, String nombreFamilia);
	
}
