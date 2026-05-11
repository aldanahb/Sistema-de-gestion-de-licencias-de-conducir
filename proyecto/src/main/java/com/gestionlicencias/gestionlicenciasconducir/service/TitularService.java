package com.gestionlicencias.gestionlicenciasconducir.service;

import java.util.List;

import com.gestionlicencias.gestionlicenciasconducir.model.TipoDocumento;
import com.gestionlicencias.gestionlicenciasconducir.model.Titular;
import com.gestionlicencias.gestionlicenciasconducir.dto.TitularRecord;

public interface TitularService {
    Titular registrarTitular(TitularRecord titularRecord);
    List<Titular> listarTitulares();
    TitularRecord buscarTitular(TipoDocumento tipoDocumento, String documento);
    Titular buscarTitularDocumento(TipoDocumento tipoDocumento, String documento);
    void actualizarTitular(TipoDocumento tipoDocumento, String documento, TitularRecord titularModificado);
    List<Titular> buscarTitulares(String apellido, TipoDocumento tipoDocumento, String documento);
}
