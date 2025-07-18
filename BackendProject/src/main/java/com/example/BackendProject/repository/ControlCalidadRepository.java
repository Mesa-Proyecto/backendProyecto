package com.example.BackendProject.repository;

import com.example.BackendProject.entity.ControlCalidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ControlCalidadRepository extends JpaRepository<ControlCalidad, Long> {

    /**
     * Busca controles de calidad por producto
     */
    List<ControlCalidad> findByProductoId(Long productoId);

    /**
     * Busca controles de calidad por responsable
     */
    List<ControlCalidad> findByResponsableId(Long responsableId);

    /**
     * Busca controles de calidad por etapa
     */
    List<ControlCalidad> findByEtapaControl(String etapaControl);

    /**
     * Busca controles de calidad por resultado
     */
    List<ControlCalidad> findByResultado(String resultado);

    /**
     * Busca controles de calidad por rango de fechas
     */
    List<ControlCalidad> findByFechaControlBetween(LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Busca controles de calidad por producto y etapa
     */
    List<ControlCalidad> findByProductoIdAndEtapaControl(Long productoId, String etapaControl);

    /**
     * Busca controles de calidad pendientes (método específico)
     */
    List<ControlCalidad> findByResultadoOrderByFechaControlDesc(String resultado);

    /**
     * Cuenta controles por resultado
     */
    @Query("SELECT COUNT(c) FROM ControlCalidad c WHERE c.resultado = :resultado")
    Long countByResultado(@Param("resultado") String resultado);

    /**
     * Obtiene controles de calidad con información del producto y responsable
     */
    @Query("SELECT c FROM ControlCalidad c " +
            "JOIN FETCH c.producto p " +
            "JOIN FETCH c.responsable r " +
            "WHERE (:productoId IS NULL OR c.producto.id = :productoId) " +
            "AND (:etapaControl IS NULL OR c.etapaControl = :etapaControl) " +
            "AND (:resultado IS NULL OR c.resultado = :resultado) " +
            "ORDER BY c.fechaControl DESC")
    List<ControlCalidad> findControlesConFiltros(
            @Param("productoId") Long productoId,
            @Param("etapaControl") String etapaControl,
            @Param("resultado") String resultado
    );

    /**
     * Obtiene el último control de calidad de un producto en una etapa específica
     */
    @Query("SELECT c FROM ControlCalidad c " +
            "WHERE c.producto.id = :productoId " +
            "AND c.etapaControl = :etapaControl " +
            "ORDER BY c.fechaControl DESC")
    List<ControlCalidad> findUltimoControlPorProductoYEtapa(
            @Param("productoId") Long productoId,
            @Param("etapaControl") String etapaControl
    );
}