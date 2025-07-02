package desi.tp.servicios;

import desi.tp.accesoDatos.IngredienteRepo;
import desi.tp.entidades.Ingrediente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngredienteServiceImpl implements IngredienteService {

    @Autowired
    private IngredienteRepo ingredienteRepo;

    @Override
    public List<Ingrediente> listarTodos() {
        return ingredienteRepo.findByActivoTrue();
    }

    @Override
    public Ingrediente buscarPorId(Integer id) {
        return ingredienteRepo.findById(id).orElse(null);
    }

    @Override
    public Ingrediente guardar(Ingrediente ingrediente) {
        return ingredienteRepo.save(ingrediente);
    }

    @Override
    public Ingrediente modificar(Ingrediente ingrediente) {
        return ingredienteRepo.save(ingrediente);
    }

    @Override
    public void eliminar(Integer id) {
    	ingredienteRepo.findById(id).ifPresent(i -> {
    	     i.setActivo(false);
    	     ingredienteRepo.save(i);
    	});
    }
}