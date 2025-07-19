package com.example.BackendProject.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entidad que representa un control de calidad en el sistema.
 * Los controles de calidad se realizan en diferentes etapas del proceso productivo.
 */
@Entity
@Table(name = "control_calidad")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ControlCalidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "etapa_control", nullable = false, length = 50)
    private String etapaControl;  // materia_prima, proceso, final

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, length = 20)
    private String resultado;     // Aprobado, Rechazado, Pendiente

    @Column(name = "fecha_control", nullable = false)
    private LocalDate fechaControl;

    @ManyToOne(fetch = FetchType.LAZY)  // Cambiado a LAZY para mejor rendimiento
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)  // Cambiado a LAZY para mejor rendimiento
    @JoinColumn(name = "responsable_id", nullable = false)
    private Usuario responsable;

    /**
     * Constructor para crear un control de calidad
     */
    public ControlCalidad(String etapaControl, String descripcion, String resultado,
                          Producto producto, Usuario responsable) {
        this.etapaControl = etapaControl;
        this.descripcion = descripcion;
        this.resultado = resultado;
        this.producto = producto;
        this.responsable = responsable;
        this.fechaControl = LocalDate.now();
    }

    /**
     * Enum para las etapas de control
     */
    public enum EtapaControl {
        MATERIA_PRIMA("materia_prima"),
        PROCESO("proceso"),
        FINAL("final");

        private final String valor;

        EtapaControl(String valor) {
            this.valor = valor;
        }

        public String getValor() {
            return valor;
        }
    }

    /**
     * Enum para los resultados
     */
    public enum Resultado {
        APROBADO("Aprobado"),
        RECHAZADO("Rechazado"),
        PENDIENTE("Pendiente");

        private final String valor;

        Resultado(String valor) {
            this.valor = valor;
        }

        public String getValor() {
            return valor;
        }
    }
}