package com.helpdeskflow.repositorio;

import com.helpdeskflow.modelo.Incidencia;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Implementacion en memoria del repositorio de incidencias. */
public class RepositorioEnMemoria implements RepositorioIncidencias {

    private final Map<String, Incidencia> almacen = new LinkedHashMap<>();

    @Override
    public void guardar(Incidencia incidencia) {
        almacen.put(incidencia.getId(), incidencia);
    }

    @Override
    public Optional<Incidencia> buscarPorId(String id) {
        // Permite buscar por id completo o por el prefijo corto mostrado en consola
        Incidencia exacta = almacen.get(id);
        if (exacta != null) return Optional.of(exacta);
        return almacen.values().stream()
                .filter(i -> i.getId().startsWith(id))
                .findFirst();
    }

    @Override
    public List<Incidencia> obtenerTodas() {
        return new ArrayList<>(almacen.values());
    }
}
