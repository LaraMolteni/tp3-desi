package desi.tp.presentacion.familia;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import desi.tp.entidades.Asistido;
import desi.tp.entidades.Familia;
import desi.tp.exepciones.Excepcion;
import desi.tp.presentacion.asistido.AsistidoForm;
import desi.tp.servicios.AsistidoServiceImpl;
import desi.tp.servicios.FamiliaServiceImpl;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/familias")
@SessionAttributes("familiaForm")
public class FamiliaController {

	@Autowired
	FamiliaServiceImpl familiaService;

	@Autowired
	AsistidoServiceImpl asistidoService;

	// Crea un objeto familiaForm y lo guarda en el Model
	@ModelAttribute("familiaForm")
	public FamiliaForm familia() {
		FamiliaForm familiaForm = new FamiliaForm();
		familiaForm.setFechaRegistro(LocalDate.now());
		return familiaForm;
	}

	// Listar familias activas

	@GetMapping
	public String listarFamilias(Model model) {
		List<FamiliaListadoDTO> familias = familiaService.listarFamilias().stream()
				.map(f -> FamiliaListadoDTO.from(f, familiaService)).toList();

		model.addAttribute("familias", familias);
		return "familias/familias";
	}

	// Agregar familia

	@GetMapping("agregarFamilia")
	public String mostrarFormulario(@ModelAttribute("familiaForm") FamiliaForm familiaForm) {
		return "familias/agregarFamilia";
	}

	@GetMapping("/nuevoIntegrante")
	public String nuevoIntegrante(Model model) {
		model.addAttribute("asistidoForm", new AsistidoForm());
		return "familias/nuevoIntegrante";
	}

	@PostMapping("/guardarIntegrante")
	public String guardarIntegrante(@Valid @ModelAttribute("asistidoForm") AsistidoForm asistidoForm,
			BindingResult result, @ModelAttribute("familiaForm") FamiliaForm familiaForm, Model model,
			RedirectAttributes redirectAttributes) {

		if (result.hasErrors()) {
			return "familias/nuevoIntegrante";
		}

		try {

			List<Asistido> existentes = familiaForm.getAsistidos().stream().map(AsistidoForm::toEntidad)
					.collect(Collectors.toList());
			Asistido nuevo = asistidoForm.toEntidad();

			asistidoService.validarDnisFamilia(existentes, nuevo);

		} catch (Excepcion e) {
			result.rejectValue("dni", "error.dni", e.getMessage());
			return "familias/nuevoIntegrante";
		}

		asistidoForm.setActivo(true);
		asistidoForm.setFechaRegistro(LocalDate.now());
		familiaForm.getAsistidos().add(asistidoForm);

		redirectAttributes.addFlashAttribute("mensaje", "Integrante agregado con éxito");
		return "redirect:/familias/agregarFamilia";
	}

	@PostMapping("/agregarFamilia")
	public String crearFamilia(@Valid @ModelAttribute("familiaForm") FamiliaForm familiaForm, BindingResult result,
			SessionStatus status, Model model, RedirectAttributes redirectAttributes) throws Excepcion {

		if (result.hasErrors()) {
			return "familias/agregarFamilia";
		}
		// Guardar familia en BD
		familiaService.crearFamilia(familiaForm.toEntidad());

		status.setComplete();
		redirectAttributes.addFlashAttribute("mensaje", "Familia creada con éxito.");
		return "redirect:/familias";
	}

	// Editar familia

	@GetMapping("/editarFamilia/{id}")
	public String mostrarFormularioEdicion(@PathVariable Integer id, Model model) {
		Familia familia = familiaService.obtenerFamiliaSiExiste(id);
		FamiliaForm form = FamiliaForm.desdeEntidad(familia);
		model.addAttribute("familiaForm", form);
		return "familias/editarFamilia";
	}

	@PostMapping("/editarIntegrante/{index}")
	public String editarIntegrante(@Valid @PathVariable int index,
			@ModelAttribute("familiaForm") FamiliaForm familiaForm, BindingResult result,
			RedirectAttributes redirectAttributes) {

		AsistidoForm asistidoEditado = familiaForm.getAsistidos().get(index);

		if (asistidoEditado.getFechaNacimiento() == null) {
			result.rejectValue("asistidos[" + index + "].fechaNacimiento", "error.fechaNacimiento",
					"La fecha de nacimiento no puede ser vacía");
		}

		if (result.hasErrors()) {
			redirectAttributes.addFlashAttribute("error", "Error al guardar integrante.");
			return "redirect:/familias/editarFamilia/" + familiaForm.getIdFamilia();
		}

		redirectAttributes.addFlashAttribute("mensaje", "Integrante editado con éxito.");
		return "redirect:/familias/editarFamilia/" + familiaForm.getIdFamilia();
	}

	@PostMapping("/eliminarIntegrante/{index}")
	public String eliminarIntegrante(@PathVariable int index, @ModelAttribute("familiaForm") FamiliaForm familiaForm,
			RedirectAttributes redirectAttributes) {
		if (index >= 0 && index < familiaForm.getAsistidos().size()) {
			familiaForm.getAsistidos().get(index).setActivo(false);
			redirectAttributes.addFlashAttribute("mensaje", "Integrante eliminado.");
		}
		return "redirect:/familias/editarFamilia/" + familiaForm.getIdFamilia();
	}

	@PostMapping("/{id}")
	public String procesarFormulario(@PathVariable Integer id, @ModelAttribute("familiaForm") FamiliaForm form,
			@RequestParam(required = false) String action, RedirectAttributes redirectAttributes,
			SessionStatus status) {

		// Cancelar edición
		if ("cancelar".equals(action)) {
			status.setComplete();
			redirectAttributes.addFlashAttribute("mensaje", "Cambios cancelados.");
			return "redirect:/familias";
		}

		// Guardar un integrante individual
		if (action != null && action.startsWith("guardar-")) {
			int index = Integer.parseInt(action.replace("guardar-", ""));

			if (index >= 0 && index < form.getAsistidos().size()) {
				AsistidoForm asistidoEditado = form.getAsistidos().get(index);

				if (asistidoEditado.getFechaNacimiento() != null
						&& asistidoEditado.getFechaNacimiento().isAfter(LocalDate.now())) {
					redirectAttributes.addFlashAttribute("error", "La fecha de nacimiento no puede ser futura.");
					return "redirect:/familias/editarFamilia/" + id;
				}

				try {
					Familia existente = familiaService.obtenerFamiliaSiExiste(id);
					Familia actualizada = form.actualizarEntidad(existente, asistidoService);
					familiaService.modificarFamilia(id, actualizada);
					redirectAttributes.addFlashAttribute("mensaje", "Integrante guardado con éxito.");
				} catch (Excepcion e) {
					redirectAttributes.addFlashAttribute("error", e.getMessage());
				}
			}
			return "redirect:/familias/editarFamilia/" + id;
		}

		// Eliminar un integrante individual
		if (action != null && action.startsWith("eliminar-")) {
			int index = Integer.parseInt(action.replace("eliminar-", ""));
			if (index >= 0 && index < form.getAsistidos().size()) {
				form.getAsistidos().get(index).setActivo(false);

				try {
					Familia existente = familiaService.obtenerFamiliaSiExiste(id);
					Familia actualizada = form.actualizarEntidad(existente, asistidoService);
					familiaService.modificarFamilia(id, actualizada);
					redirectAttributes.addFlashAttribute("mensaje", "Integrante eliminado.");
				} catch (Excepcion e) {
					redirectAttributes.addFlashAttribute("error", e.getMessage());
				}
			}
			return "redirect:/familias/editarFamilia/" + id;
		}

		// Guardar todos los cambios de la familia
		if ("guardar".equals(action)) {
			try {
				Familia familiaExistente = familiaService.obtenerFamiliaSiExiste(id);
				Familia actualizada = form.actualizarEntidad(familiaExistente, asistidoService);
				familiaService.modificarFamilia(id, actualizada);
				status.setComplete();
				redirectAttributes.addFlashAttribute("mensaje", "Familia actualizada con éxito.");
				return "redirect:/familias";
			} catch (Excepcion e) {
				redirectAttributes.addFlashAttribute("error", e.getMessage());
				return "redirect:/familias/editarFamilia/" + id;
			}

		}

		// Acción por defecto si no coincide nada
		return "redirect:/familias";
	}

	// Buscar familia por id y nombre

	@GetMapping("/filtrar")
	public String buscarFamilias(@RequestParam(required = false) Integer nroFamilia,
			@RequestParam(required = false) String nombre, Model model) {

		List<FamiliaListadoDTO> resultados = familiaService.listarFamilias().stream()
				.map(f -> FamiliaListadoDTO.from(f, familiaService))
				.filter(f -> (nroFamilia == null || f.getNroFamilia().equals(nroFamilia))
						&& (nombre == null || f.getNombreFamilia().toLowerCase().contains(nombre.toLowerCase())))
				.toList();

		model.addAttribute("id", nroFamilia); //
		model.addAttribute("nombre", nombre);
		model.addAttribute("familias", resultados);
		return "familias/familias";
	}

	// Eliminar familia

	@GetMapping("/eliminar/{id}")
	public String eliminarFamilia(@PathVariable Integer id, RedirectAttributes redirect) {
		familiaService.eliminarFamilia(id);
		redirect.addFlashAttribute("mensaje", "Familia eliminada correctamente.");
		return "redirect:/familias";
	}

}
