package com.example.BackendProject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para mostrar información completa de Control de Calidad con datos relacionados
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ControlCalidadResponseDTO {

    private Long id;
    private String etapaControl;
    private String descripcion;
    private String resultado;
    private LocalDate fechaControl;

    // Información completa del producto
    private ProductoBasicoDTO producto;

    // Información completa del responsable
    private ResponsableBasicoDTO responsable;

    /**
     * DTO básico para mostrar información del producto
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductoBasicoDTO {
        private Long id;
        private String nombre;
        private String descripcion;
        private Integer stock;
        private Integer stock_minimo;
        private String imagen;
        private String tiempo;
        private Double precioUnitario;
    }

    /**
     * DTO básico para mostrar información del responsable
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponsableBasicoDTO {
        private Long id;
        private String nombre;
        private String apellido;
        private String email;
        private String telefono;
        private String rol;
    }
}