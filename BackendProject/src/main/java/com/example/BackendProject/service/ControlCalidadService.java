package com.example.BackendProject.service;

import com.example.BackendProject.dto.ControlCalidadDTO;
import com.example.BackendProject.dto.ControlCalidadResponseDTO;
import com.example.BackendProject.entity.ControlCalidad;
import com.example.BackendProject.entity.Producto;
import com.example.BackendProject.entity.Usuario;
import com.example.BackendProject.repository.ControlCalidadRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
public class ControlCalidadService {

    @Autowired
    private ControlCalidadRepository controlCalidadRepository;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Crea un nuevo control de calidad
     */
    @Transactional
    public ControlCalidad crearControl(ControlCalidadDTO controlDTO) {
        try {
            // Obtener producto y responsable
            Producto producto = productoService.obtenerProductoPorId(controlDTO.getProductoId());
            Usuario responsable = usuarioService.obtenerUserPorId(controlDTO.getResponsableId());

            // Crear el control de calidad
            ControlCalidad control = new ControlCalidad(
                    controlDTO.getEtapaControl(),
                    controlDTO.getDescripcion(),
                    controlDTO.getResultado(),
                    producto,
                    responsable
            );

            // Si se proporciona una fecha específica, usarla; sino usar la actual
            if (controlDTO.getFechaControl() != null) {
                control.setFechaControl(controlDTO.getFechaControl());
            }

            return controlCalidadRepository.save(control);

        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Producto o responsable no encontrado");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Error al crear control de calidad: " + e.getMessage());
        }
    }

    /**
     * Obtiene todos los controles de calidad (método base)
     */
    public List<ControlCalidad> obtenerTodosLosControles() {
        return controlCalidadRepository.findAll();
    }

    /**
     * Obtiene todos los controles de calidad con información completa
     */
    public List<ControlCalidadResponseDTO> obtenerTodosLosControlesConInfo() {
        List<ControlCalidad> controles = controlCalidadRepository.findAll();
        return controles.stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un control de calidad por ID con información completa
     */
    public ControlCalidadResponseDTO obtenerControlConInfoPorId(Long id) {
        ControlCalidad control = obtenerControlPorId(id);
        return convertirAResponseDTO(control);
    }

    /**
     * Busca controles con filtros y retorna información completa
     */
    public List<ControlCalidadResponseDTO> buscarControlesConFiltrosConInfo(Long productoId, String etapaControl, String resultado) {
        List<ControlCalidad> controles = controlCalidadRepository.findControlesConFiltros(productoId, etapaControl, resultado);
        return controles.stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene controles por producto con información completa
     */
    public List<ControlCalidadResponseDTO> obtenerControlesPorProductoConInfo(Long productoId) {
        List<ControlCalidad> controles = obtenerControlesPorProducto(productoId);
        return controles.stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene controles por etapa con información completa
     */
    public List<ControlCalidadResponseDTO> obtenerControlesPorEtapaConInfo(String etapaControl) {
        List<ControlCalidad> controles = obtenerControlesPorEtapa(etapaControl);
        return controles.stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene controles por resultado con información completa
     */
    public List<ControlCalidadResponseDTO> obtenerControlesPorResultadoConInfo(String resultado) {
        List<ControlCalidad> controles = obtenerControlesPorResultado(resultado);
        return controles.stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene controles por fechas con información completa
     */
    public List<ControlCalidadResponseDTO> obtenerControlesPorFechasConInfo(LocalDate fechaInicio, LocalDate fechaFin) {
        List<ControlCalidad> controles = obtenerControlesPorFechas(fechaInicio, fechaFin);
        return controles.stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un control de calidad por ID
     */
    public ControlCalidad obtenerControlPorId(Long id) {
        return controlCalidadRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Control de calidad no encontrado con ID: " + id));
    }

    /**
     * Actualiza un control de calidad
     */
    @Transactional
    public ControlCalidad actualizarControl(Long id, ControlCalidadDTO controlDTO) {
        try {
            ControlCalidad control = obtenerControlPorId(id);

            // Actualizar producto si es diferente
            if (!control.getProducto().getId().equals(controlDTO.getProductoId())) {
                Producto producto = productoService.obtenerProductoPorId(controlDTO.getProductoId());
                control.setProducto(producto);
            }

            // Actualizar responsable si es diferente
            if (!control.getResponsable().getId().equals(controlDTO.getResponsableId())) {
                Usuario responsable = usuarioService.obtenerUserPorId(controlDTO.getResponsableId());
                control.setResponsable(responsable);
            }

            // Actualizar campos
            control.setEtapaControl(controlDTO.getEtapaControl());
            control.setDescripcion(controlDTO.getDescripcion());
            control.setResultado(controlDTO.getResultado());

            if (controlDTO.getFechaControl() != null) {
                control.setFechaControl(controlDTO.getFechaControl());
            }

            return controlCalidadRepository.save(control);

        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Error al actualizar control de calidad: " + e.getMessage());
        }
    }

    /**
     * Elimina un control de calidad
     */
    @Transactional
    public void eliminarControl(Long id) {
        if (!controlCalidadRepository.existsById(id)) {
            throw new EntityNotFoundException("Control de calidad no encontrado con ID: " + id);
        }
        controlCalidadRepository.deleteById(id);
    }

    /**
     * Obtiene controles de calidad por producto
     */
    public List<ControlCalidad> obtenerControlesPorProducto(Long productoId) {
        return controlCalidadRepository.findByProductoId(productoId);
    }

    /**
     * Obtiene controles de calidad por responsable
     */
    public List<ControlCalidad> obtenerControlesPorResponsable(Long responsableId) {
        return controlCalidadRepository.findByResponsableId(responsableId);
    }

    /**
     * Obtiene controles de calidad por etapa
     */
    public List<ControlCalidad> obtenerControlesPorEtapa(String etapaControl) {
        return controlCalidadRepository.findByEtapaControl(etapaControl);
    }

    /**
     * Obtiene controles de calidad por resultado
     */
    public List<ControlCalidad> obtenerControlesPorResultado(String resultado) {
        return controlCalidadRepository.findByResultado(resultado);
    }

    /**
     * Obtiene controles de calidad por rango de fechas
     */
    public List<ControlCalidad> obtenerControlesPorFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return controlCalidadRepository.findByFechaControlBetween(fechaInicio, fechaFin);
    }

    /**
     * Busca controles con filtros opcionales
     */
    public List<ControlCalidad> buscarControlesConFiltros(Long productoId, String etapaControl, String resultado) {
        return controlCalidadRepository.findControlesConFiltros(productoId, etapaControl, resultado);
    }

    /**
     * Obtiene el último control de un producto en una etapa específica
     */
    public ControlCalidad obtenerUltimoControl(Long productoId, String etapaControl) {
        List<ControlCalidad> controles = controlCalidadRepository
                .findUltimoControlPorProductoYEtapa(productoId, etapaControl);
        return controles.isEmpty() ? null : controles.get(0);
    }

    /**
     * Obtiene estadísticas de controles de calidad
     */
    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> estadisticas = new HashMap<>();

        estadisticas.put("totalControles", controlCalidadRepository.count());
        estadisticas.put("aprobados", controlCalidadRepository.countByResultado("Aprobado"));
        estadisticas.put("rechazados", controlCalidadRepository.countByResultado("Rechazado"));
        estadisticas.put("pendientes", controlCalidadRepository.countByResultado("Pendiente"));

        return estadisticas;
    }

    /**
     * Verifica si un producto tiene controles de calidad aprobados en todas las etapas
     */
    public boolean productoTieneControlesCompletos(Long productoId) {
        String[] etapas = {"materia_prima", "proceso", "final"};

        for (String etapa : etapas) {
            ControlCalidad ultimoControl = obtenerUltimoControl(productoId, etapa);
            if (ultimoControl == null || !"Aprobado".equals(ultimoControl.getResultado())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Convierte ControlCalidad a ControlCalidadResponseDTO con información completa
     */
    public ControlCalidadResponseDTO convertirAResponseDTO(ControlCalidad control) {
        ControlCalidadResponseDTO responseDTO = new ControlCalidadResponseDTO();

        // Información básica del control
        responseDTO.setId(control.getId());
        responseDTO.setEtapaControl(control.getEtapaControl());
        responseDTO.setDescripcion(control.getDescripcion());
        responseDTO.setResultado(control.getResultado());
        responseDTO.setFechaControl(control.getFechaControl());

        // Información completa del producto
        Producto producto = control.getProducto();
        ControlCalidadResponseDTO.ProductoBasicoDTO productoDTO = new ControlCalidadResponseDTO.ProductoBasicoDTO();
        productoDTO.setId(producto.getId());
        productoDTO.setNombre(producto.getNombre());
        productoDTO.setDescripcion(producto.getDescripcion());
        productoDTO.setStock(producto.getStock());
        productoDTO.setStock_minimo(producto.getStock_minimo());
        productoDTO.setImagen(producto.getImagen());
        productoDTO.setTiempo(producto.getTiempo());
        productoDTO.setPrecioUnitario(producto.getPrecioUnitario());
        responseDTO.setProducto(productoDTO);

        // Información completa del responsable
        Usuario responsable = control.getResponsable();
        ControlCalidadResponseDTO.ResponsableBasicoDTO responsableDTO = new ControlCalidadResponseDTO.ResponsableBasicoDTO();
        responsableDTO.setId(responsable.getId());
        responsableDTO.setNombre(responsable.getNombre());
        responsableDTO.setApellido(responsable.getApellido());
        responsableDTO.setEmail(responsable.getEmail());
        responsableDTO.setTelefono(responsable.getTelefono());
        responsableDTO.setRol(responsable.getRol().getNombre());
        responseDTO.setResponsable(responsableDTO);

        return responseDTO;
    }

    /**
     * Convierte ControlCalidad a DTO con información adicional (método existente actualizado)
     */
    public ControlCalidadDTO convertirADTO(ControlCalidad control) {
        ControlCalidadDTO dto = new ControlCalidadDTO();
        dto.setId(control.getId());
        dto.setProductoId(control.getProducto().getId());
        dto.setEtapaControl(control.getEtapaControl());
        dto.setDescripcion(control.getDescripcion());
        dto.setResultado(control.getResultado());
        dto.setResponsableId(control.getResponsable().getId());
        dto.setFechaControl(control.getFechaControl());
        dto.setProductoNombre(control.getProducto().getNombre());
        dto.setResponsableNombre(control.getResponsable().getNombre() + " " +
                control.getResponsable().getApellido());

        return dto;
    }
}