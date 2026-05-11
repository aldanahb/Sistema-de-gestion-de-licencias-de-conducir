package com.gestionlicencias.gestionlicenciasconducir.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gestionlicencias.gestionlicenciasconducir.model.Titular;
import com.gestionlicencias.gestionlicenciasconducir.model.Tramite;

public interface TramiteRepository extends JpaRepository<Tramite, Integer> {
    
    Tramite findFirstByTitularAsociadoOrderByFechaDesc(Titular titular);
    Tramite findByIdTramite(Integer idTramite);
    long countByTitularAsociadoAndLicenciaAsociada_ClaseAndDescripcion(Titular titularAsociado, String clase, String descripcion);

    @Query("""
        SELECT COUNT(t) FROM Tramite t
        WHERE t.titularAsociado = :titular
        AND t.licenciaAsociada.clase = :claseLicencia
        AND LOWER(t.descripcion) LIKE LOWER(CONCAT(:inicio, '%'))
    """)
    long contarCopiasPorInicioTipoTramite(
        @Param("titular") Titular titular,
        @Param("claseLicencia") String claseLicencia,
        @Param("inicio") String inicio
    );
}
