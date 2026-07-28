# Plan de trabajo TDD y guia de commits

Este plan cubre el requisito de al menos un commit significativo por dia calendario y la evidencia del ciclo RED -> GREEN -> REFACTOR. La secuencia puede ajustarse a las fechas reales de la pareja, lo que no puede pasar es escribir todo el codigo y las pruebas al final, el historial debe mostrar el ciclo tal como ocurrio.

## Como se ve el ciclo en el historial

Cada historia produce como minimo tres commits en este orden:

1. `test: add failing tests for <regla>` (RED, la prueba existe y falla)
2. `feat: implement <regla>` (GREEN, lo minimo para que pase)
3. `refactor: <mejora> keeping tests green` (REFACTOR, cuando aplica)

En ping-pong TDD el commit RED lo firma un integrante y el commit GREEN el otro, esa alternancia es la evidencia natural de la programacion en pareja.

## Secuencia sugerida (8 a 10 dias)

Dia 1, preparacion. Crear repositorio, proyecto base Maven con JUnit 5, tablero con columnas, limites WIP y las 10 tarjetas iniciales, README inicial.
Commits: `chore: create maven project with junit 5`, `docs: add initial readme and kanban board link`

Dia 2, HU-02 primero (es la regla mas pura, ideal para arrancar con TDD).
Commits: `test: add failing tests for critical priority calculation` (RED), `feat: implement automatic incident priority` (GREEN)

Dia 3, HU-01 con ping-pong invirtiendo roles.
Commits: `test: add failing tests for incident registration rules` (RED), `feat: implement incident registration with validations` (GREEN)

Dia 4, HU-03 transiciones.
Commits: `test: add failing tests for valid and invalid state transitions`, `feat: implement state transition flow`, `fix: prevent closing incidents without solution`

Dia 5, integracion continua y refactorizacion.
Commits: `ci: execute junit tests on push and pull requests`, `refactor: extract priority calculator from incident service`, `refactor: replace transition conditionals with state map`

Dia 6, HU-04 consultas y filtros.
Commits: `test: add failing tests for queries and filters`, `feat: implement incident queries and filters`

Dia 7, HU-05 metricas.
Commits: `test: add failing tests for throughput and lead time metrics`, `feat: implement basic flow metrics`

Dia 8, cambio EXPEDITE (crear la tarjeta en el backlog primero, con criterios verificables).
Commits: `test: add failing tests for expedite service class` (RED), `feat: implement expedite policy with single active limit` (GREEN)

Dia 9, pruebas funcionales, interfaz JavaFX y defectos.
Commits: `test: add end to end functional tests`, `feat: add javafx desktop interface`, y los `fix:` que aparezcan. La interfaz no lleva pruebas unitarias propias porque no contiene reglas, solo traduce hacia los servicios ya probados.

Dia 10, cierre. Completar IA-LOG.md, RETROSPECTIVA.md, README final, verificar la lista de la seccion 12 de la tarea, y ensayar la demostracion desde un entorno limpio (`git clone` en otra carpeta, compilar y correr las pruebas).

## Reglas que la pareja se compromete a respetar

- Ningun dia sin commit significativo, si un dia solo hubo lectura o diseno, se materializa en documentacion o en una prueba nueva.
- Ambos integrantes commitean a lo largo de todo el periodo, no uno al inicio y otro al final.
- La rama main siempre compila y con pruebas en verde, si un cambio rompe algo se corrige antes de seguir.
- El historial no se reescribe, los errores tambien son evidencia de trabajo real.
