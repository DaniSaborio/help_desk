package com.helpdeskflow.dominio;

import com.helpdeskflow.modelo.Impacto;
import com.helpdeskflow.modelo.Prioridad;
import com.helpdeskflow.modelo.Urgencia;

/**
 * Regla de negocio HU-02: calculo automatico de la prioridad.
 *
 * Impacto ALTO + Urgencia ALTA                  -> CRITICA
 * Impacto ALTO + Urgencia MEDIA o BAJA          -> ALTA
 * Impacto MEDIO o BAJO + Urgencia ALTA          -> ALTA
 * Cualquier otra combinacion                    -> NORMAL
 *
 * Esta clase existe como resultado de la refactorizacion documentada en
 * docs/REFACTORIZACION.md: el calculo se extrajo del servicio de incidencias
 * para separar responsabilidades y facilitar las pruebas unitarias.
 */
public class CalculadoraPrioridad {

    public Prioridad calcular(Impacto impacto, Urgencia urgencia) {
        if (impacto == null || urgencia == null) {
            throw new IllegalArgumentException("El impacto y la urgencia son obligatorios");
        }
        boolean impactoAlto = impacto == Impacto.ALTO;
        boolean urgenciaAlta = urgencia == Urgencia.ALTA;

        if (impactoAlto && urgenciaAlta) return Prioridad.CRITICA;
        if (impactoAlto || urgenciaAlta) return Prioridad.ALTA;
        return Prioridad.NORMAL;
    }
}
