package desi.tp.servicios;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import desi.tp.accesoDatos.EntregaAsistenciaRepo;
import desi.tp.entidades.EntregaAsistencia;
import desi.tp.entidades.Familia;
import desi.tp.entidades.Preparacion;
import desi.tp.exepciones.Excepcion;

import desi.tp.entidades.Asistido;


@Service
public class EntregaAsistenciaServiceImpl implements EntregaAsistenciaService {

	@Autowired
	private EntregaAsistenciaRepo entregaAsistenciaRepo;

	@Autowired 
	private FamiliaService familiaService;

	@Autowired
	private PreparacionService preparacionService;


	
	@Override
	@Transactional 
	public EntregaAsistencia registrarEntrega(Integer idFamilia, Integer idPreparacion, Integer cantidadRaciones) throws Excepcion {
		// 1. Obtener Entidades (Familia, Preparacion)
		Familia familia = familiaService.findById(idFamilia)
				.orElseThrow(() -> new Excepcion("Error: Familia con ID " + idFamilia + " no encontrada."));

		Preparacion preparacion = preparacionService.buscarPorId(idPreparacion); // Asumiendo buscarPorId lanza Excepcion o devuelve objeto directamente


		// 2. Aplicar Criterios de Aceptación (Validaciones)

		// **Validación 1: La cantidad de raciones debe ser positiva.**
		if (cantidadRaciones == null || cantidadRaciones <= 0) {
			throw new Excepcion("Error: La cantidad de raciones a entregar debe ser un número positivo.");
		}

		// **Validación 2: No podrá haber dos entregas en el mismo día para la misma familia.**
		LocalDate fechaActual = LocalDate.now();
		List<EntregaAsistencia> entregasExistentes = entregaAsistenciaRepo.findByFamiliaAndFecha(familia, fechaActual);
		if (!entregasExistentes.isEmpty()) {
			throw new Excepcion("Error: Ya existe una entrega registrada para la familia '" + familia.getNombre() + "' en la fecha actual (" + fechaActual + ").");
		}

		// **Validación 3: No se puede entregar más raciones que integrantes activos registrados para la familia.**
		int cantidadIntegrantesFamilia = familiaService.contarIntegrantesActivos(familia.getIdFamilia());
		if (cantidadIntegrantesFamilia == 0) {
			throw new Excepcion("Error: La familia '" + familia.getNombre() + "' no tiene integrantes activos registrados.");
		}
		if (cantidadRaciones > cantidadIntegrantesFamilia) {
			throw new Excepcion("Error: No se puede entregar más raciones (" + cantidadRaciones + ") que integrantes activos de la familia (" + cantidadIntegrantesFamilia + ").");
		}

		// **Validación 4: Suficiente stock de la Preparación antes de dar de baja.**
		if (preparacion.getTotalRacionesPreparadas() == null || cantidadRaciones > preparacion.getStockRacionesRestantes()) {
			throw new Excepcion("Error: No hay suficiente stock de raciones para el plato '" + preparacion.getReceta().getNombre() + "'. Stock disponible: " + preparacion.getStockRacionesRestantes());
		}

		// 3. Actualizar Stock de la Preparación (dar de baja del stock las raciones entregadas)
		preparacion.setStockRacionesRestantes(preparacion.getStockRacionesRestantes() - cantidadRaciones);
		preparacionService.guardar(preparacion); // Usamos el método guardar

		// 4. Crear y Guardar la nueva EntregaAsistencia
		EntregaAsistencia nuevaEntrega = new EntregaAsistencia();
		nuevaEntrega.setFamilia(familia);
		nuevaEntrega.setPreparacion(preparacion);
		nuevaEntrega.setCantidadRaciones(cantidadRaciones);
		nuevaEntrega.setFecha(fechaActual); // La fecha se asigna automáticamente al día de hoy
		nuevaEntrega.setActivo(true); // Se establece como activo por defecto

		return entregaAsistenciaRepo.save(nuevaEntrega); // Guarda la nueva entrega
	}


	
	@Override
	public EntregaAsistencia crearEntrega(EntregaAsistencia entrega) throws Excepcion {
		// Validar que no haya dos entregas el mismo día para la misma familia
		boolean existe = entregaAsistenciaRepo.findAll().stream().filter(EntregaAsistencia::isActivo)
				.anyMatch(r -> r.getFecha().equals(LocalDate.now()) && r.getFamilia() != null
						&& entrega.getFamilia() != null
						&& r.getFamilia().getIdFamilia().equals(entrega.getFamilia().getIdFamilia()));
		if (existe) {
			throw new Excepcion("Ya existe una entrega para esa familia en el día de hoy"); // Usar Excepcion
		}
		// Validar que no se entreguen más raciones que integrantes activos
		if (entrega.getFamilia() != null) {
			long integrantesActivos = entrega.getFamilia().getAsistidos().stream().filter(Asistido::isActivo).count(); 
			if (entrega.getCantidadRaciones() > integrantesActivos) {
				throw new Excepcion("No se pueden entregar más raciones que integrantes activos"); // Usar Excepcion
			}
		}

		entrega.setFecha(LocalDate.now());
		entrega.setActivo(true);
		return entregaAsistenciaRepo.save(entrega);
	}

	@Override
	public EntregaAsistencia modificarEntrega(Integer id, EntregaAsistencia datos) {
		Optional<EntregaAsistencia> opt = entregaAsistenciaRepo.findById(id);
		if (opt.isPresent()) {
			EntregaAsistencia entrega = opt.get();
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
	@Transactional // Es importante que este método sea transaccional
    public void eliminarEntrega(Integer id) {
        Optional<EntregaAsistencia> opt = entregaAsistenciaRepo.findById(id);
        if (opt.isPresent()) {
            EntregaAsistencia entrega = opt.get();
            
            // Revertir el stock de la Preparación
            Preparacion preparacion = entrega.getPreparacion();
            if (preparacion != null) {
                // Sumar las raciones de vuelta al stock
                preparacion.setStockRacionesRestantes(preparacion.getStockRacionesRestantes() + entrega.getCantidadRaciones());
                preparacionService.guardar(preparacion); // Guarda la preparación con el stock actualizado
            }
            
            entrega.setActivo(false); // Eliminación lógica
            entregaAsistenciaRepo.save(entrega);
        } 
    }



	@Override
	public List<EntregaAsistencia> filtrarEntregas(LocalDate fecha, Integer idFamilia, String nombreFamilia) {
		// Implementación básica: filtra por los parámetros si no son null
		List<EntregaAsistencia> entregas = entregaAsistenciaRepo.findAll();
		if (fecha != null) {
			entregas = entregas.stream().filter(e -> fecha.equals(e.getFecha())).toList();
		}
		if (idFamilia != null) {
			entregas = entregas.stream().filter(e -> e.getFamilia() != null && idFamilia.equals(e.getFamilia().getIdFamilia())).toList();
		}
		if (nombreFamilia != null && !nombreFamilia.isEmpty()) {
			entregas = entregas.stream().filter(e -> e.getFamilia() != null && e.getFamilia().getNombre() != null && e.getFamilia().getNombre().toLowerCase().contains(nombreFamilia.toLowerCase())).toList();
		}
		return entregas;
	}
	
	
	
	/*public void eliminarEntrega(Integer id) {
		Optional<EntregaAsistencia> opt = entregaAsistenciaRepo.findById(id);
		if (opt.isPresent()) {
			EntregaAsistencia entrega = opt.get();
			entrega.setActivo(false); // Eliminación lógica
			entregaAsistenciaRepo.save(entrega);
		}
	}*/
}








/*package desi.tp.servicios;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import desi.tp.accesoDatos.EntregaAsistenciaRepo;
import desi.tp.accesoDatos.VoluntarioRepo;
import desi.tp.entidades.EntregaAsistencia;
import desi.tp.entidades.Familia;
import desi.tp.entidades.Preparacion;
import desi.tp.entidades.Voluntario;
import desi.tp.exepciones.Excepcion;

@Service
public class EntregaAsistenciaServiceImpl implements EntregaAsistenciaService {

	@Autowired 
	private FamiliaService familiaService;

	@Autowired 
	private PreparacionService preparacionService;

	@Autowired 
	private VoluntarioRepo voluntarioRepo;
	
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
*/