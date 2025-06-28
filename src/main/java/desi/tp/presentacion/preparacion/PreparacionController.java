package desi.tp.presentacion.preparacion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import desi.tp.entidades.Preparacion;
import desi.tp.servicios.PreparacionService;



@Controller
@RequestMapping("/preparaciones")
public class PreparacionController {
	
    @Autowired
    private PreparacionService preparacionService;

    /*@PostMapping
    public Preparacion crearPreparacion(@RequestBody Preparacion preparacion) throws Excepcion {
        if (preparacion.getFechaCoccion() == null || preparacion.getTotalRacionesPreparadas() == null || preparacion.getTotalRacionesPreparadas().isEmpty()) {
            throw new IllegalArgumentException("Fecha y raciones son requeridas");
        }
        return preparacionService.crearPreparacion(preparacion);
    }*/

    @PutMapping("/{id}")
    public ResponseEntity<Preparacion> modificarPreparacion(@PathVariable Integer id, @RequestBody Preparacion datos) {
        // Solo se puede editar la fecha
    	Preparacion actualizado = preparacionService.modificarPreparcion(id, datos);
        if (actualizado != null) {
            return ResponseEntity.ok(actualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public List<Preparacion> listarPreparaciones() {
        return preparacionService.listarPreparaciones();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPreparacion(@PathVariable Integer id) {
        preparacionService.eliminarPreparacion(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint para filtrar por fecha y nombre de receta
    @GetMapping("/buscar")
    public List<Preparacion> buscarPreparaciones(@RequestParam(required = false) String fecha,
                                                            @RequestParam(required = false) String nombreReceta) {
        return preparacionService.listarPreparaciones().stream()
            .filter(p -> (fecha == null || p.getFechaCoccion().toString().equals(fecha)))
            .filter(p -> (nombreReceta == null || p.getReceta().getPreparaciones().stream().anyMatch(r -> r.getReceta().getNombre().toLowerCase().contains(nombreReceta.toLowerCase()))))
            .toList();
    }

    
    
}

