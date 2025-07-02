package desi.tp.servicios;

import desi.tp.accesoDatos.IngredienteRepo;
import desi.tp.entidades.Ingrediente;
import desi.tp.entidades.ItemReceta;
import desi.tp.entidades.Preparacion;
import desi.tp.entidades.Receta;
import desi.tp.exepciones.Excepcion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StockServiceImpl implements StockService {

    @Autowired
    private IngredienteRepo ingredienteRepo;

    @Override
    public void validarStockSuficiente(Receta receta, Integer raciones) {
        for (ItemReceta item : receta.getItemsReceta()) {
            Ingrediente ingrediente = item.getIngrediente();
            Double requerido = (double) (item.getCantidad() * raciones);
            if (ingrediente.getStockDisponible() == null || ingrediente.getStockDisponible() < requerido) {
                throw new Excepcion("No hay stock suficiente de " + ingrediente.getNombre());
            }
        }
    }

    @Override
    public void descontarStock(Preparacion preparacion) {
        Receta receta = preparacion.getReceta();
        Integer raciones = preparacion.getTotalRacionesPreparadas();
        for (ItemReceta item : receta.getItemsReceta()) {
            Ingrediente ingrediente = item.getIngrediente();
            int nuevoStock = ingrediente.getStockDisponible() - (item.getCantidad() * raciones);
            ingrediente.setStockDisponible(nuevoStock);
            ingredienteRepo.save(ingrediente);
        }
    }
}
