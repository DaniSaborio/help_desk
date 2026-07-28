package com.helpdeskflow.modelo;

/**
 * Estados del flujo de una incidencia (HU-03).
 * Flujo permitido: REGISTRADA -> LISTA -> EN_DESARROLLO -> EN_VALIDACION -> FINALIZADA
 */
public enum Estado {
    REGISTRADA, LISTA, EN_DESARROLLO, EN_VALIDACION, FINALIZADA
}
