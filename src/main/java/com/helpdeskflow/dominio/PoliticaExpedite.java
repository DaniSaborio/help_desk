package com.helpdeskflow.dominio;

import com.helpdeskflow.modelo.Estado;
import com.helpdeskflow.modelo.Incidencia;
import com.helpdeskflow.modelo.Prioridad;

import java.util.Collection;

/**
 * Cambio de requerimiento: clase de servicio EXPEDITE.
 *
 * Reglas:
 *  1. Solo una incidencia CRITICA puede marcarse como EXPEDITE.
 *  2. Solo puede existir UNA incidencia EXPEDITE en desarrollo o validacion
 *     de forma simultanea.
 *
 * La regla se implemento como politica independiente para no modificar el
 * comportamiento previamente validado del validador de transiciones
 * (principio abierto/cerrado, ver docs/REFACTORIZACION.md).
 */
public class PoliticaExpedite {

    public void validarMarcado(Incidencia incidencia) {
        if (incidencia.getPrioridad() != Prioridad.CRITICA) {
            throw new IllegalStateException(
                    "Solo una incidencia CRITICA puede marcarse como EXPEDITE");
        }
    }

    /**
     * Verifica que al mover una incidencia EXPEDITE hacia EN_DESARROLLO o
     * EN_VALIDACION no exista otra EXPEDITE ocupando esas etapas.
     */
    public void validarTransicion(Incidencia incidencia, Estado destino,
                                  Collection<Incidencia> todas) {
        if (!incidencia.esExpedite()) return;
        if (destino != Estado.EN_DESARROLLO && destino != Estado.EN_VALIDACION) return;

        boolean existeOtraExpediteActiva = todas.stream()
                .filter(i -> !i.getId().equals(incidencia.getId()))
                .filter(Incidencia::esExpedite)
                .anyMatch(i -> i.getEstado() == Estado.EN_DESARROLLO
                        || i.getEstado() == Estado.EN_VALIDACION);

        if (existeOtraExpediteActiva) {
            throw new IllegalStateException(
                    "Ya existe una incidencia EXPEDITE en desarrollo o validacion");
        }
    }
}
