package com.helpdeskflow.servicio;

import com.helpdeskflow.dominio.CalculadoraPrioridad;
import com.helpdeskflow.dominio.ValidadorTransiciones;
import com.helpdeskflow.modelo.*;
import com.helpdeskflow.repositorio.RepositorioIncidencias;

/**
 * Servicio de aplicacion: coordina registro (HU-01), calculo de
 * prioridad (HU-02) y el flujo de estados (HU-03).
 */
public class ServicioIncidencias {

    private final RepositorioIncidencias repositorio;
    private final CalculadoraPrioridad calculadora = new CalculadoraPrioridad();
    private final ValidadorTransiciones validador = new ValidadorTransiciones();

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

    /** HU-03: avanza el estado de una incidencia aplicando el flujo valido. */
    public void cambiarEstado(String id, Estado destino) {
        Incidencia incidencia = obtenerObligatoria(id);
        validador.transicionar(incidencia, destino);
        repositorio.guardar(incidencia);
    }

    public void registrarSolucion(String id, String solucion) {
        Incidencia incidencia = obtenerObligatoria(id);
        incidencia.setSolucion(solucion);
        repositorio.guardar(incidencia);
    }

    public Incidencia obtenerObligatoria(String id) {
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe una incidencia con id " + id));
    }
}
