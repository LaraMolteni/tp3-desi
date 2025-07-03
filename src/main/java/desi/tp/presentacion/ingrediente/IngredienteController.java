package desi.tp.presentacion.ingrediente;

import desi.tp.entidades.Ingrediente;
import desi.tp.servicios.IngredienteService;
import desi.tp.accesoDatos.IngredienteRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/ingredientes")
public class IngredienteController {

    @Autowired
    private IngredienteRepo ingredienteRepo;
    
    @Autowired
    private IngredienteService ingredienteService;
    

    @GetMapping("")
    public String listar(Model model) {
    	model.addAttribute("ingredientes", ingredienteService.listarTodos()); 
        return "ingredientes/list";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("ingrediente", new Ingrediente());
        return "ingredientes/form";
    }

    @PostMapping("/nuevo")
    public String crear(@ModelAttribute @Valid Ingrediente ingrediente, BindingResult result, Model model) {
        if (result.hasErrors() || ingrediente.getNombre() == null || ingrediente.getNombre().isBlank()) {
            model.addAttribute("error", "El nombre es requerido");
            return "ingredientes/form";
        }
        ingredienteRepo.save(ingrediente);
        return "redirect:/ingredientes?success";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Optional<Ingrediente> ing = ingredienteRepo.findById(id);
        if (ing.isPresent()) {
            model.addAttribute("ingrediente", ing.get());
            return "ingredientes/edit";
        }
        return "redirect:/ingredientes";
    }

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Integer id, @ModelAttribute @Valid Ingrediente ingrediente, BindingResult result, Model model) {
        if (result.hasErrors() || ingrediente.getNombre() == null || ingrediente.getNombre().isBlank()) {
            model.addAttribute("error", "El nombre es requerido");
            return "ingredientes/edit";
        }
        ingrediente.setId(id);
        ingredienteRepo.save(ingrediente);
        return "redirect:/ingredientes?success";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        ingredienteService.eliminar(id);
        return "redirect:/ingredientes?deleted";
    }
}
