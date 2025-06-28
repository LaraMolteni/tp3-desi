package desi.tp.accesoDatos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import desi.tp.entidades.Familia;


public interface FamiliaRepo extends JpaRepository <Familia, Integer>{

	 // Buscar por número exacto y nombre que contenga texto (case insensitive)
    List<Familia> findByIdFamiliaAndNombreContainingIgnoreCase(Integer idFamilia, String nombre);
	
}
