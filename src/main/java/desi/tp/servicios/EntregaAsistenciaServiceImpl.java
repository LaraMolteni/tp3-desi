package desi.tp.servicios;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import desi.tp.accesoDatos.EntregaAsistenciaRepo;
import desi.tp.entidades.EntregaAsistencia;
import desi.tp.exepciones.Excepcion;

@Service
public class EntregaAsistenciaServiceImpl implements EntregaAsistenciaService {

	@Autowired
	private EntregaAsistenciaRepo entregaAsistenciaRepo;

	@Override
	public EntregaAsistencia crearEntrega(EntregaAsistencia entrega) throws Excepcion {
		// Validar que no haya dos entregas el mismo día para la misma familia
		boolean existe = entregaAsistenciaRepo.findAll().stream().filter(EntregaAsistencia::isActivo)
				.anyMatch(r -> r.getFecha().equals(LocalDate.now()) && r.getFamilia() != null
						&& entrega.getFamilia() != null
						&& r.getFamilia().getIdFamilia().equals(entrega.getFamilia().getIdFamilia()));
		if (existe) {
			throw new IllegalArgumentException("Ya existe una entrega para esa familia en el día de hoy");
		}
		// Validar que no se entreguen más raciones que integrantes activos
		if (entrega.getFamilia() != null) {
			long integrantesActivos = entrega.getFamilia().getAsistidos().stream().filter(b -> b.isActivo()).count();
			if (entrega.getCantidadRaciones() > integrantesActivos) {
				throw new IllegalArgumentException("No se pueden entregar más raciones que integrantes activos");
			}
		}
		entrega.setFecha(LocalDate.now());
		entrega.setActivo(true);
		// TODO: Dar de baja del stock las raciones entregadas (placeholder)
		return entregaAsistenciaRepo.save(entrega);
	}

	@Override
	public EntregaAsistencia modificarEntrega(Integer id, EntregaAsistencia datos) {
		Optional<EntregaAsistencia> opt = entregaAsistenciaRepo.findById(id);
		if (opt.isPresent()) {
			EntregaAsistencia entrega = opt.get();
			// Solo se puede editar cantidadEntregada y racion
			entrega.setCantidadRaciones(datos.getCantidadRaciones());
			entrega.setPreparacion(datos.getPreparacion());
			return entregaAsistenciaRepo.save(entrega);
		}
		return null;
	}

	@Override
	public List<EntregaAsistencia> listarEntregas() {
		return entregaAsistenciaRepo.findAll().stream().filter(EntregaAsistencia::isActivo).toList();
	}

	@Override
	public void eliminarEntrega(Integer id) {
		Optional<EntregaAsistencia> opt = entregaAsistenciaRepo.findById(id);
		if (opt.isPresent()) {
			EntregaAsistencia entrega = opt.get();
			entrega.setActivo(false); // Eliminación lógica
			entregaAsistenciaRepo.save(entrega);
		}
	}
}
