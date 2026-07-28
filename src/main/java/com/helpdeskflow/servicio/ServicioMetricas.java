package com.helpdeskflow.servicio;

import com.helpdeskflow.modelo.Incidencia;
import com.helpdeskflow.modelo.Prioridad;
import com.helpdeskflow.repositorio.RepositorioIncidencias;

import java.time.Duration;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * HU-05: metricas basicas de flujo.
 * Throughput y lead time son metricas propias de Kanban: el sistema que
 * construimos mide lo mismo que medimos en nuestro propio tablero.
 */
public class ServicioMetricas {

    private final RepositorioIncidencias repositorio;

    public ServicioMetricas(RepositorioIncidencias repositorio) {
        this.repositorio = repositorio;
    }

    public long total() {
        return repositorio.obtenerTodas().size();
    }

    public long finalizadas() {
        return repositorio.obtenerTodas().stream().filter(i -> !i.estaAbierta()).count();
    }

    public long abiertas() {
        return repositorio.obtenerTodas().stream().filter(Incidencia::estaAbierta).count();
    }

    /** Throughput: incidencias terminadas durante el periodo registrado. */
    public long throughput() {
        return finalizadas();
    }

    /** Lead time promedio en horas de las incidencias terminadas. */
    public double leadTimePromedioHoras() {
        List<Incidencia> terminadas = repositorio.obtenerTodas().stream()
                .filter(i -> i.getFechaCierre() != null)
                .toList();
        if (terminadas.isEmpty()) return 0.0;
        double totalHoras = terminadas.stream()
                .mapToDouble(i -> Duration.between(i.getFechaCreacion(), i.getFechaCierre()).toMillis() / 3_600_000.0)
                .sum();
        return totalHoras / terminadas.size();
    }

    public Map<Prioridad, Long> porPrioridad() {
        Map<Prioridad, Long> conteo = new EnumMap<>(Prioridad.class);
        for (Prioridad p : Prioridad.values()) conteo.put(p, 0L);
        repositorio.obtenerTodas().forEach(i ->
                conteo.merge(i.getPrioridad(), 1L, Long::sum));
        return conteo;
    }
}
