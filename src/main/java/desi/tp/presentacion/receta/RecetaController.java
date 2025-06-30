package desi.tp.presentacion.receta;

import desi.tp.entidades.Receta;
import desi.tp.entidades.ItemReceta;
import desi.tp.entidades.Ingrediente;
import desi.tp.accesoDatos.IngredienteRepo;
import desi.tp.accesoDatos.ItemRecetaRepo;
import desi.tp.servicios.RecetaService;
import desi.tp.exepciones.Excepcion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Map;

@Controller
@RequestMapping("/recetas")
public class RecetaController {

    @Autowired
    private RecetaService recetaService;
    @Autowired
    private IngredienteRepo ingredienteRepo;
    @Autowired
    private ItemRecetaRepo itemRecetaRepo;

    @GetMapping("")
    public String listarRecetas(@RequestParam(required = false) String nombre,
                                @RequestParam(required = false) Integer minCalorias,
                                @RequestParam(required = false) Integer maxCalorias,
                                Model model) {
        List<Receta> recetas = recetaService.listarRecetas();
        // Filtros, also known as magia negra
        if (nombre != null && !nombre.isEmpty()) {
            recetas = recetas.stream().filter(r -> r.getNombre().toLowerCase().contains(nombre.toLowerCase())).toList();
        }
        if (minCalorias != null) {
            recetas = recetas.stream().filter(r -> r.getItemsReceta().stream().mapToInt(ItemReceta::getCalorias).sum() >= minCalorias).toList();
        }
        if (maxCalorias != null) {
            recetas = recetas.stream().filter(r -> r.getItemsReceta().stream().mapToInt(ItemReceta::getCalorias).sum() <= maxCalorias).toList();
        }
        model.addAttribute("recetas", recetas);
        return "recetas";
    }

    @GetMapping("/nueva")
    public String mostrarFormularioNuevaReceta(Model model) {
        model.addAttribute("receta", new Receta());
        model.addAttribute("ingredientes", ingredienteRepo.findAll());
        return "agregarReceta";
    }

    @PostMapping("/nueva")
    public String crearReceta(@ModelAttribute Receta receta, BindingResult result, Model model, @RequestParam Map<String, String> params) {
        // Validación básica de nombre y descripción
        if (receta.getNombre() == null || receta.getNombre().isBlank() || receta.getDescripcion() == null || receta.getDescripcion().isBlank()) {
            model.addAttribute("error", "Todos los campos son requeridos");
            model.addAttribute("ingredientes", ingredienteRepo.findAll());
            return "agregarReceta";
        }
        // Procesar ingredientes dinámicos
        List<ItemReceta> items = new ArrayList<>();
        int idx = 0;
        while (params.containsKey("itemsReceta[" + idx + "].ingrediente.id")) {
            String idStr = params.get("itemsReceta[" + idx + "].ingrediente.id");
            String cantidadStr = params.get("itemsReceta[" + idx + "].cantidad");
            String caloriasStr = params.get("itemsReceta[" + idx + "].calorias");
            if (idStr != null && cantidadStr != null && caloriasStr != null) {
                try {
                    Integer ingId = Integer.parseInt(idStr);
                    double cantidad = Double.parseDouble(cantidadStr);
                    int calorias = Integer.parseInt(caloriasStr);
                    if (cantidad > 0 && calorias > 0) {
                        Ingrediente ing = ingredienteRepo.findById(ingId).orElse(null);
                        if (ing != null) {
                            ItemReceta item = new ItemReceta();
                            item.setIngrediente(ing);
                            item.setCantidad((int) (cantidad * 1000)); // Guardando en gramos, ¿cambiamos a kg para estandarizar?
                            item.setCalorias(calorias);
                            item.setReceta(receta);
                            items.add(item);
                        }
                    }
                } catch (Exception ignored) {}
            }
            idx++;
        }
        if (items.isEmpty()) {
            model.addAttribute("error", "Debe agregar al menos un ingrediente");
            model.addAttribute("ingredientes", ingredienteRepo.findAll());
            return "agregarReceta";
        }
        receta.setItemsReceta(items);
        try {
            recetaService.crearReceta(receta);
            return "redirect:/recetas?success";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("ingredientes", ingredienteRepo.findAll());
            return "agregarReceta";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarReceta(@PathVariable Integer id, Model model) {
        Optional<Receta> recetaOpt = recetaService.listarRecetas().stream().filter(r -> r.getId().equals(id)).findFirst();
        if (recetaOpt.isPresent()) {
            model.addAttribute("receta", recetaOpt.get());
            model.addAttribute("ingredientes", ingredienteRepo.findAll());
            return "editarReceta";
        }
        return "redirect:/recetas";
    }

    @PostMapping("/editar/{id}")
    public String editarReceta(@PathVariable Integer id, @ModelAttribute @Valid Receta receta, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("ingredientes", ingredienteRepo.findAll());
            return "editarReceta";
        }
        recetaService.modificarReceta(id, receta);
        return "redirect:/recetas?success";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarReceta(@PathVariable Integer id) {
        recetaService.eliminarReceta(id);
        return "redirect:/recetas?deleted";
    }
}
