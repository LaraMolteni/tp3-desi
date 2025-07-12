package desi.tp.config;

import desi.tp.entidades.Ingrediente;
import desi.tp.servicios.IngredienteService;
import org.springframework.beans.factory.annotation.Autowired; // Inyección de dependencias automática
import org.springframework.boot.CommandLineRunner; // Interfaz que ejecuta código al iniciar la aplicación
import org.springframework.stereotype.Component; // Marca la clase como un bean gestionado por Spring

// Tomé el approach programático porque es más flexible y permite agregar lógica adicional si es necesario. El script SQL estaba dando problemas por el dtype de ingrediente y no pude resolverlo.
@Component // Esta anotación le dice a Spring: "Esta clase es un bean"
public class DataInitializer implements CommandLineRunner {

    @Autowired // Spring automáticamente busca un bean de tipo IngredienteService y lo inyecta acá
    private IngredienteService ingredienteService;

    @Override
    public void run(String... args) throws Exception {
        // Verificar si la tabla ingredientes está vacía (solo ingredientes activos)
        if (ingredienteService.listarTodos().isEmpty()) {
            System.out.println("Tabla ingredientes vacía. Agregando ingredientes iniciales...");
            
            // Crear ingrediente 1: Leche
            Ingrediente leche = new Ingrediente();
            leche.setNombre("Leche");
            leche.setStockDisponible(5000); // Ya está en gramos
            leche.setPrecioActual(100.0f);
            leche.setActivo(true);
            ingredienteService.guardar(leche);
            
            // Crear ingrediente 2: Harina
            Ingrediente harina = new Ingrediente();
            harina.setNombre("Harina");
            harina.setStockDisponible(10000); // Ya está en gramos
            harina.setPrecioActual(50.0f);
            harina.setActivo(true);
            ingredienteService.guardar(harina);
            
            // Crear ingrediente 3: Huevos
            Ingrediente huevos = new Ingrediente();
            huevos.setNombre("Huevos");
            huevos.setStockDisponible(15000); // Ya está en gramos
            huevos.setPrecioActual(500.0f);
            huevos.setActivo(true);
            ingredienteService.guardar(huevos);
            
            System.out.println("Ingredientes iniciales agregados correctamente.");
        } else {
            System.out.println("La tabla ingredientes ya contiene datos. No se ejecuta la inicialización.");
        }
    }
}
