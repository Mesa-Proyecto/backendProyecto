package com.example.BackendProject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.BackendProject.entity.Detalle_Devolucion;
import com.example.BackendProject.entity.Devolucion;

@Repository
public interface DetalleDevolucionRepository extends JpaRepository<Detalle_Devolucion, Long> {
    List<Detalle_Devolucion> findByDevolucion(Devolucion devolucion);
    List<Detalle_Devolucion> findByDevolucion_Id(Long devolucionId);
}