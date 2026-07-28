package com.helpdeskflow.servicio;

import com.helpdeskflow.dominio.CalculadoraPrioridad;
import com.helpdeskflow.modelo.*;
import com.helpdeskflow.repositorio.RepositorioIncidencias;

/**
 * Servicio de aplicacion: coordina registro (HU-01) y calculo de
 * prioridad (HU-02).
 */
public class ServicioIncidencias {

    private final RepositorioIncidencias repositorio;
    private final CalculadoraPrioridad calculadora = new CalculadoraPrioridad();

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
}
