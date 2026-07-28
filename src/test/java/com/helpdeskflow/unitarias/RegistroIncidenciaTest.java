package com.helpdeskflow.unitarias;

import com.helpdeskflow.modelo.*;
import com.helpdeskflow.repositorio.RepositorioEnMemoria;
import com.helpdeskflow.servicio.ServicioIncidencias;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias de HU-01: registro y criterios de aceptacion. */
class RegistroIncidenciaTest {

    private ServicioIncidencias servicio;

    @BeforeEach
    void preparar() {
        servicio = new ServicioIncidencias(new RepositorioEnMemoria());
    }

    @Test
    @DisplayName("El titulo no puede estar vacio")
    void tituloVacioEsRechazado() {
        assertThrows(IllegalArgumentException.class, () ->
                servicio.registrar("", "Descripcion suficientemente larga", "Red", Impacto.ALTO, Urgencia.ALTA));
        assertThrows(IllegalArgumentException.class, () ->
                servicio.registrar("   ", "Descripcion suficientemente larga", "Red", Impacto.ALTO, Urgencia.ALTA));
    }

    @Test
    @DisplayName("La descripcion debe contener al menos diez caracteres")
    void descripcionCortaEsRechazada() {
        assertThrows(IllegalArgumentException.class, () ->
                servicio.registrar("Falla VPN", "corta", "Red", Impacto.ALTO, Urgencia.ALTA));
    }

    @Test
    @DisplayName("El sistema genera un identificador unico y estado inicial REGISTRADA")
    void generaIdentificadorUnicoYEstadoInicial() {
        Incidencia a = servicio.registrar("Falla VPN", "La VPN corporativa no conecta", "Red", Impacto.ALTO, Urgencia.ALTA);
        Incidencia b = servicio.registrar("Impresora", "La impresora del piso 2 no imprime", "Hardware", Impacto.BAJO, Urgencia.BAJA);

        assertNotNull(a.getId());
        assertNotEquals(a.getId(), b.getId());
        assertEquals(Estado.REGISTRADA, a.getEstado());
        assertNotNull(a.getFechaCreacion());
    }

    @Test
    @DisplayName("Al registrar se calcula la prioridad automaticamente")
    void registroCalculaPrioridad() {
        Incidencia i = servicio.registrar("Servidor caido", "El servidor principal no responde", "Infraestructura",
                Impacto.ALTO, Urgencia.ALTA);
        assertEquals(Prioridad.CRITICA, i.getPrioridad());
    }
}
