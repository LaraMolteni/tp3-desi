package desi.tp.servicios;

import java.util.List;

import desi.tp.entidades.Asistido;
import desi.tp.exepciones.Excepcion;

public interface AsistidoService {

	public void validarDni(Asistido nuevo, List<Asistido> yaAgregados) throws Excepcion;
}
