package com.helpdeskflow.unitarias;

import com.helpdeskflow.modelo.*;
import com.helpdeskflow.repositorio.RepositorioEnMemoria;
import com.helpdeskflow.servicio.ServicioIncidencias;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias de HU-04 (consultas y filtros). */
class ConsultasYMetricasTest {

    private ServicioIncidencias servicio;

    @BeforeEach
    void preparar() {
        RepositorioEnMemoria repositorio = new RepositorioEnMemoria();
        servicio = new ServicioIncidencias(repositorio);
    }

    private Incidencia finalizada() {
        Incidencia i = servicio.registrar("Disco lleno", "El disco del servidor de archivos esta lleno",
                "Infraestructura", Impacto.ALTO, Urgencia.ALTA);
        servicio.cambiarEstado(i.getId(), Estado.LISTA);
        servicio.cambiarEstado(i.getId(), Estado.EN_DESARROLLO);
        servicio.cambiarEstado(i.getId(), Estado.EN_VALIDACION);
        servicio.registrarSolucion(i.getId(), "Se depuraron registros antiguos y se amplio la particion");
        servicio.cambiarEstado(i.getId(), Estado.FINALIZADA);
        return i;
    }

    @Test
    @DisplayName("Buscar por identificador y filtrar por estado y prioridad")
    void consultasYFiltros() {
        Incidencia abierta = servicio.registrar("Mouse danado", "El mouse de recepcion no responde",
                "Hardware", Impacto.BAJO, Urgencia.BAJA);
        Incidencia cerrada = finalizada();

        assertEquals(abierta.getId(), servicio.obtenerObligatoria(abierta.getId()).getId());
        assertEquals(1, servicio.filtrarPorEstado(Estado.REGISTRADA).size());
        assertEquals(1, servicio.filtrarPorPrioridad(Prioridad.CRITICA).size());
        assertEquals(1, servicio.obtenerAbiertas().size());
        assertEquals(1, servicio.obtenerFinalizadas().size());
        assertEquals(cerrada.getId(), servicio.obtenerFinalizadas().get(0).getId());
        assertThrows(IllegalArgumentException.class, () -> servicio.obtenerObligatoria("id-inexistente"));
    }
}
