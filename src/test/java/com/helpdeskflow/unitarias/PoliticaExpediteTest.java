package com.helpdeskflow.unitarias;

import com.helpdeskflow.modelo.*;
import com.helpdeskflow.repositorio.RepositorioEnMemoria;
import com.helpdeskflow.servicio.ServicioIncidencias;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas especificas del cambio de requerimiento EXPEDITE. */
class PoliticaExpediteTest {

    private ServicioIncidencias servicio;

    @BeforeEach
    void preparar() {
        servicio = new ServicioIncidencias(new RepositorioEnMemoria());
    }

    private Incidencia critica(String titulo) {
        return servicio.registrar(titulo, "Descripcion de incidencia critica de prueba",
                "Infraestructura", Impacto.ALTO, Urgencia.ALTA);
    }

    @Test
    @DisplayName("Una incidencia critica puede marcarse como EXPEDITE")
    void criticaPuedeMarcarseExpedite() {
        Incidencia i = critica("Servidor caido");
        servicio.marcarExpedite(i.getId());
        assertEquals(ClaseServicio.EXPEDITE, i.getClaseServicio());
    }

    @Test
    @DisplayName("Una incidencia no critica no puede marcarse como EXPEDITE")
    void noCriticaNoPuedeMarcarseExpedite() {
        Incidencia normal = servicio.registrar("Tinta baja", "La impresora reporta tinta baja",
                "Hardware", Impacto.BAJO, Urgencia.BAJA);
        assertThrows(IllegalStateException.class, () -> servicio.marcarExpedite(normal.getId()));
    }

    @Test
    @DisplayName("Solo puede existir una EXPEDITE en desarrollo o validacion simultaneamente")
    void soloUnaExpediteActivaALaVez() {
        Incidencia primera = critica("Caida total del sistema");
        Incidencia segunda = critica("Base de datos corrupta");
        servicio.marcarExpedite(primera.getId());
        servicio.marcarExpedite(segunda.getId());

        servicio.cambiarEstado(primera.getId(), Estado.LISTA);
        servicio.cambiarEstado(primera.getId(), Estado.EN_DESARROLLO);

        servicio.cambiarEstado(segunda.getId(), Estado.LISTA);
        IllegalStateException e = assertThrows(IllegalStateException.class, () ->
                servicio.cambiarEstado(segunda.getId(), Estado.EN_DESARROLLO));
        assertTrue(e.getMessage().contains("EXPEDITE"));
    }

    @Test
    @DisplayName("Al finalizar la EXPEDITE activa, otra puede entrar en desarrollo")
    void alFinalizarUnaExpeditePuedeEntrarOtra() {
        Incidencia primera = critica("Caida total del sistema");
        Incidencia segunda = critica("Base de datos corrupta");
        servicio.marcarExpedite(primera.getId());
        servicio.marcarExpedite(segunda.getId());

        servicio.cambiarEstado(primera.getId(), Estado.LISTA);
        servicio.cambiarEstado(primera.getId(), Estado.EN_DESARROLLO);
        servicio.cambiarEstado(primera.getId(), Estado.EN_VALIDACION);
        servicio.registrarSolucion(primera.getId(), "Se restauro el cluster desde respaldo");
        servicio.cambiarEstado(primera.getId(), Estado.FINALIZADA);

        servicio.cambiarEstado(segunda.getId(), Estado.LISTA);
        assertDoesNotThrow(() -> servicio.cambiarEstado(segunda.getId(), Estado.EN_DESARROLLO));
    }
}
