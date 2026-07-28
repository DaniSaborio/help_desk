package com.helpdeskflow.unitarias;

import com.helpdeskflow.dominio.ValidadorTransiciones;
import com.helpdeskflow.modelo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias de HU-03: transiciones validas e invalidas. */
class TransicionesEstadoTest {

    private ValidadorTransiciones validador;
    private Incidencia incidencia;

    @BeforeEach
    void preparar() {
        validador = new ValidadorTransiciones();
        incidencia = new Incidencia("Falla correo", "El correo institucional rebota mensajes",
                "Software", Impacto.MEDIO, Urgencia.MEDIA, Prioridad.NORMAL);
    }

    @Test
    @DisplayName("El flujo completo REGISTRADA -> LISTA -> EN_DESARROLLO -> EN_VALIDACION -> FINALIZADA es valido")
    void flujoCompletoEsValido() {
        validador.transicionar(incidencia, Estado.LISTA);
        validador.transicionar(incidencia, Estado.EN_DESARROLLO);
        validador.transicionar(incidencia, Estado.EN_VALIDACION);
        incidencia.setSolucion("Se corrigio el registro MX del dominio");
        validador.transicionar(incidencia, Estado.FINALIZADA);

        assertEquals(Estado.FINALIZADA, incidencia.getEstado());
        assertNotNull(incidencia.getFechaCierre());
    }

    @Test
    @DisplayName("REGISTRADA -> FINALIZADA es una transicion invalida")
    void saltoDirectoAFinalizadaEsInvalido() {
        IllegalStateException e = assertThrows(IllegalStateException.class, () ->
                validador.transicionar(incidencia, Estado.FINALIZADA));
        assertTrue(e.getMessage().contains("Transicion invalida"));
    }

    @Test
    @DisplayName("FINALIZADA -> EN_DESARROLLO (retroceso) es una transicion invalida")
    void retrocesoDesdeFinalizadaEsInvalido() {
        validador.transicionar(incidencia, Estado.LISTA);
        validador.transicionar(incidencia, Estado.EN_DESARROLLO);
        validador.transicionar(incidencia, Estado.EN_VALIDACION);
        incidencia.setSolucion("Reinicio del servicio de correo");
        validador.transicionar(incidencia, Estado.FINALIZADA);

        assertThrows(IllegalStateException.class, () ->
                validador.transicionar(incidencia, Estado.EN_DESARROLLO));
    }

    @Test
    @DisplayName("No puede finalizarse una incidencia sin descripcion de la solucion")
    void noPuedeCerrarseSinSolucion() {
        validador.transicionar(incidencia, Estado.LISTA);
        validador.transicionar(incidencia, Estado.EN_DESARROLLO);
        validador.transicionar(incidencia, Estado.EN_VALIDACION);

        IllegalStateException e = assertThrows(IllegalStateException.class, () ->
                validador.transicionar(incidencia, Estado.FINALIZADA));
        assertTrue(e.getMessage().contains("sin descripcion de la solucion"));
        assertEquals(Estado.EN_VALIDACION, incidencia.getEstado());
    }
}
