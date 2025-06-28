package desi.tp.accesoDatos;

import org.springframework.data.jpa.repository.JpaRepository;

import desi.tp.entidades.Receta;

public interface RecetaRepo extends JpaRepository <Receta, Integer>{

}
