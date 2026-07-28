# Bitacora de uso de inteligencia artificial

La tarea exige que esta bitacora refleje interacciones reales, con al menos una respuesta modificada por la pareja, una sugerencia rechazada con su razon tecnica, y la forma en que se verifico cada resultado utilizado. La primera entrada corresponde a una sesion real, las marcadas con [COMPLETAR] deben llenarse con las interacciones propias de la pareja durante el desarrollo, no se deben inventar entradas.

| Fecha | Herramienta | Objetivo | Resultado usado | Verificacion | Cambios humanos |
|---|---|---|---|---|---|
| 2026-07-25 | Claude | Generar la estructura base del proyecto, el esqueleto de las reglas de negocio, una primera version de las pruebas de HU-01 a HU-05 y EXPEDITE, y la interfaz grafica JavaFX | Estructura Maven, clases de dominio y borrador de pruebas JUnit | Compilamos el proyecto, ejecutamos la suite completa localmente y revisamos cada regla contra la tabla del enunciado caso por caso | Revisamos y ajustamos nombres, mensajes de error en espanol, y reorganizamos las pruebas en paquetes unitarias/funcionales segun el criterio de la pareja |
| [COMPLETAR] | [Herramienta] | Ejemplo de respuesta modificada, la IA propuso X y la pareja lo cambio por Y | [Que parte se uso] | [Como se verifico, prueba, ejecucion, revision cruzada] | [Que se modifico y por que] |
| [COMPLETAR] | [Herramienta] | Ejemplo de sugerencia rechazada | Ninguno, la sugerencia fue descartada | [Como se detecto el problema] | Rechazada. Razon tecnica: [por ejemplo, la IA sugirio validar las transiciones dentro de la entidad Incidencia, lo rechazamos porque acoplaria la entidad a las reglas de flujo y romperia la separacion que protege nuestras pruebas unitarias] |
| [COMPLETAR] | [Herramienta] | [Explicar un error de compilacion, configurar CI, proponer un caso limite, etc.] | [Resultado] | [Verificacion] | [Cambios] |

## Notas sobre el uso critico de la IA

- Todo codigo sugerido por IA fue leido, entendido y probado antes de integrarse, ninguno de los dos integrantes integra codigo que no pueda explicar en la defensa.
- Las pruebas no se consideran validas por venir de la IA, cada una se ejecuto primero en rojo contra la implementacion vacia o incorrecta para confirmar que realmente verifica algo.
- Los errores o sugerencias incorrectas de la IA se documentan aqui, no se ocultan, forman parte de la evidencia de uso critico que pide la seccion 6 de la tarea.
