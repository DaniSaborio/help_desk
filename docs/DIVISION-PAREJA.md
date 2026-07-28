# Division de trabajo entre la pareja

Este documento registra como se dividio el desarrollo de HelpDesk Flow entre
los dos integrantes y sirve de base para crear las tarjetas del tablero
Kanban (docs/KANBAN.md) en el orden real de integracion. Los commits de la
"Primera parte" ya estan en `main` (push realizado el 2026-07-28). La
"Segunda parte" queda preparada en el working tree (archivos nuevos o
modificados sin commitear) para que el segundo integrante la revise, la
haga suya y la commitee con su propia autoria.

## Primera parte (Daniel Saborio) — ya integrada en main

| # | Commit | Tarjeta Kanban sugerida | Columna al cerrar |
|---|---|---|---|
| 1 | `625667d` chore: create maven project base with junit 5 and domain model | Tarea tecnica: proyecto base Maven + JUnit 5 | Hecho |
| 2 | `5119cc2` docs: add methodology docs, IA log, retrospective and author name | Tarea tecnica: proyecto base Maven + JUnit 5 (documentacion inicial) | Hecho |
| 3 | `396fed8` test: add failing tests for critical priority calculation (RED) | HU-02 Calcular automaticamente la prioridad | Validacion |
| 4 | `faf3394` feat: implement automatic incident priority calculation (GREEN) | HU-02 Calcular automaticamente la prioridad | Hecho |
| 5 | `a34df6b` test: add failing tests for incident registration rules (RED) | HU-01 Registrar una incidencia | Validacion |
| 6 | `c293a5d` feat: implement incident registration with validations (GREEN) | HU-01 Registrar una incidencia | Hecho |
| 7 | `3d4b8bb` test: add failing tests for valid and invalid state transitions (RED) | HU-03 Gestionar el flujo de la incidencia | Validacion |
| 8 | `f000985` feat: implement state transition flow (GREEN) | HU-03 Gestionar el flujo de la incidencia | Hecho |
| 9 | `3f49e70` ci: execute maven build and tests on push and pull requests | Tarea tecnica: pipeline de integracion continua | Hecho |
| 10 | `2d8ebce` docs: document priority calculator and transition validator refactors | Tarea tecnica: refactorizacion documentada | Hecho |

Cada paso RED se verifico ejecutando `mvn test` con la implementacion
todavia ausente (falla de compilacion real, no simulada) antes de commitear
la prueba; cada paso GREEN se verifico ejecutando `mvn test` de nuevo para
confirmar que la prueba nueva pasa sin romper las anteriores.

## Segunda parte (compañero/a) — pendiente de commitear

Estos archivos ya existen en el working tree (generados en la sesion de IA
documentada en IA-LOG.md y revisados por la pareja) pero deliberadamente
**no se commitearon** para que el segundo integrante los integre con su
propia autoria de Git, siguiendo el mismo patron RED -> GREEN:

| Tarjeta Kanban sugerida | Archivos involucrados | Nota |
|---|---|---|
| HU-04 Consultar y filtrar incidencias | `src/test/java/.../ConsultasYMetricasTest.java` (parte de filtros), metodos `obtenerTodas`, `filtrarPorEstado`, `filtrarPorPrioridad`, `obtenerAbiertas`, `obtenerFinalizadas` en `ServicioIncidencias.java` | `ServicioIncidencias.java` esta modificado en el working tree con la version completa; separar el commit de HU-04 solo debe tocar estos metodos |
| HU-05 Generar metricas basicas | `src/main/java/.../servicio/ServicioMetricas.java`, resto de `ConsultasYMetricasTest.java` | Depende de HU-04 ya commiteado |
| Cambio de requerimiento: clase de servicio EXPEDITE | `src/main/java/.../dominio/PoliticaExpedite.java`, `src/test/java/.../PoliticaExpediteTest.java`, metodo `marcarExpedite()` y la linea `politicaExpedite.validarTransicion(...)` dentro de `cambiarEstado()` en `ServicioIncidencias.java` | Crear primero la tarjeta en el backlog con criterios de aceptacion (seccion 3 de la tarea) antes de commitear |
| Tarea tecnica: pruebas funcionales e interfaz | `src/test/java/.../funcionales/FlujoCompletoIncidenciaTest.java`, `FlujoExpediteTest.java`, `src/main/java/.../app/Main.java`, `src/main/java/.../app/MainApp.java` | La interfaz no lleva pruebas propias porque solo traduce hacia los servicios ya probados |
| Defectos | (se crean conforme aparecen) | Ninguno detectado todavia en esta sesion |

## Como continuar

1. El segundo integrante hace `git pull`, revisa los archivos sin commitear
   con `git status` y `git diff`.
2. Los divide en commits pequenos siguiendo el mismo orden RED -> GREEN
   (ver docs/PLAN-TDD.md), firmandolos con su propio nombre y correo de Git.
3. Cada commit se mueve en el tablero Kanban desde Preparado -> En desarrollo
   -> Validacion -> Hecho, respetando los limites WIP (seccion 1.2 de
   docs/KANBAN.md).
4. Al terminar, actualizar este archivo marcando la segunda parte como
   integrada y borrar la tabla de pendientes o moverla a un historial.
