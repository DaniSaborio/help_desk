package com.helpdeskflow.app;

import com.helpdeskflow.modelo.*;
import com.helpdeskflow.repositorio.RepositorioEnMemoria;
import com.helpdeskflow.servicio.ServicioIncidencias;
import com.helpdeskflow.servicio.ServicioMetricas;

import java.util.List;
import java.util.Scanner;

/**
 * Aplicacion de consola de HelpDesk Flow.
 * La capa de presentacion solo traduce entradas del usuario a llamadas de
 * servicio; todas las reglas de negocio viven en el paquete dominio.
 */
public class Main {

    private static final Scanner entrada = new Scanner(System.in);

    public static void main(String[] args) {
        var repositorio = new RepositorioEnMemoria();
        var servicio = new ServicioIncidencias(repositorio);
        var metricas = new ServicioMetricas(repositorio);

        System.out.println("==============================================");
        System.out.println("  HelpDesk Flow - Gestion de incidencias");
        System.out.println("==============================================");

        boolean continuar = true;
        while (continuar) {
            mostrarMenu();
            String opcion = entrada.nextLine().trim();
            try {
                switch (opcion) {
                    case "1" -> registrar(servicio);
                    case "2" -> listar(servicio.obtenerTodas());
                    case "3" -> buscarPorId(servicio);
                    case "4" -> filtrarPorEstado(servicio);
                    case "5" -> filtrarPorPrioridad(servicio);
                    case "6" -> listar(servicio.obtenerAbiertas());
                    case "7" -> listar(servicio.obtenerFinalizadas());
                    case "8" -> avanzarEstado(servicio);
                    case "9" -> registrarSolucion(servicio);
                    case "10" -> marcarExpedite(servicio);
                    case "11" -> mostrarMetricas(metricas);
                    case "0" -> continuar = false;
                    default -> System.out.println("Opcion no valida");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println(">> " + e.getMessage());
            }
        }
        System.out.println("Hasta pronto.");
    }

    private static void mostrarMenu() {
        System.out.println("""

                1.  Registrar incidencia
                2.  Mostrar todas
                3.  Buscar por identificador
                4.  Filtrar por estado
                5.  Filtrar por prioridad
                6.  Mostrar abiertas
                7.  Mostrar finalizadas
                8.  Avanzar estado
                9.  Registrar solucion
                10. Marcar EXPEDITE
                11. Metricas
                0.  Salir
                """);
        System.out.print("Opcion: ");
    }

    private static void registrar(ServicioIncidencias servicio) {
        System.out.print("Titulo: ");
        String titulo = entrada.nextLine();
        System.out.print("Descripcion: ");
        String descripcion = entrada.nextLine();
        System.out.print("Categoria: ");
        String categoria = entrada.nextLine();
        System.out.print("Impacto (BAJO/MEDIO/ALTO): ");
        Impacto impacto = Impacto.valueOf(entrada.nextLine().trim().toUpperCase());
        System.out.print("Urgencia (BAJA/MEDIA/ALTA): ");
        Urgencia urgencia = Urgencia.valueOf(entrada.nextLine().trim().toUpperCase());
        Incidencia i = servicio.registrar(titulo, descripcion, categoria, impacto, urgencia);
        System.out.println("Registrada con prioridad " + i.getPrioridad() + ": " + i);
    }

    private static void listar(List<Incidencia> incidencias) {
        if (incidencias.isEmpty()) {
            System.out.println("(sin resultados)");
            return;
        }
        incidencias.forEach(System.out::println);
    }

    private static void buscarPorId(ServicioIncidencias servicio) {
        System.out.print("Identificador (completo o los primeros 8 caracteres): ");
        System.out.println(servicio.obtenerObligatoria(entrada.nextLine().trim()));
    }

    private static void filtrarPorEstado(ServicioIncidencias servicio) {
        System.out.print("Estado (REGISTRADA/LISTA/EN_DESARROLLO/EN_VALIDACION/FINALIZADA): ");
        listar(servicio.filtrarPorEstado(Estado.valueOf(entrada.nextLine().trim().toUpperCase())));
    }

    private static void filtrarPorPrioridad(ServicioIncidencias servicio) {
        System.out.print("Prioridad (NORMAL/ALTA/CRITICA): ");
        listar(servicio.filtrarPorPrioridad(Prioridad.valueOf(entrada.nextLine().trim().toUpperCase())));
    }

    private static void avanzarEstado(ServicioIncidencias servicio) {
        System.out.print("Identificador: ");
        String id = entrada.nextLine().trim();
        System.out.print("Estado destino: ");
        Estado destino = Estado.valueOf(entrada.nextLine().trim().toUpperCase());
        servicio.cambiarEstado(id, destino);
        System.out.println("Estado actualizado.");
    }

    private static void registrarSolucion(ServicioIncidencias servicio) {
        System.out.print("Identificador: ");
        String id = entrada.nextLine().trim();
        System.out.print("Descripcion de la solucion: ");
        servicio.registrarSolucion(id, entrada.nextLine());
        System.out.println("Solucion registrada.");
    }

    private static void marcarExpedite(ServicioIncidencias servicio) {
        System.out.print("Identificador: ");
        servicio.marcarExpedite(entrada.nextLine().trim());
        System.out.println("Incidencia marcada como EXPEDITE.");
    }

    private static void mostrarMetricas(ServicioMetricas metricas) {
        System.out.println("Total de incidencias:      " + metricas.total());
        System.out.println("Finalizadas:               " + metricas.finalizadas());
        System.out.println("Abiertas:                  " + metricas.abiertas());
        System.out.println("Throughput del periodo:    " + metricas.throughput());
        System.out.printf ("Lead time promedio:        %.2f horas%n", metricas.leadTimePromedioHoras());
        System.out.println("Por prioridad:             " + metricas.porPrioridad());
    }
}
