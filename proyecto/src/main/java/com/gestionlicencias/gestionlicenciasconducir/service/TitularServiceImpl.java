package com.gestionlicencias.gestionlicenciasconducir.service;

import java.util.List;

import com.gestionlicencias.gestionlicenciasconducir.mapper.TitularMapper;
import com.gestionlicencias.gestionlicenciasconducir.model.TipoDocumento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestionlicencias.gestionlicenciasconducir.model.Titular;
import com.gestionlicencias.gestionlicenciasconducir.repository.TitularRepository;

import jakarta.persistence.EntityNotFoundException;

import com.gestionlicencias.gestionlicenciasconducir.dto.TitularRecord;

@Service
public class TitularServiceImpl implements TitularService {

    private final TitularRepository repository;
    private final TitularMapper titularMapper;

    @Autowired
    public TitularServiceImpl(TitularRepository repository, TitularMapper titularMapper) {
        this.repository = repository;
        this.titularMapper = titularMapper;
    }

    @Override
    @Transactional
    public Titular registrarTitular(TitularRecord titularRecord) {

        if (titularRecord == null) {
            throw new IllegalArgumentException("El registro del titular no puede ser nulo");
        }

        // Verificar si ya existe un titular con el mismo documento
        if (repository.existsByTipoDocumentoAndDocumento(titularRecord.getTipoDocumento(), titularRecord.getDocumento())) {
            throw new IllegalArgumentException(
                    "Ya existe un titular con documento: " + titularRecord.getDocumento() + " y tipo de documento: " + titularRecord.getTipoDocumento()
            );
        }
        // Convertir el Record a entidad
        Titular titular = titularMapper.toEntity(titularRecord);
        //sugerencia sacar por mapper: Titular titular = titularRecord.toTitular();
        // Guardar el titular en la base de datos
        return repository.save(titular);
    }

    @Override
    public List<Titular> listarTitulares() {
        return repository.findAll();
    }

    public Titular buscarTitularDocumento(TipoDocumento tipoDocumento, String documento) {
        Titular titular = repository.findByTipoDocumentoAndDocumento(tipoDocumento, documento)
                .orElse(null);
        return titular;
    }

    @Transactional(readOnly = true)
    @Override
    public TitularRecord buscarTitular(TipoDocumento tipoDocumento, String documento) {
        Titular titular = repository.findByTipoDocumentoAndDocumento(tipoDocumento, documento)
                .orElse(null);
                //.orElseThrow(() -> new IllegalArgumentException("Titular no encontrado con documento: " + documento + " y tipo de documento: " + tipoDocumento));
        return titularMapper.toRecord(titular);
        //checkear aca que no devuelva null
        /*
            return new TitularRecord(
              titular.getTipoDocumento(),
              titular.getDocumento(),
              titular.getNombre(),
              titular.getApellido(),
              titular.getFechaNacimiento(),
              titular.getDireccion(),
              titular.getGrupoSanguineo(),
              titular.getFactorRH(),
              titular.getDonanteOrganos()
            );
        */
    }

    public void actualizarTitular(TipoDocumento tipoDocumento, String documento, TitularRecord titularModificado) {
        Titular titularExistente = repository
            .findByTipoDocumentoAndDocumento(tipoDocumento, documento)
            .orElseThrow(() -> new EntityNotFoundException("Titular no encontrado"));

        // Actualizar campos (excepto tipo y nro documento)
        titularExistente.setNombre(titularModificado.nombre());
        titularExistente.setApellido(titularModificado.apellido());
        titularExistente.setFechaNacimiento(titularModificado.fechaNacimiento());
        titularExistente.setDireccion(titularModificado.direccion());
        titularExistente.setGrupoSanguineo(titularModificado.grupoSanguineo());
        titularExistente.setFactorRH(titularModificado.factorRH());
        titularExistente.setDonanteOrganos(titularModificado.donanteOrganos());
        titularExistente.setModificado(true);

        repository.save(titularExistente);
    }

    public void actualizarModificaciones(Titular titular) {
        titular.setModificado(false);
        repository.save(titular);
    }

    @Override
    public List<Titular> buscarTitulares(String apellido, TipoDocumento tipoDocumento, String documento) {
        List<Titular> todos = repository.findAll();

        return todos.stream()
            .filter(t -> {
                boolean coincideApellido = (apellido == null || apellido.isBlank()) ||
                        t.getApellido().toLowerCase().contains(apellido.toLowerCase());
                boolean coincideTipoDoc = (tipoDocumento == null) ||
                        tipoDocumento.equals(t.getTipoDocumento());
                boolean coincideDocumento = (documento == null || documento.isBlank()) ||
                        documento.equals(t.getDocumento());
                return coincideApellido && coincideTipoDoc && coincideDocumento;
            })
            .toList();
    }

    
}
