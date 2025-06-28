package desi.tp.servicios;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import desi.tp.accesoDatos.PreparacionRepo;
import desi.tp.entidades.Preparacion;
import desi.tp.exepciones.Excepcion;


@Service
public class PreparacionServiceImpl implements PreparacionService {

	@Autowired
	private PreparacionRepo preparacionRepo;

	@Override
	public Preparacion crearPreparacion(Preparacion preparacion) throws Excepcion {
		// Validar fecha no futura
		if (preparacion.getFechaCoccion().isAfter(LocalDate.now())) {
			throw new IllegalArgumentException("La fecha no puede ser futura");
		}
		// Validar que no haya dos preparaciones de la misma receta para el mismo día
		boolean existe = preparacionRepo.findAll().stream().filter(Preparacion::isActiva)
				.anyMatch(p -> p.getFechaCoccion().equals(preparacion.getFechaCoccion())
						&& p.getTotalRacionesPreparadas().equals(preparacion.getTotalRacionesPreparadas()));
		if (existe) {
			throw new IllegalArgumentException("Ya existe una preparación de la misma receta para el mismo día");
		}
		preparacion.setActiva(true);
		// TODO: Validar stock suficiente para ingredientes (placeholder)
		return preparacionRepo.save(preparacion);
	}

	@Override
	public Preparacion modificarPreparcion(Integer id, Preparacion datos) {
		Optional<Preparacion> opt = preparacionRepo.findById(id);
		if (opt.isPresent()) {
			Preparacion preparacion = opt.get();
			// Solo se puede editar la fecha
			preparacion.setFechaCoccion(datos.getFechaCoccion());
			return preparacionRepo.save(preparacion);
		}
		return null;
	}

	@Override
	public List<Preparacion> listarPreparaciones() {
		return preparacionRepo.findAll().stream().filter(Preparacion::isActiva).toList();
	}

	@Override
	public void eliminarPreparacion(Integer id) {
		Optional<Preparacion> opt = preparacionRepo.findById(id);
		if (opt.isPresent()) {
			Preparacion preparacio = opt.get();
			preparacio.setActiva(false); // Eliminación lógica
			preparacionRepo.save(preparacio);
		}

	}

}
