package desi.tp.servicios;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import desi.tp.accesoDatos.AsistidoRepo;
import desi.tp.accesoDatos.FamiliaRepo;
import desi.tp.entidades.Asistido;
import desi.tp.entidades.Familia;
import desi.tp.exepciones.Excepcion;

@Service
public class FamiliaServiceImpl implements FamiliaService {

	@Autowired
	private FamiliaRepo familiaRepo;

	@Autowired
	private AsistidoRepo asistidoRepo;

	@Override
	public Familia crearFamilia(Familia familia) throws Excepcion {
		// Setear fecha de alta y activa por defecto
		familia.setFechaRegistro(LocalDate.now());
		familia.setActivo(true);

		// Validar DNIs duplicados dentro de la familia (memoria)
		Set<Integer> dnisUnicos = new HashSet<>();
		for (Asistido asistido : familia.getAsistidos()) {
			if (!dnisUnicos.add(asistido.getDni())) {
				throw new Excepcion("Ya existe una persona con ese DNI");
			}
		}

		// Validar DNIs existentes en la base
		List<Integer> dnis = familia.getAsistidos().stream().map(Asistido::getDni).toList();
		if (!asistidoRepo.findByDniIn(dnis).isEmpty()) {
			throw new Excepcion("Ya existe una persona con ese DNI");
		}

		return familiaRepo.save(familia);
	}

	@Override
	public Familia modificarFamilia(Integer id, Familia datos) {
		Optional<Familia> opt = familiaRepo.findById(id);
		if (opt.isPresent()) {
			Familia familia = opt.get();
			// No modificar id
			familia.setNombre(datos.getNombre());
			familia.setAsistidos(datos.getAsistidos());
			// No modificar fechaAlta ni id
			return familiaRepo.save(familia);
		}
		return null;
	}

	@Override
	public Familia obtenerFamiliaPorId(Integer id) {
		return familiaRepo.findById(id).orElse(null);
	}

	@Override
	public List<Familia> listarFamilias() {
		// Solo familias activas
		return familiaRepo.findAll().stream().filter(Familia::isActivo).toList();
	}

	@Override
	public void eliminarFamilia(Integer id) {
		Optional<Familia> opt = familiaRepo.findById(id);
		if (opt.isPresent()) {
			Familia familia = opt.get();
			familia.setActivo(false); // Eliminación lógica
			familiaRepo.save(familia);
		}

	}
	
	@Override
	public void contarIntegrantesActivos(Integer idFamilia) {
		Optional <Familia> optFamilia = familiaRepo.findByID(idFamilia);
		if (optFamilia.isEmpty()) {
			return 0;
		}
		Familia familia = optFamilia.get();
		if (familia.getAsistidos() == null) {
			return 0;
		}
		return (int) familia.getAsistidos().stream()
							.filter(Asistido:isActivo) //Filtra solo los asistidos que están activos
							.count(); //Cuenta el número de asistidos activos.
	}
}
