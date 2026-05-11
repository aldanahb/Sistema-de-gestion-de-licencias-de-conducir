package com.gestionlicencias.gestionlicenciasconducir.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestionlicencias.gestionlicenciasconducir.model.Titular;
import com.gestionlicencias.gestionlicenciasconducir.model.TipoDocumento;

import java.util.Optional;

@Repository
public interface TitularRepository extends JpaRepository<Titular, Integer> {
    //Buscar si ya existe un titular por tipo de documento y número de documento
    boolean existsByTipoDocumentoAndDocumento(TipoDocumento tipoDocumento, String documento);
    Optional<Titular> findByTipoDocumentoAndDocumento(TipoDocumento tipoDocumento, String documento);
}
