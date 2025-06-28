package desi.tp.accesoDatos;

import org.springframework.data.jpa.repository.JpaRepository;

import desi.tp.entidades.Condimento;

public interface CondimentoRepo extends JpaRepository <Condimento, Integer>{

}
