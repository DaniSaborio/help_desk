# Metodologia Xanpan aplicada: Kanban + XP

Xanpan combina el flujo visual y los limites de Kanban con las practicas tecnicas de Extreme Programming, en este documento describimos como gobernamos el trabajo del proyecto HelpDesk Flow con ese enfoque.

## 1. Tablero

Usamos GitHub Projects (tipo Board) sobre el mismo repositorio, de manera que cada tarjeta puede enlazarse con sus commits y pull requests. Cualquier plataforma de la lista permitida sirve igual, lo importante es que el historial de movimientos quede visible.

### 1.1 Columnas

| Columna | Significado |
|---|---|
| Opciones / Backlog | Todo lo identificado pero aun no comprometido, historias, tareas tecnicas, defectos y el cambio de requerimiento |
| Preparado | Trabajo listo para iniciar, cumple la politica de entrada (limite WIP: 3) |
| En desarrollo | Lo que la pareja esta construyendo en este momento (limite WIP: 1 por pareja) |
| Validacion | Cambios en revision cruzada y verificacion de criterios (limite WIP: 1) |
| Hecho | Integrado en main con CI en verde (sin limite) |

### 1.2 Limites WIP y regla de flujo

Cuando una columna alcanza su limite no se arranca trabajo nuevo en esa etapa, la prioridad pasa a terminar, desbloquear, revisar o ayudar a avanzar lo que ya esta comprometido. En la practica esto significa que si hay una tarjeta en Validacion, el siguiente paso de la pareja es revisarla, no empezar otra historia.

Si un limite se incumple por una situacion real (por ejemplo un defecto urgente encontrado a mitad de una historia), lo registramos en la tarjeta con un comentario que explica la causa, ocultarlo seria una falta de trazabilidad.

### 1.3 Politicas explicitas (definicion de cada movimiento)

**Entrada a Preparado**
- La descripcion es comprensible por ambos integrantes.
- Tiene criterios de aceptacion verificables escritos en la tarjeta.
- El tamano es manejable, si supera un dia de trabajo se divide.
- Las pruebas principales estan identificadas antes de empezar.

**Entrada a Validacion**
- El codigo compila.
- Las pruebas pasan localmente.
- Existe revision del otro integrante.
- No hay cambios relevantes sin confirmar en el repositorio.

**Entrada a Hecho**
- La integracion continua esta en verde.
- Los criterios de aceptacion fueron verificados uno por uno.
- El codigo esta integrado en la rama principal.
- La documentacion afectada fue actualizada.

## 2. Carril EXPEDITE

El cambio de requerimiento introduce la clase de servicio EXPEDITE, y la aplicamos igual en nuestro propio tablero: una tarjeta marcada EXPEDITE (etiqueta roja) puede saltarse la cola de Preparado, pero solo puede existir una en desarrollo o validacion a la vez, exactamente la misma regla que implementa el sistema en `PoliticaExpedite`. Esta simetria es intencional, el producto modela el proceso con el que fue construido.

## 3. Tarjetas iniciales del backlog

1. HU-01 Registrar una incidencia
2. HU-02 Calcular automaticamente la prioridad
3. HU-03 Gestionar el flujo de la incidencia
4. HU-04 Consultar y filtrar incidencias
5. HU-05 Generar metricas basicas
6. Tarea tecnica: proyecto base Maven + JUnit 5
7. Tarea tecnica: pipeline de integracion continua
8. Tarea tecnica: refactorizacion documentada
9. Cambio de requerimiento: clase de servicio EXPEDITE (entra al backlog cuando el docente lo habilita, con sus propios criterios y pruebas escritas primero)
10. Defectos: se crean conforme aparecen, con pasos de reproduccion

Cada commit relevante menciona su tarjeta (por ejemplo `feat: implement automatic incident priority (#2)`), asi cualquier persona puede seguir la trazabilidad tarjeta - commit - prueba.

## 4. Practicas XP integradas al flujo

- **TDD:** ninguna tarjeta entra a En desarrollo sin sus pruebas principales identificadas, y el primer commit de cada historia es una prueba en rojo (ver docs/PLAN-TDD.md).
- **Programacion en pareja:** alternamos los roles de driver y navigator por sesion, y usamos ping-pong TDD dentro de cada historia, uno escribe la prueba, el otro implementa lo minimo para hacerla pasar y se intercambian los roles.
- **Integracion continua:** cada push ejecuta compilacion y pruebas, una CI en rojo bloquea la entrada de tarjetas a Hecho.
- **Refactorizacion:** se hace con la red de pruebas en verde y se documenta en docs/REFACTORIZACION.md.

## 5. Metricas del flujo

Del propio tablero medimos throughput (tarjetas que llegan a Hecho por semana) y lead time (dias entre entrada a Preparado y llegada a Hecho), las mismas metricas que el sistema calcula para las incidencias en `ServicioMetricas`, lo que nos permitio validar el significado de cada metrica con nuestros propios datos.
