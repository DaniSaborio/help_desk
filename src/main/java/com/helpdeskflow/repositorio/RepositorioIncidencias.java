package com.helpdeskflow.repositorio;

import com.helpdeskflow.modelo.Incidencia;

import java.util.List;
import java.util.Optional;

/**
 * Abstraccion de persistencia. La implementacion actual es en memoria,
 * pero la interfaz permite sustituirla por una base de datos sin tocar
 * los servicios (inversion de dependencias).
 */
public interface RepositorioIncidencias {
    void guardar(Incidencia incidencia);
    Optional<Incidencia> buscarPorId(String id);
    List<Incidencia> obtenerTodas();
}
