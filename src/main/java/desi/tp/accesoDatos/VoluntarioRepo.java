package desi.tp.accesoDatos;

import org.springframework.data.jpa.repository.JpaRepository;

import desi.tp.entidades.Voluntario;

public interface VoluntarioRepo extends JpaRepository<Voluntario, Integer> {

}
