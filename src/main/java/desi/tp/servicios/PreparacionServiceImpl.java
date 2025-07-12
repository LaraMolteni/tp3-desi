package desi.tp.servicios;

import desi.tp.accesoDatos.PreparacionRepo;
import desi.tp.entidades.Preparacion;
import desi.tp.entidades.Receta;
import desi.tp.exepciones.Excepcion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Service
public class PreparacionServiceImpl implements PreparacionService {

    @Autowired
    private PreparacionRepo preparacionRepo;

    @Autowired
    private RecetaService recetaService;

    @Autowired
    private StockService stockService;

    @Override
    public List<Preparacion> listarPreparacionesActivas() {
        // Solo mostrar preparaciones activas con stock de raciones restantes > 0
        return preparacionRepo.findByActivaTrue().stream()
            .filter(p -> p.getStockRacionesRestantes() != null && p.getStockRacionesRestantes() > 0)
            .toList();
    }
    
    @Override
    public List<Preparacion> filtrar(String nombreReceta, LocalDate fecha) {
        return preparacionRepo.findAll().stream()
            .filter(p -> p.isActiva())
            .filter(p -> nombreReceta == null || p.getReceta().getNombre().toLowerCase().contains(nombreReceta.toLowerCase()))
            .filter(p -> fecha == null || p.getFechaCoccion().equals(fecha))
            .toList();
    }


    @Override
    public Preparacion buscarPorId(Integer id) {
        return preparacionRepo.findById(id)
                .orElseThrow(() -> new Excepcion("Preparación no encontrada."));
    }

    @Override
    public void crearPreparacion(Preparacion preparacion)  {
        validarPreparacion(preparacion);
        // Inicializa el stock de raciones restantes al total preparado
        preparacion.setStockRacionesRestantes(preparacion.getTotalRacionesPreparadas());
        stockService.descontarStock(preparacion);
        preparacionRepo.save(preparacion);
    }

    @Override
    public void eliminarPreparacion(Integer id) {
        Preparacion p = buscarPorId(id);
        p.setActiva(false);
        preparacionRepo.save(p);
    }

    @Override
    public void modificarFecha(Integer id, LocalDate nuevaFecha)  {
        if (nuevaFecha.isAfter(LocalDate.now())) {
            throw new Excepcion("La fecha no puede ser futura.");
        }
        Preparacion p = buscarPorId(id);
        p.setFechaCoccion(nuevaFecha);
        preparacionRepo.save(p);
    }
    @Override
    public Preparacion guardar(Preparacion preparacion) {
        // Guarda o actualiza la preparación existente en la base de datos
        return preparacionRepo.save(preparacion);
    }

    private void validarPreparacion(Preparacion preparacion) {
        if (preparacion.getFechaCoccion() == null || preparacion.getFechaCoccion().isAfter(LocalDate.now())) {
            throw new Excepcion("La fecha es obligatoria y no puede ser futura.");
        }

        if (preparacion.getReceta() == null || preparacion.getReceta().getId() == null) {
            throw new Excepcion("Debe seleccionar una receta.");
        }

        if (preparacion.getTotalRacionesPreparadas() == null || preparacion.getTotalRacionesPreparadas() <= 0) {
            throw new Excepcion("Debe indicar una cantidad de raciones válida.");
        }

        Receta receta = recetaService.buscarPorId(preparacion.getReceta().getId());

        Optional<Preparacion> existente = preparacionRepo.findByFechaCoccionAndRecetaAndActivaTrue(
                preparacion.getFechaCoccion(), receta);
        if (existente.isPresent()) {
            throw new Excepcion("Ya existe una preparación para esa receta en esa fecha.");
        }

        stockService.validarStockSuficiente(receta, preparacion.getTotalRacionesPreparadas());

        preparacion.setReceta(receta);
    }
}
