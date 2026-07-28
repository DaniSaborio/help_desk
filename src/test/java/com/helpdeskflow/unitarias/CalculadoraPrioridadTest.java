package com.helpdeskflow.unitarias;

import com.helpdeskflow.dominio.CalculadoraPrioridad;
import com.helpdeskflow.modelo.Impacto;
import com.helpdeskflow.modelo.Prioridad;
import com.helpdeskflow.modelo.Urgencia;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias de la regla HU-02: calculo automatico de prioridad. */
class CalculadoraPrioridadTest {

    private final CalculadoraPrioridad calculadora = new CalculadoraPrioridad();

    @Test
    @DisplayName("Impacto alto y urgencia alta produce prioridad CRITICA")
    void impactoAltoUrgenciaAltaEsCritica() {
        assertEquals(Prioridad.CRITICA, calculadora.calcular(Impacto.ALTO, Urgencia.ALTA));
    }

    @Test
    @DisplayName("Impacto alto con urgencia media o baja produce prioridad ALTA")
    void impactoAltoConUrgenciaMediaOBajaEsAlta() {
        assertEquals(Prioridad.ALTA, calculadora.calcular(Impacto.ALTO, Urgencia.MEDIA));
        assertEquals(Prioridad.ALTA, calculadora.calcular(Impacto.ALTO, Urgencia.BAJA));
    }

    @Test
    @DisplayName("Impacto medio o bajo con urgencia alta produce prioridad ALTA")
    void urgenciaAltaConImpactoMedioOBajoEsAlta() {
        assertEquals(Prioridad.ALTA, calculadora.calcular(Impacto.MEDIO, Urgencia.ALTA));
        assertEquals(Prioridad.ALTA, calculadora.calcular(Impacto.BAJO, Urgencia.ALTA));
    }

    @Test
    @DisplayName("Cualquier otra combinacion produce prioridad NORMAL")
    void otrasCombinacionesSonNormales() {
        assertEquals(Prioridad.NORMAL, calculadora.calcular(Impacto.MEDIO, Urgencia.MEDIA));
        assertEquals(Prioridad.NORMAL, calculadora.calcular(Impacto.MEDIO, Urgencia.BAJA));
        assertEquals(Prioridad.NORMAL, calculadora.calcular(Impacto.BAJO, Urgencia.MEDIA));
        assertEquals(Prioridad.NORMAL, calculadora.calcular(Impacto.BAJO, Urgencia.BAJA));
    }

    @Test
    @DisplayName("Impacto o urgencia nulos lanzan excepcion")
    void valoresNulosLanzanExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> calculadora.calcular(null, Urgencia.ALTA));
        assertThrows(IllegalArgumentException.class, () -> calculadora.calcular(Impacto.ALTO, null));
    }
}
