package desi.tp.servicios;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import desi.tp.accesoDatos.AsistidoRepo;
import desi.tp.entidades.Asistido;
import desi.tp.exepciones.Excepcion;

@Service
public class AsistidoServiceImpl implements AsistidoService {

	@Autowired
	AsistidoRepo asistidoRepo;

	@Override
	public void validarDni(Asistido nuevo, List<Asistido> yaAgregados) throws Excepcion {
		if (yaAgregados.stream().anyMatch(a -> a.getDni().equals(nuevo.getDni()))) {
			throw new Excepcion("dni", "Ya existe una persona con ese DNI en esta familia.");
		}
		if (!asistidoRepo.findByDniIn(List.of(nuevo.getDni())).isEmpty())  {
			throw new Excepcion("dni", "Ya existe una persona con ese DNI registrado.");
		}
		
	}
}
