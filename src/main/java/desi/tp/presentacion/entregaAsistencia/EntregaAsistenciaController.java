package desi.tp.presentacion.entregaAsistencia;

import java.util.List;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.ModelAttribute;

import desi.tp.entidades.EntregaAsistencia;
import desi.tp.exepciones.Excepcion;
import desi.tp.servicios.EntregaAsistenciaService;
import desi.tp.servicios.FamiliaService;
import desi.tp.servicios.PreparacionService;

@Controller
@RequestMapping("/entregaAsistencia")
public class EntregaAsistenciaController {
	@Autowired
	private EntregaAsistenciaService entregaAsistenciaService;
	@Autowired
	private FamiliaService familiaService;
	@Autowired
	private PreparacionService preparacionService;

	@PutMapping("/{id}")
	public ResponseEntity<EntregaAsistencia> modificarEntrega(@PathVariable Integer id,
			@RequestBody EntregaAsistencia datos) {
		EntregaAsistencia actualizado = entregaAsistenciaService.modificarEntrega(id, datos);
		if (actualizado != null) {
			return ResponseEntity.ok(actualizado);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminarEntrega(@PathVariable Integer id) {
		entregaAsistenciaService.eliminarEntrega(id);
		return ResponseEntity.noContent().build();
	}

	// *** ENDPOINT PARA EL LISTADO FILTRADO ***

	@GetMapping("/filtrar") // Un nuevo path más específico para el filtrado
	public List<EntregaAsistenciaListadoDTO> listarEntregasFiltradas(@RequestParam(required = false) LocalDate fecha, // @RequestParam
																														// para
																														// parámetros
																														// de
																														// URL
			@RequestParam(required = false) Integer idFamilia, @RequestParam(required = false) String nombreFamilia) {

		// Llama al nuevo método del servicio que realiza el filtrado
		List<EntregaAsistencia> entregasFiltradas = entregaAsistenciaService.filtrarEntregas(fecha, idFamilia,
				nombreFamilia);

		// Mapea los resultados filtrados al DTO de listado existente
		return entregasFiltradas.stream().map(r -> {
			EntregaAsistenciaListadoDTO dto = new EntregaAsistenciaListadoDTO();
			if (r.getFamilia() != null) {
				dto.setNroFamilia(r.getFamilia().getIdFamilia());
				dto.setNombreFamilia(r.getFamilia().getNombre());
			}
			dto.setFechaEntrega(r.getFecha());
			if (r.getPreparacion() != null && r.getPreparacion().getReceta() != null) { // Agregado null check para
																						// receta
				dto.setNombrePlato(r.getPreparacion().getReceta().getNombre());
			}
			dto.setCantidadRaciones(r.getCantidadRaciones());
			return dto;
		}).toList();
	}

	@GetMapping("/listado-ext")
	public List<EntregaAsistenciaListadoDTO> listadoExtendido() {
		return entregaAsistenciaService.listarEntregas().stream().map(r -> {
			EntregaAsistenciaListadoDTO dto = new EntregaAsistenciaListadoDTO();
			if (r.getFamilia() != null) {
				dto.setNroFamilia(r.getFamilia().getIdFamilia());
				dto.setNombreFamilia(r.getFamilia().getNombre());
			}
			dto.setFechaEntrega(r.getFecha());
			if (r.getPreparacion() != null) {
				dto.setNombrePlato(r.getPreparacion().getReceta().getNombre());
			}
			dto.setCantidadRaciones(r.getCantidadRaciones());
			return dto;
		}).toList();
	}

	@GetMapping("/form")
	public String mostrarFormularioEntrega(Model model) {
		model.addAttribute("entregaForm", new RegistrarEntregaRequestDTO());
		model.addAttribute("familias", familiaService.listarFamilias());
		model.addAttribute("preparaciones", preparacionService.listarPreparacionesActivas());
		model.addAttribute("entregas", entregaAsistenciaService.listarEntregas());
		return "entregaAsistencia";
	}

	@PostMapping("/registrar")
	public String registrarEntrega(@ModelAttribute("entregaForm") RegistrarEntregaRequestDTO request, Model model,
			RedirectAttributes redirectAttrs) {
		try {
			entregaAsistenciaService.registrarEntrega(request.getIdFamilia(), request.getIdPreparacion(),
					request.getCantidadRaciones());
			redirectAttrs.addFlashAttribute("success", "Entrega registrada correctamente.");
		} catch (Excepcion e) {
			redirectAttrs.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/entregaAsistencia/form";
	}
}