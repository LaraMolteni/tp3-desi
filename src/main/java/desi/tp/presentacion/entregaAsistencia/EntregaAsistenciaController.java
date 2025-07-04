package desi.tp.presentacion.entregaAsistencia;

import java.util.List;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.RequestParam;

import desi.tp.entidades.EntregaAsistencia;
import desi.tp.exepciones.Excepcion;
import desi.tp.servicios.EntregaAsistenciaService;


@Controller
@RequestMapping("/entregaAsistencia")
public class EntregaAsistenciaController {
    @Autowired
    private EntregaAsistenciaService entregaAsistenciaService;

    // *** NUEVO ENDPOINT AGREGADO PARA EL ALTA DE ENTREGA DE RACIONES ***
    @PostMapping("/registrar") // Un path específico para la operación de registro
    public ResponseEntity<EntregaAsistencia> registrarEntrega(@RequestBody RegistrarEntregaRequestDTO request) {
        try {
            // Llama al método del servicio que tiene toda la lógica de negocio
            EntregaAsistencia nuevaEntrega = entregaAsistenciaService.registrarEntrega(
                request.getIdFamilia(),
                request.getIdPreparacion(),
                request.getCantidadRaciones(),
                request.getIdVoluntario()
            );
            return new ResponseEntity<>(nuevaEntrega, HttpStatus.CREATED); 
        } catch (Excepcion e) {
            // Si el servicio lanza una Excepcion (tu excepción personalizada)
            // se mapea a un HTTP 400 Bad Request con el mensaje de error
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            // Para cualquier otra excepción inesperada, un 500 Internal Server Error
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado al registrar la entrega.", e);
        }
    }
    // *********************************************************


    @PostMapping // Este endpoint ahora sería más para un "crear entrega básica"
    public EntregaAsistencia crearEntrega(@RequestBody EntregaAsistencia entrega) throws Excepcion{
        if (entrega.getFamilia() == null || entrega.getCantidadRaciones() == null || entrega.getCantidadRaciones() <= 0) {
            throw new IllegalArgumentException("Familia y cantidad de raciones son requeridos");
        }
        
        return entregaAsistenciaService.crearEntrega(entrega);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntregaAsistencia> modificarEntrega(@PathVariable Integer id, @RequestBody EntregaAsistencia datos) {
    	EntregaAsistencia actualizado = entregaAsistenciaService.modificarEntrega(id, datos);
        if (actualizado != null) {
            return ResponseEntity.ok(actualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public List<EntregaAsistencia> listarRegistros() {
        return entregaAsistenciaService.listarEntregas();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEntrega(@PathVariable Integer id) {
    	entregaAsistenciaService.eliminarEntrega(id);
        return ResponseEntity.noContent().build();
    }
    
 // *** ENDPOINT PARA EL LISTADO FILTRADO ***
   
    @GetMapping("/filtrar") // Un nuevo path más específico para el filtrado
    public List<EntregaAsistenciaListadoDTO> listarEntregasFiltradas(
            @RequestParam(required = false) LocalDate fecha, // @RequestParam para parámetros de URL
            @RequestParam(required = false) Integer idFamilia,
            @RequestParam(required = false) String nombreFamilia) {

        // Llama al nuevo método del servicio que realiza el filtrado
        List<EntregaAsistencia> entregasFiltradas = entregaAsistenciaService.filtrarEntregas(fecha, idFamilia, nombreFamilia);

        // Mapea los resultados filtrados al DTO de listado existente
        return entregasFiltradas.stream().map(r -> {
        	EntregaAsistenciaListadoDTO dto = new EntregaAsistenciaListadoDTO();
            if (r.getFamilia() != null) {
                dto.setNroFamilia(r.getFamilia().getIdFamilia());
                dto.setNombreFamilia(r.getFamilia().getNombre());
            }
            dto.setFechaEntrega(r.getFecha());
            if (r.getPreparacion() != null && r.getPreparacion().getReceta() != null) { // Agregado null check para receta
                dto.setNombrePlato(r.getPreparacion().getReceta().getNombre());
            }
            dto.setCantidadRaciones(r.getCantidadRaciones());
            return dto;
        }).toList();
    }

    @GetMapping("/listado-ext")
    public List<EntregaAsistenciaListadoDTO> listadoExtendido() {
        return entregaAsistenciaService.listarEntregas().stream().map(r -> {
        	EntregaAsistenciaListadoDTO dto = new EntregaAsistenciaListadoDTO();
            if (r.getFamilia() != null) {
                dto.setNroFamilia(r.getFamilia().getIdFamilia());
                dto.setNombreFamilia(r.getFamilia().getNombre());
            }
            dto.setFechaEntrega(r.getFecha());
            if (r.getPreparacion() != null) {
                dto.setNombrePlato(r.getPreparacion().getReceta().getNombre());
            }
            dto.setCantidadRaciones(r.getCantidadRaciones());
            return dto;
        }).toList();
    }
}








/*package desi.tp.presentacion.entregaAsistencia;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import desi.tp.entidades.EntregaAsistencia;
import desi.tp.exepciones.Excepcion;
import desi.tp.servicios.EntregaAsistenciaService;


@Controller
@RequestMapping("/entregaAsistencia")
public class EntregaAsistenciaController {
    @Autowired
    private EntregaAsistenciaService entregaAsistenciaService;

    @PostMapping
    public EntregaAsistencia crearEntrega(@RequestBody EntregaAsistencia entrega) throws Excepcion{
        if (entrega.getFamilia() == null || entrega.getCantidadRaciones() == null || entrega.getCantidadRaciones() <= 0) {
            throw new IllegalArgumentException("Familia y cantidad de raciones son requeridos");
        }
        
        return entregaAsistenciaService.crearEntrega(entrega);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntregaAsistencia> modificarEntrega(@PathVariable Integer id, @RequestBody EntregaAsistencia datos) {
    	EntregaAsistencia actualizado = entregaAsistenciaService.modificarEntrega(id, datos);
        if (actualizado != null) {
            return ResponseEntity.ok(actualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public List<EntregaAsistencia> listarRegistros() {
        return entregaAsistenciaService.listarEntregas();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEntrega(@PathVariable Integer id) {
    	entregaAsistenciaService.eliminarEntrega(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint para listado extendido (DTO) con columnas requeridas
    @GetMapping("/listado-ext")
    public List<EntregaAsistenciaListadoDTO> listadoExtendido() {
        return entregaAsistenciaService.listarEntregas().stream().map(r -> {
        	EntregaAsistenciaListadoDTO dto = new EntregaAsistenciaListadoDTO();
            if (r.getFamilia() != null) {
                dto.setNroFamilia(r.getFamilia().getIdFamilia());
                dto.setNombreFamilia(r.getFamilia().getNombre());
            }
            dto.setFechaEntrega(r.getFecha());
            if (r.getPreparacion() != null) {
                dto.setNombrePlato(r.getPreparacion().getReceta().getNombre());
            }
            dto.setCantidadRaciones(r.getCantidadRaciones());
            return dto;
        }).toList();
    }
}
*/