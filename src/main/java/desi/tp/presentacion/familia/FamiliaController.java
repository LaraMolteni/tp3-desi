package desi.tp.presentacion.familia;

import java.time.LocalDate;
import java.util.List;

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

	@ModelAttribute("familiaForm")
	public FamiliaForm familia() {
		FamiliaForm familiaForm = new FamiliaForm();
		familiaForm.setFechaRegistro(LocalDate.now());
		return familiaForm;
	}

	//Agregar familia
	@GetMapping("/agregarFamilia")
	public String mostrarFormulario(@ModelAttribute("familiaForm") FamiliaForm familiaForm) {
		return "agregarFamilia";
	}

	@GetMapping("/nuevoIntegrante")
	public String nuevoIntegrante(Model model) {
		model.addAttribute("asistidoForm", new AsistidoForm());
		return "nuevoIntegrante";
	}

	@PostMapping("/guardarIntegrante")
	public String guardarIntegrante(@Valid @ModelAttribute("asistidoForm") AsistidoForm asistidoForm,
			BindingResult result, @ModelAttribute("familiaForm") FamiliaForm familiaForm, Model model) {

		if (result.hasErrors()) {
			return "nuevoIntegrante";
		}

		if (asistidoForm.getFechaNacimiento().isAfter(LocalDate.now())) {
			result.rejectValue("fechaNacimiento", "error.fechaNacimiento",
					"La fecha de nacimiento no puede ser futura.");
			return "nuevoIntegrante";
		}

		// Validar DNI duplicado en la familia en memoria
		boolean dniRepetido = familiaForm.getAsistidos().stream()
				.anyMatch(a -> a.getDni().equals(asistidoForm.getDni()));

		if (dniRepetido) {
			result.rejectValue("dni", "error.dni", "Ya existe una persona con ese DNI en esta familia.");
			return "nuevoIntegrante";
		}

		List<Asistido> asistidosConvertidos = familiaForm.getAsistidos().stream().map(AsistidoForm::toEntidad).toList();
		try {
			Asistido integrante = asistidoForm.toEntidad();
			asistidoService.validarDni(integrante, asistidosConvertidos);
		} catch (Excepcion e) {
			result.rejectValue(e.getAtributo(), "error." + e.getAtributo(), e.getMessage());
			return "nuevoIntegrante";
		}

		// Si todo está bien, agregar integrante
		asistidoForm.setActivo(true);
		asistidoForm.setFechaRegistro(LocalDate.now());
		familiaForm.getAsistidos().add(asistidoForm);
		model.addAttribute("mensaje", "Integrante agregado con éxito");

		return "redirect:/familias/agregarFamilia";
	}

	@PostMapping("/agregarFamilia")
	public String crearFamilia(@ModelAttribute("familiaForm") FamiliaForm familiaForm, BindingResult result,
			SessionStatus status, Model model) throws Excepcion {

		if (familiaForm.getNombre() == null || familiaForm.getNombre().isBlank()) {
			result.rejectValue("nombre", "error.nombre", "El nombre es obligatorio.");
		}

		if (familiaForm.getAsistidos() == null || familiaForm.getAsistidos().isEmpty()) {
			result.rejectValue("asistidos", "error.asistidos", "Debe agregar al menos un integrante.");
		}

		if (result.hasErrors()) {
			return "agregarFamilia";
		}

		Familia familia = familiaForm.toEntidad();
		familiaService.crearFamilia(familia);

		status.setComplete();
		model.addAttribute("mensaje", "Familia creada con éxito.");
		return "redirect:/familias";
	}

	// Editar familia

	@GetMapping("/editarFamilia/{id}")
	public String mostrarFormularioEdicion(@PathVariable Integer id, Model model) {
		Familia familia = familiaService.obtenerFamiliaPorId(id);
		FamiliaForm form = FamiliaForm.desdeEntidad(familia);
		model.addAttribute("familiaForm", form);
		return "editarFamilia";
	}

	@PostMapping("/editarIntegrante/{index}")
	public String editarIntegrante(@PathVariable int index, @ModelAttribute("familiaForm") FamiliaForm familiaForm,
			RedirectAttributes redirectAttributes) {
		if (index >= 0 && index < familiaForm.getAsistidos().size()) {
			AsistidoForm asistido = familiaForm.getAsistidos().get(index);
			if (asistido.getFechaNacimiento() != null && asistido.getFechaNacimiento().isAfter(LocalDate.now())) {
				redirectAttributes.addFlashAttribute("error", "La fecha de nacimiento no puede ser futura.");
			} else {
				redirectAttributes.addFlashAttribute("mensaje", "Integrante editado con éxito.");
			}
		}
		return "redirect:/familias/editarFamilia/" + familiaForm.getIdFamilia();
	}

	@GetMapping("/eliminarIntegrante/{index}")
	public String eliminarIntegrante(@PathVariable int index, @ModelAttribute("familiaForm") FamiliaForm familiaForm,
			RedirectAttributes redirectAttributes) {

		if (index >= 0 && index < familiaForm.getAsistidos().size()) {
			familiaForm.getAsistidos().get(index).setActivo(false);
			redirectAttributes.addFlashAttribute("mensaje", "Integrante marcado como inactivo.");
		} else {
			redirectAttributes.addFlashAttribute("error", "Índice de integrante inválido.");
		}

		return "redirect:/familias/editarFamilia/" + familiaForm.getIdFamilia();
	}

	@PostMapping("/editarFamilia/{id}")
	public String modificarFamilia(@PathVariable Integer id, @ModelAttribute FamiliaForm form) {
		Familia existente = familiaService.obtenerFamiliaPorId(id);
		existente.setNombre(form.getNombre());
		existente.setFechaRegistro(form.getFechaRegistro());

		List<Asistido> actualizados = form.getAsistidos().stream()
		    .map(f -> {
		        Asistido a = f.toEntidad();
		        a.setFamilia(existente);  // muy importante
		        return a;
		    }).toList();

		existente.setAsistidos(actualizados);

		familiaService.modificarFamilia(id, existente);
		return "redirect:/familias";	}

	// Botones guardar cambios y cancelar

	@PostMapping("/{id}")
	public String procesarFormulario(@PathVariable Integer id, @ModelAttribute FamiliaForm form,
			@RequestParam(required = false) String action, RedirectAttributes redirectAttributes) {

		if ("cancelar".equals(action)) {
			redirectAttributes.addFlashAttribute("mensaje", "Cambios descartados.");
			return "redirect:/familias";
		}

		if ("guardar".equals(action)) {
			Familia familia = form.toEntidad();
			familiaService.modificarFamilia(id, familia);
			redirectAttributes.addFlashAttribute("mensaje", "Cambios guardados con éxito.");
			return "redirect:/familias";
		}

		return "redirect:/familias";
	}

	// Listar familias activas

	@GetMapping
	public String listarFamilias(Model model) {
		List<FamiliaListadoDTO> familias = familiaService.listarFamilias().stream().map(f -> {
			FamiliaListadoDTO dto = new FamiliaListadoDTO();
			dto.setNroFamilia(f.getIdFamilia());
			dto.setNombreFamilia(f.getNombre());
			dto.setFechaAlta(f.getFechaRegistro());
			// Fecha última asistencia: no implementado, placeholder null
			dto.setFechaUltimaAsistencia(null);
			// Nro de integrantes activos
			dto.setNroIntegrantes((int) f.getAsistidos().stream().filter(b -> b.isActivo()).count());
			return dto;
		}).toList();
		model.addAttribute("familias", familias);
		return "familias";
	}

	// Buscar familia por id y nombre
	@GetMapping("/buscarFamilia")
	public String buscarFamilias(@RequestParam(required = false) Integer nroFamilia,
			@RequestParam(required = false) String nombre, Model model) {

		List<Familia> resultados = familiaService.listarFamilias().stream()
				.filter(f -> (nroFamilia == null || f.getIdFamilia().equals(nroFamilia))
						&& (nombre == null || f.getNombre().toLowerCase().contains(nombre.toLowerCase())))
				.toList();

		model.addAttribute("id", nroFamilia); // opcional si lo usás
		model.addAttribute("nombre", nombre);
		model.addAttribute("familias", resultados);
		return "buscarFamilia";
	}

	// Eliminar familia

	@GetMapping("/eliminar/{id}")
	public String eliminarFamilia(@PathVariable Integer id, RedirectAttributes redirect) {
		familiaService.eliminarFamilia(id);
		redirect.addFlashAttribute("mensaje", "Familia eliminada correctamente.");
		return "redirect:/familias";
	}

}
