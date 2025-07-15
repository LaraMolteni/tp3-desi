package desi.tp.presentacion.familia;

import java.time.LocalDate;
import java.util.ArrayList;
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

	// Crear un objeto familiaForm y guardarlo en el Model

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

		boolean filtroAplicado = false;

		model.addAttribute("familias", familias);
		model.addAttribute("filtroAplicado", filtroAplicado);
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

	// Guarda en la BD los cambios hechos en los integrantes (en memoria), y la
	// familia completa.
	@PostMapping("/{id}")
	public String procesarFormulario(@PathVariable Integer id, @ModelAttribute("familiaForm") FamiliaForm form,
			@RequestParam(required = false) String action, RedirectAttributes redirectAttributes,
			SessionStatus status) {

		if ("cancelar".equals(action)) {
			status.setComplete();
			redirectAttributes.addFlashAttribute("mensaje", "Cambios cancelados.");
			return "redirect:/familias";
		}

		try {
			Familia existente = familiaService.obtenerFamiliaSiExiste(id);

			if (action != null) {

				// Guardar cambios de integrante individual
				if (action.startsWith("guardar-")) {
					int index = Integer.parseInt(action.replace("guardar-", ""));
					if (validarIntegrante(form, index, redirectAttributes)) {
						Familia actualizada = form.actualizarEntidad(existente, asistidoService);
						familiaService.modificarFamilia(id, actualizada);
						redirectAttributes.addFlashAttribute("mensaje", "Integrante guardado con éxito.");
					}
					return "redirect:/familias/editarFamilia/" + id;
				}

				// Eliminar integrante

				if (action.startsWith("eliminar-")) {
					int index = Integer.parseInt(action.replace("eliminar-", ""));
					form.getAsistidos().get(index).setActivo(false);

					// Validar que la familia tenga al menos un integrante
					if (form.getAsistidos().stream().noneMatch(AsistidoForm::isActivo)) {
						form.getAsistidos().get(index).setActivo(true); // revertir
						redirectAttributes.addFlashAttribute("error",
								"La familia debe tener al menos un integrante activo.");
						return "redirect:/familias/editarFamilia/" + id;
					}

					Familia actualizada = form.actualizarEntidad(existente, asistidoService);
					familiaService.modificarFamilia(id, actualizada);
					redirectAttributes.addFlashAttribute("mensaje", "Integrante eliminado.");
					return "redirect:/familias/editarFamilia/" + id;
				}

				// Guardar los cambios de la familia completa
				if ("guardar".equals(action)) {
					if(form.getNombreFamilia().isBlank()) {
						redirectAttributes.addFlashAttribute("error", "El nombre de la familia es obligatorio");
						return "redirect:/familias/editarFamilia/" + id;
					}
					Familia actualizada = form.actualizarEntidad(existente, asistidoService);
					familiaService.modificarFamilia(id, actualizada);
					status.setComplete();
					redirectAttributes.addFlashAttribute("mensaje", "Familia actualizada con éxito.");
					return "redirect:/familias";
				}
			}

		} catch (Excepcion e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
			return "redirect:/familias/editarFamilia/" + id;
		}

		return "redirect:/familias";
	}

	// Filtrar familia por id y nombre

	@GetMapping("/filtrar")
	public String buscarFamilias(@RequestParam(required = false) Integer nroFamilia,
			@RequestParam(required = false) String nombre, Model model) {

		List<FamiliaListadoDTO> resultados = familiaService.listarFamilias().stream()
				.map(f -> FamiliaListadoDTO.from(f, familiaService))
				.filter(f -> (nroFamilia == null || f.getNroFamilia().equals(nroFamilia))
						&& (nombre == null || f.getNombreFamilia().toLowerCase().contains(nombre.toLowerCase())))
				.toList();

		boolean filtroAplicado = (nroFamilia != null) || (nombre != null && !nombre.isBlank());

		model.addAttribute("id", nroFamilia); //
		model.addAttribute("nombre", nombre);
		model.addAttribute("familias", resultados);
		model.addAttribute("filtroAplicado", filtroAplicado);
		return "familias/familias";
	}

	// Eliminar familia

	@GetMapping("/eliminar/{id}")
	public String eliminarFamilia(@PathVariable Integer id, RedirectAttributes redirect) {
		familiaService.eliminarFamilia(id);
		redirect.addFlashAttribute("mensaje", "Familia eliminada correctamente.");
		return "redirect:/familias";
	}

	// Validación manual para un integrante individual (al ser en memoria, no puedo
	// utilizar los @Valid directamente)
	private boolean validarIntegrante(FamiliaForm form, int index, RedirectAttributes redirectAttributes) {
		if (index < 0 || index >= form.getAsistidos().size()) {
			redirectAttributes.addFlashAttribute("error", "Índice de integrante inválido.");
			return false;
		}

		AsistidoForm asistido = form.getAsistidos().get(index);
		List<String> errores = new ArrayList<>();

		if (asistido.getDni() == null)
			errores.add("El DNI no puede estar vacío.");
		if (asistido.getNombre() == null || asistido.getNombre().isBlank())
			errores.add("El nombre no puede estar vacío.");
		if (asistido.getApellido() == null || asistido.getApellido().isBlank())
			errores.add("El apellido no puede estar vacío.");
		if (asistido.getFechaNacimiento() != null && asistido.getFechaNacimiento().isAfter(LocalDate.now())) {
			errores.add("La fecha de nacimiento no puede ser futura.");
		}

		if (!errores.isEmpty()) {
			redirectAttributes.addFlashAttribute("error", String.join(" ", errores));
			return false;
		}

		return true;
	}

}
