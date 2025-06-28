package desi.tp.accesoDatos;

import org.springframework.data.jpa.repository.JpaRepository;

import desi.tp.entidades.Ingrediente;

public interface IngredienteRepo extends JpaRepository<Ingrediente, Integer> {

}
