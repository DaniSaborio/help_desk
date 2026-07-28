# Division de trabajo entre la pareja

Este documento registra como se dividio el desarrollo de HelpDesk Flow entre
los dos integrantes y sirve de base para crear las tarjetas del tablero
Kanban (docs/KANBAN.md) en el orden real de integracion. Ambas partes ya
estan integradas en `main` (push realizado el 2026-07-28): la "Primera
parte" con la autoria de Daniel Saborio y la "Segunda parte" con la autoria
de Alejandro Cordero, cada una siguiendo el ciclo RED -> GREEN documentado
en docs/PLAN-TDD.md.

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

## Segunda parte (Alejandro Cordero) — ya integrada en main

Integrada el 2026-07-28 siguiendo el mismo patron RED -> GREEN (ver
docs/PLAN-TDD.md, dias 6 a 9). Cada paso RED se commiteo con la prueba
presente y la implementacion todavia ausente (el arbol de ese commit no
compila, falla real y no simulada) antes del paso GREEN correspondiente:

| # | Commit | Tarjeta Kanban sugerida | Columna al cerrar |
|---|--------|-------------------------|-------------------|
| 1 | `docs: add tarea programativa assignment pdf` | Tarea tecnica: material de la tarea | Hecho |
| 2 | `test: add failing tests for queries and filters` (RED) | HU-04 Consultar y filtrar incidencias | Validacion |
| 3 | `feat: implement incident queries and filters` (GREEN) | HU-04 Consultar y filtrar incidencias | Hecho |
| 4 | `test: add failing tests for throughput and lead time metrics` (RED) | HU-05 Generar metricas basicas | Validacion |
| 5 | `feat: implement basic flow metrics` (GREEN) | HU-05 Generar metricas basicas | Hecho |
| 6 | `test: add failing tests for expedite service class` (RED) | Cambio de requerimiento: clase de servicio EXPEDITE | Validacion |
| 7 | `feat: implement expedite policy with single active limit` (GREEN) | Cambio de requerimiento: clase de servicio EXPEDITE | Hecho |
| 8 | `test: add end to end functional tests` | Tarea tecnica: pruebas funcionales e interfaz | Validacion |
| 9 | `feat: add javafx desktop interface` | Tarea tecnica: pruebas funcionales e interfaz | Hecho |

La interfaz (Main.java, MainApp.java) no lleva pruebas unitarias propias
porque no contiene reglas, solo traduce hacia los servicios ya probados.
Ningun defecto detectado en esta sesion.
