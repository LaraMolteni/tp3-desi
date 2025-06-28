package desi.tp.presentacion.entregaAsistencia;

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
