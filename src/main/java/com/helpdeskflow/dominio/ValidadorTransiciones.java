package com.helpdeskflow.dominio;

import com.helpdeskflow.modelo.Estado;
import com.helpdeskflow.modelo.Incidencia;

import java.util.EnumMap;
import java.util.Map;

/**
 * Regla de negocio HU-03: transiciones de estado.
 * Flujo lineal REGISTRADA -> LISTA -> EN_DESARROLLO -> EN_VALIDACION -> FINALIZADA.
 * Se impiden saltos y retrocesos no autorizados, y no se permite finalizar
 * una incidencia sin descripcion de la solucion aplicada.
 *
 * El mapa de transiciones sustituye una cadena extensa de condicionales,
 * cambio documentado en docs/REFACTORIZACION.md.
 */
public class ValidadorTransiciones {

    private static final Map<Estado, Estado> SIGUIENTE = new EnumMap<>(Estado.class);

    static {
        SIGUIENTE.put(Estado.REGISTRADA, Estado.LISTA);
        SIGUIENTE.put(Estado.LISTA, Estado.EN_DESARROLLO);
        SIGUIENTE.put(Estado.EN_DESARROLLO, Estado.EN_VALIDACION);
        SIGUIENTE.put(Estado.EN_VALIDACION, Estado.FINALIZADA);
    }

    public boolean esTransicionValida(Estado actual, Estado destino) {
        return SIGUIENTE.get(actual) == destino;
    }

    /**
     * Valida y aplica la transicion. Lanza IllegalStateException si la
     * transicion es invalida o si se intenta finalizar sin solucion.
     */
    public void transicionar(Incidencia incidencia, Estado destino) {
        Estado actual = incidencia.getEstado();

        if (!esTransicionValida(actual, destino)) {
            throw new IllegalStateException(
                    String.format("Transicion invalida: %s -> %s", actual, destino));
        }
        if (destino == Estado.FINALIZADA && !incidencia.tieneSolucion()) {
            throw new IllegalStateException(
                    "No se puede finalizar una incidencia sin descripcion de la solucion aplicada");
        }
        incidencia.setEstado(destino);
        if (destino == Estado.FINALIZADA) {
            incidencia.setFechaCierre(java.time.LocalDateTime.now());
        }
    }
}
