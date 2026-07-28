package com.helpdeskflow.modelo;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidad principal del sistema. Contiene la informacion minima exigida:
 * identificador unico, titulo, descripcion, categoria, impacto, urgencia,
 * prioridad calculada, estado, fecha de creacion, fecha de cierre y
 * descripcion de la solucion aplicada.
 */
public class Incidencia {

    private final String id;
    private final String titulo;
    private final String descripcion;
    private final String categoria;
    private final Impacto impacto;
    private final Urgencia urgencia;
    private final Prioridad prioridad;
    private Estado estado;
    private final LocalDateTime fechaCreacion;
    private LocalDateTime fechaCierre;
    private String solucion;
    private ClaseServicio claseServicio;

    public Incidencia(String titulo, String descripcion, String categoria,
                      Impacto impacto, Urgencia urgencia, Prioridad prioridad) {
        this.id = UUID.randomUUID().toString();
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.impacto = impacto;
        this.urgencia = urgencia;
        this.prioridad = prioridad;
        this.estado = Estado.REGISTRADA;
        this.fechaCreacion = LocalDateTime.now();
        this.claseServicio = ClaseServicio.ESTANDAR;
    }

    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getCategoria() { return categoria; }
    public Impacto getImpacto() { return impacto; }
    public Urgencia getUrgencia() { return urgencia; }
    public Prioridad getPrioridad() { return prioridad; }
    public Estado getEstado() { return estado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public LocalDateTime getFechaCierre() { return fechaCierre; }
    public String getSolucion() { return solucion; }
    public ClaseServicio getClaseServicio() { return claseServicio; }

    public void setEstado(Estado estado) { this.estado = estado; }
    public void setFechaCierre(LocalDateTime fechaCierre) { this.fechaCierre = fechaCierre; }
    public void setSolucion(String solucion) { this.solucion = solucion; }
    public void setClaseServicio(ClaseServicio claseServicio) { this.claseServicio = claseServicio; }

    public boolean tieneSolucion() {
        return solucion != null && !solucion.isBlank();
    }

    public boolean estaAbierta() {
        return estado != Estado.FINALIZADA;
    }

    public boolean esExpedite() {
        return claseServicio == ClaseServicio.EXPEDITE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Incidencia otra)) return false;
        return id.equals(otra.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s | %s | Impacto: %s | Urgencia: %s | Prioridad: %s | Estado: %s%s",
                id.substring(0, 8), titulo, categoria, claseServicio,
                impacto, urgencia, prioridad, estado,
                tieneSolucion() ? " | Solucion: " + solucion : "");
    }
}
