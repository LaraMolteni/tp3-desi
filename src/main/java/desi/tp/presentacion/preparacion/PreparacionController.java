package desi.tp.presentacion.preparacion;

import desi.tp.entidades.Preparacion;
import desi.tp.servicios.PreparacionService;
import desi.tp.servicios.RecetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/preparaciones")
public class PreparacionController {

    @Autowired
    private PreparacionService preparacionService;

    @Autowired
    private RecetaService recetaService;

    @GetMapping
    public String listarPreparaciones(@RequestParam(required = false) String receta,
                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                                       Model model) {
        List<Preparacion> filtradas = preparacionService.filtrar(receta, fecha);
        model.addAttribute("preparaciones", filtradas);
        return "preparaciones/list";
    }


    @GetMapping("/nueva")
    public String nuevaPreparacion(Model model) {
        model.addAttribute("preparacion", new Preparacion());
        model.addAttribute("recetas", recetaService.listarRecetas());
        return "preparaciones/form";
    }

    @PostMapping("/guardar")
    public String guardarPreparacion(@ModelAttribute Preparacion preparacion, RedirectAttributes redirect) {
        try {
            preparacionService.crearPreparacion(preparacion);
            redirect.addFlashAttribute("success", "Preparación creada exitosamente.");
            return "redirect:/preparaciones";
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/preparaciones/nueva";
        }
    }

    @GetMapping("/editar/{id}")
    public String editarPreparacion(@PathVariable Integer id, Model model) {
        Preparacion preparacion = preparacionService.buscarPorId(id);
        model.addAttribute("preparacion", preparacion);
        return "preparaciones/edit";
    }

    @PostMapping("/modificar")
    public String modificarPreparacion(@ModelAttribute Preparacion preparacion, RedirectAttributes redirect) {
        try {
            preparacionService.modificarFecha(preparacion.getId(), preparacion.getFechaCoccion());
            redirect.addFlashAttribute("success", "Fecha modificada correctamente.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/preparaciones/editar/" + preparacion.getId();
        }
        return "redirect:/preparaciones";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarPreparacion(@PathVariable Integer id, RedirectAttributes redirect) {
        preparacionService.eliminarPreparacion(id);
        redirect.addFlashAttribute("success", "Preparación eliminada correctamente.");
        return "redirect:/preparaciones";
    }
}
