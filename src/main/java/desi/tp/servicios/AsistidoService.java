package desi.tp.servicios;

import java.util.List;

import desi.tp.entidades.Asistido;
import desi.tp.exepciones.Excepcion;

public interface AsistidoService {

	public void validarDnisFamilia(List<Asistido> asistidos, Asistido nuevo) throws Excepcion;

	public void validarDni(Asistido nuevo, List<Asistido> otrosIntegrantes) throws Excepcion;

	public Asistido buscarPorId(Integer id);

}
