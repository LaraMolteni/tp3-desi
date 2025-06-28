package desi.tp.accesoDatos;

import org.springframework.data.jpa.repository.JpaRepository;

import desi.tp.entidades.ItemReceta;

public interface ItemRecetaRepo extends JpaRepository<ItemReceta, Integer> {

}