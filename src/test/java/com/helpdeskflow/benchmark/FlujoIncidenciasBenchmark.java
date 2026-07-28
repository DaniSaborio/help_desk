package com.helpdeskflow.benchmark;

import com.helpdeskflow.dominio.CalculadoraPrioridad;
import com.helpdeskflow.modelo.*;
import com.helpdeskflow.repositorio.RepositorioEnMemoria;
import com.helpdeskflow.servicio.ServicioIncidencias;
import com.helpdeskflow.servicio.ServicioMetricas;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.CommandLineOptions;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Microbenchmarks JMH del nucleo de dominio de HelpDesk Flow.
 *
 * JMH mide rendimiento de metodos Java in-process (no HTTP como JMeter):
 * controla warmup, JIT y dead-code elimination para que la medicion sea real.
 * Se ejecuta con:  mvn test-compile exec:java
 * o directamente con el metodo main de esta clase.
 *
 * Modos medidos: Throughput (ops/s) y AverageTime (ns/op).
 */
@BenchmarkMode({Mode.Throughput, Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class FlujoIncidenciasBenchmark {

    private CalculadoraPrioridad calculadora;

    /** Repositorio pre-poblado para los benchmarks de solo lectura (filtros y metricas). */
    private ServicioIncidencias servicioConsulta;
    private ServicioMetricas metricasConsulta;

    @Param({"100", "1000"})
    private int tamano;

    @Setup(Level.Trial)
    public void preparar() {
        calculadora = new CalculadoraPrioridad();

        RepositorioEnMemoria repositorio = new RepositorioEnMemoria();
        servicioConsulta = new ServicioIncidencias(repositorio);
        metricasConsulta = new ServicioMetricas(repositorio);
        for (int i = 0; i < tamano; i++) {
            Impacto impacto = Impacto.values()[i % Impacto.values().length];
            Urgencia urgencia = Urgencia.values()[i % Urgencia.values().length];
            Incidencia inc = servicioConsulta.registrar(
                    "Incidencia " + i,
                    "Descripcion de prueba numero " + i + " suficientemente larga",
                    "Categoria", impacto, urgencia);
            // Finaliza una fraccion para que las metricas de throughput y lead time trabajen.
            if (i % 3 == 0) {
                servicioConsulta.cambiarEstado(inc.getId(), Estado.LISTA);
                servicioConsulta.cambiarEstado(inc.getId(), Estado.EN_DESARROLLO);
                servicioConsulta.cambiarEstado(inc.getId(), Estado.EN_VALIDACION);
                servicioConsulta.registrarSolucion(inc.getId(), "Solucion aplicada en el benchmark");
                servicioConsulta.cambiarEstado(inc.getId(), Estado.FINALIZADA);
            }
        }
    }

    /** HU-02: regla pura de calculo de prioridad. */
    @Benchmark
    public Prioridad calcularPrioridad() {
        return calculadora.calcular(Impacto.ALTO, Urgencia.ALTA);
    }

    /** HU-01: registro de una incidencia (validaciones + calculo + guardado). */
    @Benchmark
    public Incidencia registrarIncidencia() {
        ServicioIncidencias servicio = new ServicioIncidencias(new RepositorioEnMemoria());
        return servicio.registrar("Titulo", "Descripcion valida de longitud suficiente",
                "Categoria", Impacto.MEDIO, Urgencia.MEDIA);
    }

    /** HU-01 + HU-03: ciclo completo registrar -> avanzar -> resolver -> finalizar. */
    @Benchmark
    public void flujoCompletoIncidencia(Blackhole bh) {
        ServicioIncidencias servicio = new ServicioIncidencias(new RepositorioEnMemoria());
        Incidencia i = servicio.registrar("Titulo", "Descripcion valida de longitud suficiente",
                "Categoria", Impacto.ALTO, Urgencia.ALTA);
        servicio.cambiarEstado(i.getId(), Estado.LISTA);
        servicio.cambiarEstado(i.getId(), Estado.EN_DESARROLLO);
        servicio.cambiarEstado(i.getId(), Estado.EN_VALIDACION);
        servicio.registrarSolucion(i.getId(), "Solucion aplicada");
        servicio.cambiarEstado(i.getId(), Estado.FINALIZADA);
        bh.consume(i);
    }

    /** HU-04: filtros sobre un repositorio de tamano parametrizado. */
    @Benchmark
    public void filtrosDeConsulta(Blackhole bh) {
        bh.consume(servicioConsulta.filtrarPorEstado(Estado.REGISTRADA));
        bh.consume(servicioConsulta.filtrarPorPrioridad(Prioridad.CRITICA));
        bh.consume(servicioConsulta.obtenerAbiertas());
        bh.consume(servicioConsulta.obtenerFinalizadas());
    }

    /** HU-05: calculo de metricas (total, throughput, lead time, por prioridad). */
    @Benchmark
    public void metricasBasicas(Blackhole bh) {
        bh.consume(metricasConsulta.throughput());
        bh.consume(metricasConsulta.leadTimePromedioHoras());
        bh.consume(metricasConsulta.porPrioridad());
    }

    public static void main(String[] args) throws Exception {
        // Los argumentos de linea de comandos (ej. -wi 1 -i 1 -f 1) sobreescriben
        // la configuracion por defecto; util para una corrida rapida de humo.
        Options opciones = new OptionsBuilder()
                .parent(new CommandLineOptions(args))
                .include(FlujoIncidenciasBenchmark.class.getSimpleName())
                .build();
        new Runner(opciones).run();
    }
}
