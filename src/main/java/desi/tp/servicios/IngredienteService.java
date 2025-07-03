package desi.tp.servicios;

import desi.tp.entidades.Ingrediente;
import java.util.List;

public interface IngredienteService {
    List<Ingrediente> listarTodos();
    Ingrediente buscarPorId(Integer id);
    Ingrediente guardar(Ingrediente ingrediente);
    Ingrediente modificar(Ingrediente ingrediente);
    void eliminar(Integer id);
}
