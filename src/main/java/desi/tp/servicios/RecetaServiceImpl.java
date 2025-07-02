package desi.tp.servicios;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import desi.tp.accesoDatos.RecetaRepo;
import desi.tp.entidades.Receta;
import desi.tp.exepciones.Excepcion;


@Service
public class RecetaServiceImpl implements RecetaService {

	
	@Autowired
	private RecetaRepo recetaRepo;

	@Override
	public Receta crearReceta(Receta receta) throws Excepcion {
		 // Validar nombre único
        if (recetaRepo.findAll().stream().anyMatch(r -> r.getNombre().equalsIgnoreCase(receta.getNombre()) && r.isActiva())) {
            throw new IllegalArgumentException("Ya existe una receta con ese nombre");
        }
        receta.setActiva(true);
        return recetaRepo.save(receta);
	}

	@Override
	public Receta modificarReceta(Integer id, Receta datos) {
		Optional<Receta> opt = recetaRepo.findById(id);
        if (opt.isPresent()) {
            Receta receta = opt.get();
            receta.setDescripcion(datos.getDescripcion());
            // Actualizar itemsReceta correctamente para evitar error de orphanRemoval
			// Porque sino se estaba generando un tema de desfazaje entre lo que cacheaba hibernate y los nuevos items
            if (receta.getItemsReceta() != null) {
                receta.getItemsReceta().clear();
                if (datos.getItemsReceta() != null) {
                    datos.getItemsReceta().forEach(item -> item.setReceta(receta));
                    receta.getItemsReceta().addAll(datos.getItemsReceta());
                }
            } else if (datos.getItemsReceta() != null) {
                datos.getItemsReceta().forEach(item -> item.setReceta(receta));
                receta.setItemsReceta(datos.getItemsReceta());
            }
            return recetaRepo.save(receta);
        }
        return null;
	}

	@Override
	public List<Receta> listarRecetas() {
		return recetaRepo.findAll().stream().filter(Receta::isActiva).toList();
	}

	@Override
	public void eliminarReceta(Integer id) {
		 Optional<Receta> opt = recetaRepo.findById(id);
	        if (opt.isPresent()) {
	            Receta receta = opt.get();
	            receta.setActiva(false); // Eliminación lógica
	            recetaRepo.save(receta);
	        }
		
	}
	
	@Override
	public Receta buscarPorId(Integer id) {
	    return recetaRepo.findById(id)
	        .orElseThrow(() -> new Excepcion("Receta no encontrada."));
	}
	
	
}
