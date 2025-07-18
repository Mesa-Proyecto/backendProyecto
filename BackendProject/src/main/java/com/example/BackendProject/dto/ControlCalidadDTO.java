package com.example.BackendProject.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

/**
 * DTO para la transferencia de datos de Control de Calidad entre capas
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ControlCalidadDTO {
    
    private Long id;
    
    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;
    
    @NotEmpty(message = "La etapa de control es obligatoria")
    @Pattern(regexp = "^(materia_prima|proceso|final)$", 
             message = "La etapa debe ser: materia_prima, proceso o final")
    private String etapaControl;
    
    private String descripcion;
    
    @NotEmpty(message = "El resultado es obligatorio")
    @Pattern(regexp = "^(Aprobado|Rechazado|Pendiente)$", 
             message = "El resultado debe ser: Aprobado, Rechazado o Pendiente")
    private String resultado;
    
    @NotNull(message = "El ID del responsable es obligatorio")
    private Long responsableId;
    
    private LocalDate fechaControl;
    
    // Campos adicionales para mostrar información en las respuestas
    private String productoNombre;
    private String responsableNombre;
}