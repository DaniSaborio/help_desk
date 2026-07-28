# Retrospectiva

Nota de la pareja: este documento debe reflejar nuestra experiencia real, lo que sigue es un borrador de trabajo que ajustamos al cerrar el proyecto con los hechos concretos que vivimos, fechas, defectos reales y decisiones propias.

## 1. Que aporto Kanban al trabajo de la pareja

Kanban nos dio algo que en tareas anteriores no teniamos, visibilidad honesta del avance. Antes era comun sentir que ibamos bien porque habia mucho codigo escrito, con el tablero la pregunta cambio a cuantas tarjetas estan realmente en Hecho, y esa diferencia se noto desde la primera semana, porque una historia a medio implementar no cuenta como avance, cuenta como trabajo comprometido que hay que terminar. Las politicas explicitas tambien evitaron discusiones, no habia que negociar cada vez que era estar listo o estar terminado, la definicion ya estaba escrita en el tablero.

## 2. Que dificultad genero el limite WIP

El limite de una tarjeta en desarrollo por pareja fue lo mas incomodo al inicio, la tentacion natural era que mientras uno implementaba una historia el otro arrancara la siguiente, y el limite lo prohibe. Nos obligo a trabajar de verdad en pareja sobre la misma tarjeta, con driver y navigator, en lugar de repartirnos el trabajo y juntarlo al final. El limite de Validacion en uno tambien genero friccion cuando queriamos avanzar y habia una revision pendiente, pero esa friccion era justamente la senal, la prioridad es terminar lo comprometido, no empezar cosas nuevas.

## 3. Que errores fueron detectados mediante TDD

Escribir primero las pruebas de transiciones nos hizo descubrir casos que no habiamos pensado al leer el enunciado, por ejemplo que pasaba si se intentaba finalizar una incidencia que si tenia solucion pero desde un estado que no era EN_VALIDACION, o que la fecha de cierre solo debia asignarse cuando la transicion realmente se concretaba y no antes de validar la solucion. La prueba del cierre sin solucion tambien nos protegio durante la refactorizacion del validador, un cambio en el orden de las verificaciones la puso en rojo de inmediato y la corregimos antes de integrarlo.

## 4. Que parte del codigo fue refactorizada

Documentamos dos refactorizaciones en docs/REFACTORIZACION.md, la extraccion del calculo de prioridad desde el servicio hacia la clase CalculadoraPrioridad, y la sustitucion de los condicionales de transicion por un mapa de estados en ValidadorTransiciones. En ambos casos la suite en verde antes y despues fue lo que nos dio confianza para mover codigo sin miedo.

## 5. Como afecto el cambio de requerimiento

EXPEDITE fue la mejor prueba del diseno, como las reglas ya estaban separadas del servicio pudimos agregar PoliticaExpedite como una clase nueva que se evalua antes de la transicion, sin tocar el validador existente ni romper ninguna prueba anterior. Escribimos primero las pruebas del limite de una EXPEDITE activa, las vimos fallar, e implementamos lo minimo para que pasaran, el cambio completo entro sin reescribir el sistema.

## 6. En que ayudo la IA

La usamos para generar la estructura base del proyecto, proponer casos de prueba que no habiamos considerado, explicar errores puntuales y revisar nombres, todo quedo documentado en IA-LOG.md con su verificacion correspondiente.

## 7. En que se equivoco o fue insuficiente la IA

[Completar con el caso real registrado en IA-LOG.md, por ejemplo una sugerencia que acoplaba las reglas de flujo a la entidad, o una prueba propuesta que no fallaba en rojo y por lo tanto no verificaba nada.]

## 8. Que cambiarian en una siguiente version

Persistencia real en base de datos aprovechando la interfaz del repositorio, una interfaz grafica o web sobre los mismos servicios, metricas por rango de fechas en lugar del periodo completo, y en el proceso, empezar la integracion continua el primer dia y no el quinto, porque los dias sin CI fueron los de menos disciplina.
