# Historias de usuario y criterios de aceptacion

## HU-01. Registrar una incidencia
Como usuario, deseo registrar una incidencia indicando su titulo, descripcion, impacto, urgencia y categoria, para que el equipo tecnico pueda atenderla.

Criterios de aceptacion
- El titulo no puede estar vacio ni contener solo espacios.
- La descripcion debe contener al menos diez caracteres.
- El impacto debe ser BAJO, MEDIO o ALTO.
- La urgencia debe ser BAJA, MEDIA o ALTA.
- El sistema genera un identificador unico y registra la fecha de creacion.
- La incidencia nace en estado REGISTRADA.

Pruebas asociadas: `RegistroIncidenciaTest`

## HU-02. Calcular automaticamente la prioridad
Como encargado de soporte, deseo que el sistema calcule la prioridad de la incidencia para evitar decisiones arbitrarias.

Criterios de aceptacion
- Impacto ALTO y urgencia ALTA producen prioridad CRITICA.
- Impacto ALTO con urgencia MEDIA o BAJA produce prioridad ALTA.
- Impacto MEDIO o BAJO con urgencia ALTA produce prioridad ALTA.
- Cualquier otra combinacion produce prioridad NORMAL.
- El calculo ocurre al registrar, sin intervencion del usuario.

Pruebas asociadas: `CalculadoraPrioridadTest`

## HU-03. Gestionar el flujo de la incidencia
Como tecnico, deseo cambiar el estado de una incidencia para representar su avance real.

Criterios de aceptacion
- El unico flujo valido es REGISTRADA -> LISTA -> EN_DESARROLLO -> EN_VALIDACION -> FINALIZADA.
- El sistema impide saltos (por ejemplo REGISTRADA -> FINALIZADA).
- El sistema impide retrocesos (por ejemplo FINALIZADA -> EN_DESARROLLO).
- Una incidencia no puede pasar a FINALIZADA sin descripcion de la solucion aplicada.
- Al finalizar se registra la fecha de cierre.

Pruebas asociadas: `TransicionesEstadoTest`

## HU-04. Consultar y filtrar incidencias
Como encargado de soporte, deseo consultar y filtrar las incidencias para conocer el estado real del servicio.

Criterios de aceptacion
- Mostrar todas las incidencias.
- Buscar una incidencia por identificador (completo o prefijo corto de consola).
- Filtrar por estado y por prioridad.
- Mostrar solamente abiertas y solamente finalizadas.

Pruebas asociadas: `ConsultasYMetricasTest`

## HU-05. Generar metricas basicas
Como encargado de soporte, deseo ver metricas del flujo para evaluar el desempeno del equipo.

Criterios de aceptacion
- Total de incidencias, finalizadas y abiertas.
- Throughput del periodo registrado.
- Lead time promedio de las incidencias terminadas.
- Cantidad de incidencias por prioridad.

Pruebas asociadas: `ConsultasYMetricasTest`

## Cambio de requerimiento. Clase de servicio EXPEDITE
Como encargado de soporte, deseo marcar una incidencia critica como EXPEDITE para que reciba atencion prioritaria sin saturar el flujo.

Criterios de aceptacion
- Solo una incidencia con prioridad CRITICA puede marcarse como EXPEDITE.
- Solo puede existir una incidencia EXPEDITE en desarrollo o validacion simultaneamente.
- Al finalizar la EXPEDITE activa, otra puede ocupar el carril.
- Las incidencias que no son EXPEDITE no se ven afectadas por la politica.
- Todo el comportamiento previo se mantiene, la suite completa sigue en verde.

Pruebas asociadas: `PoliticaExpediteTest`, `FlujoExpediteTest`
