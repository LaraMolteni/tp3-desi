package desi.tp.servicios;

import desi.tp.entidades.Preparacion;
import desi.tp.entidades.Receta;
import desi.tp.exepciones.Excepcion;

public interface StockService {

    void validarStockSuficiente(Receta receta, Integer raciones) throws Excepcion;

    void descontarStock(Preparacion preparacion) throws Excepcion;
}
