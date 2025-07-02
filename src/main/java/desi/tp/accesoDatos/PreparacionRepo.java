package desi.tp.accesoDatos;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import desi.tp.entidades.Preparacion;
import desi.tp.entidades.Receta;

public interface PreparacionRepo extends JpaRepository<Preparacion, Integer>{
	 // Buscar preparaciones activas
    List<Preparacion> findByActivaTrue();

    // Buscar por receta y fecha (para validar duplicados)
    Optional<Preparacion> findByFechaCoccionAndRecetaAndActivaTrue(LocalDate fecha, Receta receta);

}
