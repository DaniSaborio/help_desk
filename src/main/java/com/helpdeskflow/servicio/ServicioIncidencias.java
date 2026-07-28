package com.helpdeskflow.servicio;

import com.helpdeskflow.dominio.CalculadoraPrioridad;
import com.helpdeskflow.dominio.PoliticaExpedite;
import com.helpdeskflow.dominio.ValidadorTransiciones;
import com.helpdeskflow.modelo.*;
import com.helpdeskflow.repositorio.RepositorioIncidencias;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de aplicacion: coordina registro (HU-01), calculo de prioridad
 * (HU-02), flujo de estados (HU-03), consultas y filtros (HU-04) y la
 * politica EXPEDITE (cambio de requerimiento).
 */
public class ServicioIncidencias {

    private final RepositorioIncidencias repositorio;
    private final CalculadoraPrioridad calculadora = new CalculadoraPrioridad();
    private final ValidadorTransiciones validador = new ValidadorTransiciones();
    private final PoliticaExpedite politicaExpedite = new PoliticaExpedite();

    public ServicioIncidencias(RepositorioIncidencias repositorio) {
        this.repositorio = repositorio;
    }

    /** HU-01: registra una incidencia validando sus criterios de aceptacion. */
    public Incidencia registrar(String titulo, String descripcion, String categoria,
                                Impacto impacto, Urgencia urgencia) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El titulo no puede estar vacio");
        }
        if (descripcion == null || descripcion.trim().length() < 10) {
            throw new IllegalArgumentException("La descripcion debe contener al menos diez caracteres");
        }
        if (impacto == null) {
            throw new IllegalArgumentException("El impacto debe ser BAJO, MEDIO o ALTO");
        }
        if (urgencia == null) {
            throw new IllegalArgumentException("La urgencia debe ser BAJA, MEDIA o ALTA");
        }
        Prioridad prioridad = calculadora.calcular(impacto, urgencia);
        Incidencia incidencia = new Incidencia(titulo, descripcion, categoria, impacto, urgencia, prioridad);
        repositorio.guardar(incidencia);
        return incidencia;
    }

    /** HU-03: avanza el estado de una incidencia aplicando todas las politicas. */
    public void cambiarEstado(String id, Estado destino) {
        Incidencia incidencia = obtenerObligatoria(id);
        politicaExpedite.validarTransicion(incidencia, destino, repositorio.obtenerTodas());
        validador.transicionar(incidencia, destino);
        repositorio.guardar(incidencia);
    }

    public void registrarSolucion(String id, String solucion) {
        Incidencia incidencia = obtenerObligatoria(id);
        incidencia.setSolucion(solucion);
        repositorio.guardar(incidencia);
    }

    /** Cambio de requerimiento: marca una incidencia critica como EXPEDITE. */
    public void marcarExpedite(String id) {
        Incidencia incidencia = obtenerObligatoria(id);
        politicaExpedite.validarMarcado(incidencia);
        incidencia.setClaseServicio(ClaseServicio.EXPEDITE);
        repositorio.guardar(incidencia);
    }

    // ===== HU-04: consultas y filtros =====

    public List<Incidencia> obtenerTodas() {
        return repositorio.obtenerTodas();
    }

    public Incidencia obtenerObligatoria(String id) {
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe una incidencia con id " + id));
    }

    public List<Incidencia> filtrarPorEstado(Estado estado) {
        return filtrar(i -> i.getEstado() == estado);
    }

    public List<Incidencia> filtrarPorPrioridad(Prioridad prioridad) {
        return filtrar(i -> i.getPrioridad() == prioridad);
    }

    public List<Incidencia> obtenerAbiertas() {
        return filtrar(Incidencia::estaAbierta);
    }

    public List<Incidencia> obtenerFinalizadas() {
        return filtrar(i -> !i.estaAbierta());
    }

    private List<Incidencia> filtrar(java.util.function.Predicate<Incidencia> criterio) {
        return repositorio.obtenerTodas().stream()
                .filter(criterio)
                .collect(Collectors.toList());
    }
}
