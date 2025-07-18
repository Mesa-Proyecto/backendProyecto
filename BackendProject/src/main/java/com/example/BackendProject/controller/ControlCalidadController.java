package com.example.BackendProject.controller;

import com.example.BackendProject.dto.ControlCalidadDTO;
import com.example.BackendProject.entity.ControlCalidad;
import com.example.BackendProject.service.ControlCalidadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador REST para la gestión de controles de calidad.
 */
@RestController
@RequestMapping("/api/control-calidad")
@Tag(name = "Control de Calidad", description = "API para gestión de controles de calidad")
public class ControlCalidadController {

    @Autowired
    private ControlCalidadService controlCalidadService;

    /**
     * Crea un nuevo control de calidad
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('CALIDAD') or hasAuthority('PRODUCCION')")
    @Operation(
            summary = "Crear nuevo control de calidad",
            description = "Crea un nuevo control de calidad en el sistema"
    )
    public ResponseEntity<?> crearControl(
            @Valid @RequestBody ControlCalidadDTO controlDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("errors", errors);
            errorResponse.put("statusCode", HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        try {
            ControlCalidad nuevoControl = controlCalidadService.crearControl(controlDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", HttpStatus.CREATED.value());
            response.put("message", "Control de calidad creado exitosamente");
            response.put("data", nuevoControl);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (ResponseStatusException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("statusCode", e.getStatusCode().value());
            errorResponse.put("message", e.getReason());
            return new ResponseEntity<>(errorResponse, e.getStatusCode());
        }
    }

    /**
     * Obtiene todos los controles de calidad
     */
    @GetMapping
    @Operation(
            summary = "Obtener todos los controles de calidad",
            description = "Retorna una lista con todos los controles de calidad registrados"
    )
    public ResponseEntity<?> obtenerTodosLosControles(
            @RequestParam(value = "productoId", required = false) Long productoId,
            @RequestParam(value = "etapa", required = false) String etapaControl,
            @RequestParam(value = "resultado", required = false) String resultado) {

        List<ControlCalidad> controles;

        // Aplicar filtros si se proporcionan
        if (productoId != null || etapaControl != null || resultado != null) {
            controles = controlCalidadService.buscarControlesConFiltros(productoId, etapaControl, resultado);
        } else {
            controles = controlCalidadService.obtenerTodosLosControles();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", HttpStatus.OK.value());
        response.put("message", "Controles obtenidos exitosamente");
        response.put("data", controles);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Obtiene un control de calidad por su ID
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener control de calidad por ID",
            description = "Retorna un control de calidad específico según su ID"
    )
    public ResponseEntity<?> obtenerControlPorId(@PathVariable Long id) {
        try {
            ControlCalidad control = controlCalidadService.obtenerControlPorId(id);

            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", HttpStatus.OK.value());
            response.put("message", "Control obtenido exitosamente");
            response.put("data", control);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("statusCode", HttpStatus.NOT_FOUND.value());
            errorResponse.put("message", "Control de calidad no encontrado");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Actualiza un control de calidad existente
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('CALIDAD') or hasAuthority('PRODUCCION')")
    @Operation(
            summary = "Actualizar control de calidad",
            description = "Actualiza la información de un control de calidad existente"
    )
    public ResponseEntity<?> actualizarControl(
            @PathVariable Long id,
            @Valid @RequestBody ControlCalidadDTO controlDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("errors", errors);
            errorResponse.put("statusCode", HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        try {
            ControlCalidad controlActualizado = controlCalidadService.actualizarControl(id, controlDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", HttpStatus.OK.value());
            response.put("message", "Control actualizado exitosamente");
            response.put("data", controlActualizado);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ResponseStatusException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("statusCode", e.getStatusCode().value());
            errorResponse.put("message", e.getReason());
            return new ResponseEntity<>(errorResponse, e.getStatusCode());
        }
    }

    /**
     * Elimina un control de calidad
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(
            summary = "Eliminar control de calidad",
            description = "Elimina un control de calidad del sistema"
    )
    public ResponseEntity<?> eliminarControl(@PathVariable Long id) {
        try {
            controlCalidadService.eliminarControl(id);

            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", HttpStatus.NO_CONTENT.value());
            response.put("message", "Control eliminado exitosamente");

            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("statusCode", HttpStatus.NOT_FOUND.value());
            errorResponse.put("message", "Control de calidad no encontrado");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Obtiene controles de calidad por producto
     */
    @GetMapping("/producto/{productoId}")
    @Operation(
            summary = "Obtener controles por producto",
            description = "Retorna todos los controles de calidad de un producto específico"
    )
    public ResponseEntity<?> obtenerControlesPorProducto(@PathVariable Long productoId) {
        List<ControlCalidad> controles = controlCalidadService.obtenerControlesPorProducto(productoId);

        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", HttpStatus.OK.value());
        response.put("message", "Controles del producto obtenidos exitosamente");
        response.put("data", controles);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Obtiene controles de calidad por etapa
     */
    @GetMapping("/etapa/{etapaControl}")
    @Operation(
            summary = "Obtener controles por etapa",
            description = "Retorna todos los controles de calidad de una etapa específica"
    )
    public ResponseEntity<?> obtenerControlesPorEtapa(@PathVariable String etapaControl) {
        List<ControlCalidad> controles = controlCalidadService.obtenerControlesPorEtapa(etapaControl);

        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", HttpStatus.OK.value());
        response.put("message", "Controles de la etapa obtenidos exitosamente");
        response.put("data", controles);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Obtiene controles de calidad por resultado
     */
    @GetMapping("/resultado/{resultado}")
    @Operation(
            summary = "Obtener controles por resultado",
            description = "Retorna todos los controles de calidad con un resultado específico"
    )
    public ResponseEntity<?> obtenerControlesPorResultado(@PathVariable String resultado) {
        List<ControlCalidad> controles = controlCalidadService.obtenerControlesPorResultado(resultado);

        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", HttpStatus.OK.value());
        response.put("message", "Controles con resultado '" + resultado + "' obtenidos exitosamente");
        response.put("data", controles);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Obtiene controles de calidad por rango de fechas
     */
    @GetMapping("/fechas")
    @Operation(
            summary = "Obtener controles por rango de fechas",
            description = "Retorna controles de calidad en un rango de fechas específico"
    )
    public ResponseEntity<?> obtenerControlesPorFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        List<ControlCalidad> controles = controlCalidadService.obtenerControlesPorFechas(fechaInicio, fechaFin);

        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", HttpStatus.OK.value());
        response.put("message", "Controles en rango de fechas obtenidos exitosamente");
        response.put("data", controles);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Verifica si un producto tiene controles completos
     */
    @GetMapping("/producto/{productoId}/controles-completos")
    @Operation(
            summary = "Verificar controles completos de producto",
            description = "Verifica si un producto tiene controles de calidad aprobados en todas las etapas"
    )
    public ResponseEntity<?> verificarControlesCompletos(@PathVariable Long productoId) {
        boolean controlesCompletos = controlCalidadService.productoTieneControlesCompletos(productoId);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("productoId", productoId);
        resultado.put("controlesCompletos", controlesCompletos);
        resultado.put("mensaje", controlesCompletos ?
                "El producto tiene controles aprobados en todas las etapas" :
                "El producto no tiene controles completos o aprobados");

        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", HttpStatus.OK.value());
        response.put("message", "Verificación completada");
        response.put("data", resultado);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Obtiene estadísticas de controles de calidad
     */
    @GetMapping("/estadisticas")
    @Operation(
            summary = "Obtener estadísticas de controles",
            description = "Retorna estadísticas generales de los controles de calidad"
    )
    public ResponseEntity<?> obtenerEstadisticas() {
        Map<String, Object> estadisticas = controlCalidadService.obtenerEstadisticas();

        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", HttpStatus.OK.value());
        response.put("message", "Estadísticas obtenidas exitosamente");
        response.put("data", estadisticas);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}