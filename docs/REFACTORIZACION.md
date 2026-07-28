# Refactorizacion documentada

## Refactorizacion 1: extraer el calculo de prioridad del servicio de incidencias

**Problema encontrado.** En la primera version funcional el calculo de la prioridad vivia dentro de `ServicioIncidencias.registrar()`, mezclado con las validaciones de entrada y la persistencia. Eso tenia dos costos, probar la regla de HU-02 obligaba a construir el servicio completo con su repositorio, y cualquier cambio futuro en la tabla de prioridades tocaba una clase con demasiadas responsabilidades.

**Cambio realizado.** Extrajimos la regla a la clase `CalculadoraPrioridad` en el paquete `dominio`, con un unico metodo `calcular(Impacto, Urgencia)`. El servicio ahora delega en ella y las pruebas de la regla se ejecutan contra la clase aislada, sin repositorio ni servicio de por medio.

**Pruebas que protegieron la refactorizacion.** Antes de mover el codigo ya existian las pruebas de `CalculadoraPrioridadTest` y `RegistroIncidenciaTest` en verde, tras la extraccion la suite completa siguio pasando sin modificar ninguna asercion, lo que demuestra que el comportamiento externo no cambio.

**Resultado obtenido.** La regla de negocio quedo en una clase de 15 lineas efectivas, legible como la tabla del enunciado, y el servicio quedo con la unica responsabilidad de coordinar. Cuando llego el cambio EXPEDITE pudimos agregar `PoliticaExpedite` siguiendo el mismo patron sin tocar la calculadora.

## Refactorizacion 2: sustituir condicionales de transicion por un mapa de estados

**Problema encontrado.** La validacion de transiciones de HU-03 comenzo como una cadena de `if` anidados que comparaba estado actual contra estado destino, con cinco estados el bloque ya era dificil de leer y cada nueva restriccion lo alargaba.

**Cambio realizado.** Reemplazamos los condicionales por un `EnumMap<Estado, Estado>` que declara el unico sucesor valido de cada estado, la validacion se redujo a una consulta al mapa. La restriccion de no cerrar sin solucion quedo como una verificacion explicita y separada, porque es una regla de negocio distinta a la topologia del flujo.

**Pruebas que protegieron la refactorizacion.** `TransicionesEstadoTest` cubria el flujo completo valido, el salto REGISTRADA -> FINALIZADA, el retroceso desde FINALIZADA y el cierre sin solucion, las cuatro pruebas siguieron en verde despues del cambio.

**Resultado obtenido.** El validador expresa el flujo lineal en cuatro lineas declarativas, agregar o quitar un estado seria modificar el mapa, no reescribir logica, y el mensaje de error conserva el detalle de la transicion rechazada para la interfaz de consola.
