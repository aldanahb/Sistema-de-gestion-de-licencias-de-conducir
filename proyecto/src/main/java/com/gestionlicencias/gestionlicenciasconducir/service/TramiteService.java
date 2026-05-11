package com.gestionlicencias.gestionlicenciasconducir.service;
import com.gestionlicencias.gestionlicenciasconducir.model.Titular;
import com.gestionlicencias.gestionlicenciasconducir.model.Tramite;

public interface TramiteService {
    public Tramite registrarTramite(Tramite tramite);
    public Tramite obtenerUltimoTramiteTitular(Titular titular);
    public int contarCopiasPorTitularYClase(Titular titular, String claseLicencia);
    public Tramite buscarPorId(Integer id);
}
