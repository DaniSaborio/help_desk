package com.helpdeskflow.app;

import com.helpdeskflow.modelo.*;
import com.helpdeskflow.repositorio.RepositorioEnMemoria;
import com.helpdeskflow.servicio.ServicioIncidencias;
import com.helpdeskflow.servicio.ServicioMetricas;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Interfaz grafica JavaFX de HelpDesk Flow.
 *
 * La vista no contiene reglas de negocio, unicamente captura entradas,
 * invoca los servicios y muestra los resultados o los mensajes de error
 * que produce el dominio. Gracias a esa separacion, esta interfaz se
 * agrego sin modificar una sola linea de las clases ya probadas.
 */
public class MainApp extends Application {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final RepositorioEnMemoria repositorio = new RepositorioEnMemoria();
    private final ServicioIncidencias servicio = new ServicioIncidencias(repositorio);
    private final ServicioMetricas metricas = new ServicioMetricas(repositorio);

    private final ObservableList<Incidencia> datosTabla = FXCollections.observableArrayList();
    private final TableView<Incidencia> tabla = new TableView<>(datosTabla);
    private final Label etiquetaMetricas = new Label();
    private final Label etiquetaFiltro = new Label("Mostrando: todas");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage escenario) {
        BorderPane raiz = new BorderPane();
        raiz.setPadding(new Insets(12));

        raiz.setTop(construirEncabezado());
        raiz.setLeft(construirFormularioRegistro());
        raiz.setCenter(construirZonaCentral());
        raiz.setBottom(construirPanelMetricas());

        refrescar(servicio.obtenerTodas(), "todas");

        escenario.setTitle("HelpDesk Flow - Gestion de incidencias");
        escenario.setScene(new Scene(raiz, 1180, 640));
        escenario.show();
    }

    // ===== Encabezado =====

    private VBox construirEncabezado() {
        Label titulo = new Label("HelpDesk Flow");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Label subtitulo = new Label(
                "Registro, priorizacion, atencion, validacion y cierre de incidencias tecnicas");
        VBox caja = new VBox(2, titulo, subtitulo);
        caja.setPadding(new Insets(0, 0, 10, 0));
        return caja;
    }

    // ===== Formulario de registro (HU-01, HU-02) =====

    private VBox construirFormularioRegistro() {
        TextField campoTitulo = new TextField();
        TextArea campoDescripcion = new TextArea();
        campoDescripcion.setPrefRowCount(3);
        campoDescripcion.setWrapText(true);
        TextField campoCategoria = new TextField();
        ComboBox<Impacto> comboImpacto = new ComboBox<>(
                FXCollections.observableArrayList(Impacto.values()));
        ComboBox<Urgencia> comboUrgencia = new ComboBox<>(
                FXCollections.observableArrayList(Urgencia.values()));
        comboImpacto.setMaxWidth(Double.MAX_VALUE);
        comboUrgencia.setMaxWidth(Double.MAX_VALUE);

        Button botonRegistrar = new Button("Registrar incidencia");
        botonRegistrar.setMaxWidth(Double.MAX_VALUE);
        botonRegistrar.setDefaultButton(true);
        botonRegistrar.setOnAction(e -> {
            try {
                Incidencia nueva = servicio.registrar(
                        campoTitulo.getText(),
                        campoDescripcion.getText(),
                        campoCategoria.getText(),
                        comboImpacto.getValue(),
                        comboUrgencia.getValue());
                informar("Incidencia registrada",
                        "Se registro con prioridad " + nueva.getPrioridad()
                                + "\nIdentificador: " + idCorto(nueva));
                campoTitulo.clear();
                campoDescripcion.clear();
                campoCategoria.clear();
                comboImpacto.setValue(null);
                comboUrgencia.setValue(null);
                refrescar(servicio.obtenerTodas(), "todas");
            } catch (IllegalArgumentException ex) {
                advertir(ex.getMessage());
            }
        });

        VBox formulario = new VBox(6,
                seccion("Nueva incidencia"),
                new Label("Titulo"), campoTitulo,
                new Label("Descripcion (minimo 10 caracteres)"), campoDescripcion,
                new Label("Categoria"), campoCategoria,
                new Label("Impacto"), comboImpacto,
                new Label("Urgencia"), comboUrgencia,
                new Separator(),
                botonRegistrar,
                new Label("La prioridad se calcula\nautomaticamente (HU-02)"));
        formulario.setPrefWidth(230);
        formulario.setPadding(new Insets(0, 12, 0, 0));
        return formulario;
    }

    // ===== Zona central: filtros, tabla y acciones (HU-03, HU-04, EXPEDITE) =====

    private VBox construirZonaCentral() {
        configurarColumnas();
        tabla.setPlaceholder(new Label("Sin incidencias registradas"));
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(tabla, Priority.ALWAYS);

        return new VBox(8, construirBarraFiltros(), etiquetaFiltro, tabla, construirBarraAcciones());
    }

    private void configurarColumnas() {
        tabla.getColumns().setAll(List.of(
                columna("Id", 70, i -> idCorto(i)),
                columna("Titulo", 150, Incidencia::getTitulo),
                columna("Categoria", 90, Incidencia::getCategoria),
                columna("Clase", 80, i -> i.getClaseServicio().name()),
                columna("Impacto", 70, i -> i.getImpacto().name()),
                columna("Urgencia", 70, i -> i.getUrgencia().name()),
                columna("Prioridad", 80, i -> i.getPrioridad().name()),
                columna("Estado", 110, i -> i.getEstado().name()),
                columna("Creada", 110, i -> i.getFechaCreacion().format(FORMATO_FECHA)),
                columna("Cierre", 110, i -> i.getFechaCierre() == null
                        ? "" : i.getFechaCierre().format(FORMATO_FECHA)),
                columna("Solucion", 150, i -> i.getSolucion() == null ? "" : i.getSolucion())));
    }

    private TableColumn<Incidencia, String> columna(String nombre, int ancho,
                                                    java.util.function.Function<Incidencia, String> extractor) {
        TableColumn<Incidencia, String> col = new TableColumn<>(nombre);
        col.setPrefWidth(ancho);
        col.setCellValueFactory(celda -> new ReadOnlyStringWrapper(extractor.apply(celda.getValue())));
        return col;
    }

    private HBox construirBarraFiltros() {
        Button botonTodas = new Button("Todas");
        botonTodas.setOnAction(e -> refrescar(servicio.obtenerTodas(), "todas"));

        Button botonAbiertas = new Button("Abiertas");
        botonAbiertas.setOnAction(e -> refrescar(servicio.obtenerAbiertas(), "abiertas"));

        Button botonFinalizadas = new Button("Finalizadas");
        botonFinalizadas.setOnAction(e -> refrescar(servicio.obtenerFinalizadas(), "finalizadas"));

        ComboBox<Estado> filtroEstado = new ComboBox<>(
                FXCollections.observableArrayList(Estado.values()));
        filtroEstado.setPromptText("Por estado");
        filtroEstado.setOnAction(e -> {
            if (filtroEstado.getValue() != null) {
                refrescar(servicio.filtrarPorEstado(filtroEstado.getValue()),
                        "estado " + filtroEstado.getValue());
            }
        });

        ComboBox<Prioridad> filtroPrioridad = new ComboBox<>(
                FXCollections.observableArrayList(Prioridad.values()));
        filtroPrioridad.setPromptText("Por prioridad");
        filtroPrioridad.setOnAction(e -> {
            if (filtroPrioridad.getValue() != null) {
                refrescar(servicio.filtrarPorPrioridad(filtroPrioridad.getValue()),
                        "prioridad " + filtroPrioridad.getValue());
            }
        });

        TextField campoBusqueda = new TextField();
        campoBusqueda.setPromptText("Buscar por id");
        campoBusqueda.setPrefWidth(120);
        Button botonBuscar = new Button("Buscar");
        botonBuscar.setOnAction(e -> {
            try {
                Incidencia encontrada = servicio.obtenerObligatoria(campoBusqueda.getText().trim());
                refrescar(List.of(encontrada), "busqueda por id");
            } catch (IllegalArgumentException ex) {
                advertir(ex.getMessage());
            }
        });

        HBox barra = new HBox(8, botonTodas, botonAbiertas, botonFinalizadas,
                filtroEstado, filtroPrioridad, campoBusqueda, botonBuscar);
        return barra;
    }

    private HBox construirBarraAcciones() {
        ComboBox<Estado> comboDestino = new ComboBox<>(
                FXCollections.observableArrayList(Estado.values()));
        comboDestino.setPromptText("Estado destino");

        Button botonAvanzar = new Button("Cambiar estado");
        botonAvanzar.setOnAction(e -> conSeleccion(incidencia -> {
            if (comboDestino.getValue() == null) {
                advertir("Seleccione el estado destino");
                return;
            }
            servicio.cambiarEstado(incidencia.getId(), comboDestino.getValue());
            refrescarManteniendoFiltroActual();
        }));

        Button botonSolucion = new Button("Registrar solucion");
        botonSolucion.setOnAction(e -> conSeleccion(incidencia -> {
            TextInputDialog dialogo = new TextInputDialog(
                    incidencia.getSolucion() == null ? "" : incidencia.getSolucion());
            dialogo.setTitle("Registrar solucion");
            dialogo.setHeaderText("Incidencia " + idCorto(incidencia) + " - " + incidencia.getTitulo());
            dialogo.setContentText("Descripcion de la solucion aplicada:");
            dialogo.showAndWait().ifPresent(texto -> {
                servicio.registrarSolucion(incidencia.getId(), texto);
                refrescarManteniendoFiltroActual();
            });
        }));

        Button botonExpedite = new Button("Marcar EXPEDITE");
        botonExpedite.setOnAction(e -> conSeleccion(incidencia -> {
            servicio.marcarExpedite(incidencia.getId());
            informar("EXPEDITE", "La incidencia " + idCorto(incidencia)
                    + " fue marcada como EXPEDITE.\nSolo una EXPEDITE puede estar en "
                    + "desarrollo o validacion a la vez.");
            refrescarManteniendoFiltroActual();
        }));

        HBox barra = new HBox(8, new Label("Con la fila seleccionada:"),
                comboDestino, botonAvanzar, botonSolucion, botonExpedite);
        barra.setPadding(new Insets(4, 0, 0, 0));
        return barra;
    }

    // ===== Panel de metricas (HU-05) =====

    private VBox construirPanelMetricas() {
        Button botonActualizar = new Button("Actualizar metricas");
        botonActualizar.setOnAction(e -> actualizarMetricas());
        actualizarMetricas();
        VBox panel = new VBox(4, new Separator(), seccion("Metricas"),
                etiquetaMetricas, botonActualizar);
        panel.setPadding(new Insets(8, 0, 0, 0));
        return panel;
    }

    private void actualizarMetricas() {
        etiquetaMetricas.setText(String.format(
                "Total: %d   |   Finalizadas: %d   |   Abiertas: %d   |   Throughput: %d   |   "
                        + "Lead time promedio: %.2f horas   |   Por prioridad: %s",
                metricas.total(), metricas.finalizadas(), metricas.abiertas(),
                metricas.throughput(), metricas.leadTimePromedioHoras(), metricas.porPrioridad()));
    }

    // ===== Utilidades =====

    /**
     * Ejecuta una accion sobre la incidencia seleccionada, mostrando en un
     * dialogo cualquier violacion de las reglas de negocio (transiciones
     * invalidas, cierre sin solucion, limite EXPEDITE).
     */
    private void conSeleccion(java.util.function.Consumer<Incidencia> accion) {
        Incidencia seleccionada = tabla.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            advertir("Seleccione una incidencia en la tabla");
            return;
        }
        try {
            accion.accept(seleccionada);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            advertir(ex.getMessage());
        }
    }


    private void refrescar(List<Incidencia> incidencias, String descripcionFiltro) {
        datosTabla.setAll(incidencias);
        etiquetaFiltro.setText("Mostrando: " + descripcionFiltro
                + " (" + incidencias.size() + ")");
        actualizarMetricas();
    }

    private void refrescarManteniendoFiltroActual() {
        // Tras una accion se vuelve a la vista completa para reflejar el cambio
        refrescar(servicio.obtenerTodas(), "todas");
    }

    private Label seccion(String texto) {
        Label etiqueta = new Label(texto);
        etiqueta.setStyle("-fx-font-weight: bold;");
        return etiqueta;
    }

    private String idCorto(Incidencia incidencia) {
        return incidencia.getId().substring(0, 8);
    }

    private void advertir(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.WARNING, mensaje, ButtonType.OK);
        alerta.setHeaderText("Regla de negocio");
        alerta.showAndWait();
    }

    private void informar(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION, mensaje, ButtonType.OK);
        alerta.setHeaderText(titulo);
        alerta.showAndWait();
    }
}
