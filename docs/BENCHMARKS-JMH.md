# Benchmarks de rendimiento con JMH

Este proyecto no expone una capa HTTP (es una aplicacion de escritorio JavaFX
mas una app de consola sobre servicios de dominio en memoria), por lo que una
herramienta de carga web como JMeter no tiene un endpoint al que apuntar. Para
medir el rendimiento del nucleo se usa **JMH (Java Microbenchmark Harness)**, la
herramienta oficial de OpenJDK para medir metodos Java in-process controlando
warmup, JIT y dead-code elimination.

## Que se mide

`src/test/java/com/helpdeskflow/benchmark/FlujoIncidenciasBenchmark.java` mide:

| Benchmark | Regla / caso |
|-----------|--------------|
| `calcularPrioridad` | HU-02: regla pura de calculo de prioridad |
| `registrarIncidencia` | HU-01: validaciones + calculo + guardado |
| `flujoCompletoIncidencia` | HU-01 + HU-03: registrar -> avanzar -> resolver -> finalizar |
| `filtrosDeConsulta` | HU-04: filtros sobre repositorio de tamano parametrizado |
| `metricasBasicas` | HU-05: throughput, lead time y conteo por prioridad |

El parametro `tamano` (100 y 1000) prepobla el repositorio para los benchmarks
de solo lectura. Modos medidos: `Throughput` (ops/s) y `AverageTime` (ns/op).

## Como ejecutar

JMH y el plugin de ejecucion son de scope `test`; no forman parte del jar de la
aplicacion.

```bash
# Compila las clases de prueba (genera el runner de JMH por anotaciones)
mvn test-compile

# Corre todos los benchmarks (usa exec:exec para que el fork de JMH
# herede el classpath correcto)
mvn exec:exec
```

### Corrida rapida de humo

El `main` de la clase acepta argumentos de linea de comandos de JMH, utiles para
una corrida corta durante el desarrollo. Ejemplo ejecutando el jar de clases
directamente con menos iteraciones y un solo tamano:

```bash
mvn test-compile
mvn -q dependency:build-classpath -Dmdep.outputFile=cp.txt -DincludeScope=test
CP="target/test-classes:target/classes:$(cat cp.txt)"
java -cp "$CP" com.helpdeskflow.benchmark.FlujoIncidenciasBenchmark \
    -wi 1 -i 1 -f 1 -p tamano=100
```

Argumentos utiles: `-wi` iteraciones de warmup, `-i` iteraciones de medicion,
`-f` numero de forks, `-p tamano=100` fija el parametro, `-bm thrpt|avgt` el modo.

## Interpretacion

Los benchmarks NO son pruebas que aprueban o fallan; producen numeros
comparativos. Sirven para detectar regresiones de rendimiento (por ejemplo, si un
cambio en los filtros de HU-04 degrada el throughput frente a una corrida
anterior). Por eso no se ejecutan en la CI de cada push, solo bajo demanda.
