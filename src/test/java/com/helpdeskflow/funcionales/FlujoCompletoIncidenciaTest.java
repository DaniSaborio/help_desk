package com.helpdeskflow.funcionales;

import com.helpdeskflow.modelo.*;
import com.helpdeskflow.repositorio.RepositorioEnMemoria;
import com.helpdeskflow.servicio.ServicioIncidencias;
import com.helpdeskflow.servicio.ServicioMetricas;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba funcional 1: ciclo de vida completo de una incidencia, desde el
 * registro hasta el cierre, verificando el efecto sobre consultas y metricas.
 * Simula el uso real del sistema por un encargado de soporte.
 */
class FlujoCompletoIncidenciaTest {

    @Test
    @DisplayName("Ciclo de vida completo: registro, priorizacion, atencion, validacion y cierre")
    void cicloDeVidaCompleto() {
        RepositorioEnMemoria repositorio = new RepositorioEnMemoria();
        ServicioIncidencias servicio = new ServicioIncidencias(repositorio);
        ServicioMetricas metricas = new ServicioMetricas(repositorio);

        // 1. El usuario registra la incidencia
        Incidencia incidencia = servicio.registrar(
                "Caida del sistema de facturacion",
                "El sistema de facturacion no permite emitir comprobantes desde las 8 a.m.",
                "Software", Impacto.ALTO, Urgencia.ALTA);

        // 2. El sistema calcula la prioridad sin intervencion humana
        assertEquals(Prioridad.CRITICA, incidencia.getPrioridad());
        assertEquals(Estado.REGISTRADA, incidencia.getEstado());

        // 3. El tecnico avanza la incidencia por el flujo permitido
        servicio.cambiarEstado(incidencia.getId(), Estado.LISTA);
        servicio.cambiarEstado(incidencia.getId(), Estado.EN_DESARROLLO);
        servicio.cambiarEstado(incidencia.getId(), Estado.EN_VALIDACION);

        // 4. Intento de cierre sin solucion: el sistema lo impide
        assertThrows(IllegalStateException.class, () ->
                servicio.cambiarEstado(incidencia.getId(), Estado.FINALIZADA));

        // 5. Se documenta la solucion y se cierra correctamente
        servicio.registrarSolucion(incidencia.getId(),
                "Se reinicio el servicio de facturacion y se corrigio el certificado vencido");
        servicio.cambiarEstado(incidencia.getId(), Estado.FINALIZADA);

        // 6. Consultas y metricas reflejan el estado real
        assertEquals(Estado.FINALIZADA, servicio.obtenerObligatoria(incidencia.getId()).getEstado());
        assertNotNull(incidencia.getFechaCierre());
        assertTrue(servicio.obtenerAbiertas().isEmpty());
        assertEquals(1, servicio.obtenerFinalizadas().size());
        assertEquals(1, metricas.throughput());
    }
}
