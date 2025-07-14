package desi.tp.servicios;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import desi.tp.accesoDatos.AsistidoRepo;
import desi.tp.entidades.Asistido;
import desi.tp.exepciones.Excepcion;

@Service
public class AsistidoServiceImpl implements AsistidoService {

	@Autowired
	AsistidoRepo asistidoRepo;

	// Validar asistido al crear (nuevo, sin ID)

	@Override
	public void validarDnisFamilia(List<Asistido> asistidos, Asistido nuevo) throws Excepcion {
		// Verificar si el DNI ya está en la lista actual de integrantes
		boolean dniRepetidoEnLista = asistidos.stream().anyMatch(a -> a.getDni().equals(nuevo.getDni()));

		if (dniRepetidoEnLista) {
			throw new Excepcion("dni", "Ya existe una persona con ese DNI en esta familia.");
		}

		// Verificar si el DNI ya está en la base de datos
		List<Long> dniBuscado = new ArrayList<>();
		dniBuscado.add(nuevo.getDni());

		List<Asistido> encontrados = asistidoRepo.findByDniIn(dniBuscado);

		if (!encontrados.isEmpty()) {
			throw new Excepcion("dni", "Ya existe una persona con ese DNI registrado.");
		}
	}

	// Validar asistido al editar (compara por ID)
	@Override
	public void validarDni(Asistido nuevo, List<Asistido> otrosIntegrantes) throws Excepcion {
		boolean duplicadoEnLista = otrosIntegrantes.stream().filter(a -> !Objects.equals(a.getId(), nuevo.getId()))
				.anyMatch(a -> a.getDni().equals(nuevo.getDni()));

		if (duplicadoEnLista) {
			throw new Excepcion("dni", "Ya existe una persona con ese DNI en esta familia.");
		}

		List<Asistido> existentes = asistidoRepo.findByDniIn(List.of(nuevo.getDni()));
		boolean yaRegistrado = existentes.stream().anyMatch(a -> !Objects.equals(a.getId(), nuevo.getId()));

		if (yaRegistrado) {
			throw new Excepcion("dni", "Ya existe una persona con ese DNI registrado.");
		}
	}

	@Override
	public Asistido buscarPorId(Integer id) {
		return asistidoRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Asistido no encontrado con ID: " + id));
	}

}
