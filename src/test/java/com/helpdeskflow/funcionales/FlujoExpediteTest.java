package com.helpdeskflow.funcionales;

import com.helpdeskflow.modelo.*;
import com.helpdeskflow.repositorio.RepositorioEnMemoria;
import com.helpdeskflow.servicio.ServicioIncidencias;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba funcional 2: escenario completo del cambio de requerimiento EXPEDITE
 * con varias incidencias conviviendo en el sistema, incluyendo el limite de
 * una sola EXPEDITE activa y la liberacion del carril al finalizar.
 */
class FlujoExpediteTest {

    @Test
    @DisplayName("Escenario EXPEDITE: marcado, limite de una activa y liberacion del carril")
    void escenarioExpediteCompleto() {
        ServicioIncidencias servicio = new ServicioIncidencias(new RepositorioEnMemoria());

        // Conviven incidencias normales y criticas
        Incidencia normal = servicio.registrar("Solicitud de teclado",
                "Se solicita un teclado nuevo para recepcion", "Hardware", Impacto.BAJO, Urgencia.BAJA);
        Incidencia caidaTotal = servicio.registrar("Caida total de la red",
                "Ninguna estacion tiene acceso a la red interna", "Red", Impacto.ALTO, Urgencia.ALTA);
        Incidencia baseDatos = servicio.registrar("Corrupcion de base de datos",
                "La base de datos principal reporta paginas corruptas", "Infraestructura", Impacto.ALTO, Urgencia.ALTA);

        // La incidencia normal no puede ser EXPEDITE
        assertThrows(IllegalStateException.class, () -> servicio.marcarExpedite(normal.getId()));

        // Ambas criticas se marcan EXPEDITE, pero solo una puede estar activa
        servicio.marcarExpedite(caidaTotal.getId());
        servicio.marcarExpedite(baseDatos.getId());

        servicio.cambiarEstado(caidaTotal.getId(), Estado.LISTA);
        servicio.cambiarEstado(caidaTotal.getId(), Estado.EN_DESARROLLO);

        servicio.cambiarEstado(baseDatos.getId(), Estado.LISTA);
        assertThrows(IllegalStateException.class, () ->
                servicio.cambiarEstado(baseDatos.getId(), Estado.EN_DESARROLLO));

        // La incidencia normal fluye sin verse afectada por la politica
        servicio.cambiarEstado(normal.getId(), Estado.LISTA);
        assertDoesNotThrow(() -> servicio.cambiarEstado(normal.getId(), Estado.EN_DESARROLLO));

        // Al finalizar la EXPEDITE activa se libera el carril
        servicio.cambiarEstado(caidaTotal.getId(), Estado.EN_VALIDACION);
        servicio.registrarSolucion(caidaTotal.getId(), "Se reemplazo el switch principal danado");
        servicio.cambiarEstado(caidaTotal.getId(), Estado.FINALIZADA);

        assertDoesNotThrow(() -> servicio.cambiarEstado(baseDatos.getId(), Estado.EN_DESARROLLO));
        assertEquals(Estado.EN_DESARROLLO, servicio.obtenerObligatoria(baseDatos.getId()).getEstado());
    }
}
