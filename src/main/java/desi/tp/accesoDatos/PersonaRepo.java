package desi.tp.accesoDatos;

import org.springframework.data.jpa.repository.JpaRepository;

import desi.tp.entidades.Persona;

public interface PersonaRepo extends JpaRepository <Persona, Integer> {

}
