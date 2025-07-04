package desi.tp.accesoDatos;



import org.springframework.data.jpa.repository.JpaRepository;

import desi.tp.entidades.EntregaAsistencia;
import desi.tp.entidades.Familia;
import java.time.LocalDate;
import java.util.List;


public interface EntregaAsistenciaRepo extends JpaRepository <EntregaAsistencia, Integer> {
	
	// Método para buscar si ya existe una entrega para una familia en una fecha específica
	List<EntregaAsistencia> findByFamiliaAndFecha(Familia familia, LocalDate fecha);
}
