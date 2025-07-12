package desi.tp.servicios;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import desi.tp.accesoDatos.FamiliaRepo;
import desi.tp.entidades.Asistido;
import desi.tp.entidades.Familia;
import desi.tp.exepciones.Excepcion;
import jakarta.transaction.Transactional;

@Service
public class FamiliaServiceImpl implements FamiliaService {

	@Autowired
	private FamiliaRepo familiaRepo;

	@Autowired
	private AsistidoServiceImpl asistidoService;

	@Override
	public Familia crearFamilia(Familia familia) throws Excepcion {
		// Setear fecha de alta y activa por defecto
		familia.setFechaRegistro(LocalDate.now());
		familia.setActivo(true);

		// asistidoService.validarDnisFamilia(familia.getAsistidos());

		return familiaRepo.save(familia);
	}

	@Override
	@Transactional
	public Familia modificarFamilia(Integer id, Familia datos) {

		Familia familia = obtenerFamiliaSiExiste(id);

		// Asignar familia a cada asistido nuevo
		List<Asistido> nuevosAsistidos = datos.getAsistidos().stream().peek(a -> a.setFamilia(familia))
				.collect(Collectors.toList());

		// Validar cada asistido contra los demás (exceptuando a sí mismo)
		for (Asistido asistido : nuevosAsistidos) {
			asistidoService.validarDni(asistido, nuevosAsistidos);
		}
		// Actualizar nombre y lista de asistidos
		familia.setNombre(datos.getNombre());
		familia.setAsistidos(nuevosAsistidos);

		return familiaRepo.save(familia);

	}

	@Override
	public List<Familia> listarFamilias() {
		// Solo familias activas
		return familiaRepo.findAll().stream().filter(Familia::isActivo).toList();
	}

	@Override
	public void eliminarFamilia(Integer id) throws Excepcion {
		Familia familia = obtenerFamiliaSiExiste(id);
		familia.setActivo(false); // Eliminación lógica
		familiaRepo.save(familia);

	}

	@Override
	public int contarIntegrantesActivos(Integer idFamilia) {
		Familia familia = obtenerFamiliaSiExiste(idFamilia);
		return (int) familia.getAsistidos().stream().filter(Asistido::isActivo) // Filtra solo los asistidos que están
																				// activos
				.count(); // Cuenta el número de asistidos activos.
	}

	// Método privado para reutilizar búsqueda con validación
	public Familia obtenerFamiliaSiExiste(Integer idFamilia) throws Excepcion {
		return familiaRepo.findById(idFamilia)
				.orElseThrow(() -> new Excepcion("familia", "No se encontró la familia con ID: " + idFamilia));
	}
}
