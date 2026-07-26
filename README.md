# HelpDesk Flow

![CI](https://github.com/USUARIO/helpdesk-flow/actions/workflows/ci.yml/badge.svg)

Sistema de registro, priorizacion, atencion, validacion y cierre de incidencias tecnicas, desarrollado como tarea programativa del curso ITI-822 Metodologias Agiles de Desarrollo de Software aplicando Xanpan, la combinacion de Kanban para gobernar el flujo de trabajo con las practicas tecnicas de Extreme Programming.

## Integrantes

- Alejandro Cordero
- [Nombre del segundo integrante]

## Descripcion del sistema

HelpDesk Flow permite registrar incidencias con titulo, descripcion, categoria, impacto y urgencia, calcula la prioridad automaticamente segun las reglas de negocio, controla el flujo de estados REGISTRADA -> LISTA -> EN_DESARROLLO -> EN_VALIDACION -> FINALIZADA impidiendo saltos y retrocesos, ofrece consultas y filtros, genera metricas de flujo (throughput, lead time, conteos por prioridad) e implementa la clase de servicio EXPEDITE, que garantiza que solo una incidencia critica urgente ocupe el carril de desarrollo o validacion a la vez.

La interfaz principal es una aplicacion de escritorio JavaFX con formulario de registro, tabla de incidencias, filtros por estado y prioridad, busqueda por identificador, acciones de cambio de estado, registro de solucion y marcado EXPEDITE, y un panel de metricas. Todas las violaciones de reglas de negocio (transiciones invalidas, cierre sin solucion, limite EXPEDITE) se muestran como dialogos con el mensaje exacto que produce el dominio.

## Requisitos de ejecucion

- Java 17 o superior
- Maven 3.8 o superior (Maven descarga JavaFX automaticamente, no requiere instalacion aparte)

## Compilar

```
mvn clean compile
```

## Ejecutar la aplicacion

Interfaz grafica JavaFX (forma principal):

```
mvn clean javafx:run
```

Alternativa por consola, la misma logica de negocio sin interfaz grafica:

```
mvn compile exec:java -Dexec.mainClass=com.helpdeskflow.app.Main
```

## Ejecutar las pruebas

```
mvn test
```

La suite incluye pruebas unitarias (calculo de prioridad, registro, transiciones validas e invalidas, cierre sin solucion, politica EXPEDITE, consultas y metricas) y dos pruebas funcionales de extremo a extremo (`FlujoCompletoIncidenciaTest` y `FlujoExpediteTest`).

## Tablero Kanban

[Enlace al tablero](https://github.com/users/USUARIO/projects/N)

Las columnas, limites WIP, politicas explicitas y el carril EXPEDITE estan documentados en [docs/KANBAN.md](docs/KANBAN.md).

## Decisiones principales de diseno

- **Reglas de negocio aisladas en el paquete `dominio`.** `CalculadoraPrioridad`, `ValidadorTransiciones` y `PoliticaExpedite` no dependen de la persistencia ni de la consola, por eso se prueban de forma unitaria y directa.
- **Transiciones como mapa de estados.** El flujo lineal se declara en un `EnumMap` en lugar de condicionales encadenados, resultado de la refactorizacion documentada en [docs/REFACTORIZACION.md](docs/REFACTORIZACION.md).
- **EXPEDITE como politica independiente.** El cambio de requerimiento se incorporo sin modificar el validador existente, la politica se evalua antes de la transicion y toda la suite previa siguio en verde.
- **Repositorio tras una interfaz.** `RepositorioIncidencias` permite sustituir la implementacion en memoria por una base de datos sin tocar los servicios.
- **La vista solo traduce.** Ni `MainApp` (JavaFX) ni `Main` (consola) contienen reglas, capturan entradas, llaman servicios y muestran los mensajes de error del dominio. La interfaz grafica se agrego sin modificar una sola linea de las clases probadas, la mejor evidencia de que la separacion por capas funciona.

## Estado de la integracion continua

El pipeline de GitHub Actions ([.github/workflows/ci.yml](.github/workflows/ci.yml)) compila el proyecto y ejecuta la suite completa en cada push y pull request sobre main, falla si cualquier prueba falla, y el badge al inicio de este documento refleja el estado actual de la rama principal.

## Documentacion adicional

- [docs/HISTORIAS.md](docs/HISTORIAS.md), historias de usuario con criterios de aceptacion y pruebas asociadas
- [docs/KANBAN.md](docs/KANBAN.md), metodologia Xanpan, tablero, WIP y politicas
- [docs/PLAN-TDD.md](docs/PLAN-TDD.md), plan de trabajo y guia de commits RED-GREEN-REFACTOR
- [docs/REFACTORIZACION.md](docs/REFACTORIZACION.md), refactorizaciones documentadas
- [IA-LOG.md](IA-LOG.md), bitacora de uso de inteligencia artificial
- [RETROSPECTIVA.md](RETROSPECTIVA.md), retrospectiva del proyecto
# help_desk
