package desi.tp.accesoDatos;

import org.springframework.data.jpa.repository.JpaRepository;

import desi.tp.entidades.Producto;

public interface ProductoRepo extends JpaRepository<Producto, Integer> {

}
