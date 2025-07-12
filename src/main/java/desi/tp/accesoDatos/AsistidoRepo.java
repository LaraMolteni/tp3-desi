package desi.tp.accesoDatos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import desi.tp.entidades.Asistido;


public interface AsistidoRepo extends JpaRepository <Asistido, Integer> {

	   List<Asistido> findByDniIn(List<Long> listaDni);
}
