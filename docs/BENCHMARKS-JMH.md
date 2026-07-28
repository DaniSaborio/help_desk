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

## Resultados de referencia

Corrida completa (`mvn test-compile && mvn exec:exec`) sobre JDK 21, warmup 3
iteraciones y medicion 5 iteraciones, 1 fork. Los numeros varian segun la
maquina; sirven como linea base para comparar corridas futuras.

Throughput (`ops/us`, mas alto es mejor):

| Benchmark | tamano=100 | tamano=1000 |
|-----------|-----------:|------------:|
| `calcularPrioridad` | 2211.3 ± 17.9 | 2163.2 ± 155.0 |
| `registrarIncidencia` | 3.603 ± 0.036 | 3.519 ± 0.032 |
| `flujoCompletoIncidencia` | 2.246 ± 0.034 | 2.200 ± 0.010 |
| `metricasBasicas` | 0.460 ± 0.003 | 0.036 ± 0.001 |
| `filtrosDeConsulta` | 0.179 ± 0.005 | 0.023 ± 0.001 |

AverageTime (`us/op`, mas bajo es mejor):

| Benchmark | tamano=100 | tamano=1000 |
|-----------|-----------:|------------:|
| `calcularPrioridad` | ≈ 0.001 | ≈ 0.001 |
| `registrarIncidencia` | 0.282 ± 0.034 | 0.368 ± 0.203 |
| `flujoCompletoIncidencia` | 0.464 ± 0.012 | 0.457 ± 0.013 |
| `metricasBasicas` | 2.338 ± 0.085 | 26.279 ± 0.630 |
| `filtrosDeConsulta` | 5.675 ± 0.214 | 45.538 ± 3.191 |

Observaciones:

- `calcularPrioridad`, `registrarIncidencia` y `flujoCompletoIncidencia` son
  independientes del tamano del repositorio (crean su propio estado), por eso su
  costo no cambia entre 100 y 1000.
- `filtrosDeConsulta` y `metricasBasicas` escalan de forma lineal (O(n)): al pasar
  de 100 a 1000 incidencias son ~8-11 veces mas lentos porque recorren todas las
  incidencias con streams sobre `obtenerTodas()`. Es el comportamiento esperado a
  esta escala; solo justificaria un indice con volumenes mucho mayores.
- El margen de error (intervalo de confianza al 99.9%) es pequeno frente al score,
  lo que indica mediciones estables y reproducibles.
