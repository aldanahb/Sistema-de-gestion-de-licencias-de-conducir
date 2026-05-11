package com.gestionlicencias.gestionlicenciasconducir.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.gestionlicencias.gestionlicenciasconducir.mapper.TitularMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gestionlicencias.gestionlicenciasconducir.dto.TitularRecord;
import com.gestionlicencias.gestionlicenciasconducir.model.Titular;
import com.gestionlicencias.gestionlicenciasconducir.model.TipoDocumento;
import com.gestionlicencias.gestionlicenciasconducir.repository.TitularRepository;

@ExtendWith(MockitoExtension.class)
class TitularServiceImplTest {

    @Mock
    private TitularRepository titularRepository;

    @Mock
    private TitularMapper titularMapper;

    private TitularService titularService;

    @BeforeEach
    void setUp() {
        titularService = new TitularServiceImpl(titularRepository, titularMapper);
    }

    @Test
    void testCrearTitularExitoso() {
        // Arrange
        TitularRecord titularRecord = new TitularRecord(
            TipoDocumento.DNI,
            "12345678",
            "Juan",
            "Pérez",
            Date.from(LocalDate.of(1990, 1, 1).atStartOfDay().toInstant(java.time.ZoneOffset.UTC)),
            "Calle Principal 123",
            "A",
            "Positivo",
            true
        );

        //Titular titularEsperado = titularRecord.toTitular();
        Titular titularEsperado = new Titular();
            titularEsperado.setTipoDocumento(TipoDocumento.DNI);
            titularEsperado.setDocumento("12345678");
            titularEsperado.setNombre("Juan");
            titularEsperado.setApellido("Pérez");
            titularEsperado.setFechaNacimiento(Date.from(LocalDate.of(1990, 1, 1).atStartOfDay().toInstant(java.time.ZoneOffset.UTC)));
            titularEsperado.setDireccion("Calle Principal 123");
            titularEsperado.setGrupoSanguineo("A");
            titularEsperado.setFactorRH("Positivo");
            titularEsperado.setDonanteOrganos(true);

        when(titularMapper.toEntity(titularRecord)).thenReturn(titularEsperado);
        when(titularRepository.existsByTipoDocumentoAndDocumento(TipoDocumento.DNI, "12345678")).thenReturn(false);
        when(titularRepository.save(any(Titular.class))).thenReturn(titularEsperado);

        // Act
        Titular titularCreado = titularService.registrarTitular(titularRecord);

        // Assert
        assertNotNull(titularCreado);
        assertEquals(titularRecord.tipoDocumento(), titularCreado.getTipoDocumento());
        assertEquals(titularRecord.documento(), titularCreado.getDocumento());
        assertEquals(titularRecord.nombre(), titularCreado.getNombre());
        assertEquals(titularRecord.apellido(), titularCreado.getApellido());
        assertEquals(titularRecord.fechaNacimiento(), titularCreado.getFechaNacimiento());
        assertEquals(titularRecord.direccion(), titularCreado.getDireccion());
        assertEquals(titularRecord.grupoSanguineo(), titularCreado.getGrupoSanguineo());
        assertEquals(titularRecord.factorRH(), titularCreado.getFactorRH());
        assertEquals(titularRecord.donanteOrganos(), titularCreado.getDonanteOrganos());

        // Verify
        verify(titularMapper).toEntity(titularRecord);
        verify(titularRepository).existsByTipoDocumentoAndDocumento(TipoDocumento.DNI, "12345678");
        verify(titularRepository).save(titularEsperado);
    }

    @Test
    void testCrearTitularDocumentoDuplicado() {
        // Arrange
        TitularRecord titularRecord = new TitularRecord(
            TipoDocumento.DNI,
            "12345678",
            "Juan",
            "Pérez",
            Date.from(LocalDate.of(1990, 1, 1).atStartOfDay().toInstant(java.time.ZoneOffset.UTC)),
            "Calle Principal 123",
            "A",
            "Positivo",
            true
        );

        when(titularRepository.existsByTipoDocumentoAndDocumento(TipoDocumento.DNI, "12345678")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> titularService.registrarTitular(titularRecord),
            "Debería lanzar excepción cuando el documento ya existe"
        );

        assertTrue(exception.getMessage().contains("Ya existe un titular con documento: 12345678 y tipo de documento: DNI"));

        // Verify
        verify(titularRepository).existsByTipoDocumentoAndDocumento(TipoDocumento.DNI, "12345678");
        verify(titularRepository, never()).save(any(Titular.class));
    }

    @Test
    void buscarTitular_existente_retornaTitularRecord() {
        // Arrange
        TipoDocumento tipoDocumento = TipoDocumento.DNI;
        String documento = "12345678";
        
        Titular titular = new Titular();
        titular.setTipoDocumento(tipoDocumento);
        titular.setDocumento(documento);
        titular.setNombre("Juan");
        titular.setApellido("Pérez");
        titular.setFechaNacimiento(java.sql.Date.valueOf("1990-01-01"));
        titular.setDireccion("Calle Principal 123");
        titular.setGrupoSanguineo("A");
        titular.setFactorRH("+");
        titular.setDonanteOrganos(true);

        TitularRecord titularRecord = new TitularRecord(
            tipoDocumento, documento, "Juan", "Pérez", 
            java.sql.Date.valueOf("1990-01-01"), "Calle Principal 123", "A", "+", true
        );

        when(titularRepository.findByTipoDocumentoAndDocumento(tipoDocumento, documento))
            .thenReturn(Optional.of(titular));
        when(titularMapper.toRecord(titular)).thenReturn(titularRecord);

        // Act
        TitularRecord resultado = titularService.buscarTitular(tipoDocumento, documento);

        // Assert
        assertNotNull(resultado);
        assertEquals(tipoDocumento, resultado.tipoDocumento());
        assertEquals(documento, resultado.documento());
        assertEquals("Juan", resultado.nombre());
        assertEquals("Pérez", resultado.apellido());
        assertEquals("Calle Principal 123", resultado.direccion());
        assertEquals("A", resultado.grupoSanguineo());
        assertEquals("+", resultado.factorRH());
        assertTrue(resultado.donanteOrganos());
    }

    @Test
    void buscarTitular_noExistente_retornaNull() {
        // Arrange
        TipoDocumento tipoDocumento = TipoDocumento.DNI;
        String documento = "99999999";

        when(titularRepository.findByTipoDocumentoAndDocumento(tipoDocumento, documento))
            .thenReturn(Optional.empty());

        // Act
        TitularRecord resultado = titularService.buscarTitular(tipoDocumento, documento);

        // Assert
        assertNull(resultado);
    }

    @Test
    void buscarTitularDocumento_existente_retornaTitular() {
        // Arrange
        TipoDocumento tipoDocumento = TipoDocumento.DNI;
        String documento = "12345678";
        
        Titular titular = new Titular();
        titular.setTipoDocumento(tipoDocumento);
        titular.setDocumento(documento);
        titular.setNombre("Juan");
        titular.setApellido("Pérez");

        when(titularRepository.findByTipoDocumentoAndDocumento(tipoDocumento, documento))
            .thenReturn(Optional.of(titular));

        // Act
        Titular resultado = titularService.buscarTitularDocumento(tipoDocumento, documento);

        // Assert
        assertNotNull(resultado);
        assertEquals(tipoDocumento, resultado.getTipoDocumento());
        assertEquals(documento, resultado.getDocumento());
        assertEquals("Juan", resultado.getNombre());
        assertEquals("Pérez", resultado.getApellido());
    }

    @Test
    void buscarTitularDocumento_noExistente_retornaNull() {
        // Arrange
        TipoDocumento tipoDocumento = TipoDocumento.DNI;
        String documento = "99999999";

        when(titularRepository.findByTipoDocumentoAndDocumento(tipoDocumento, documento))
            .thenReturn(Optional.empty());

        // Act
        Titular resultado = titularService.buscarTitularDocumento(tipoDocumento, documento);

        // Assert
        assertNull(resultado);
    }

    @Test
    void buscarTitulares_sinFiltros_retornaTodosLosTitulares() {
        // Arrange
        Titular titular1 = new Titular();
        titular1.setTipoDocumento(TipoDocumento.DNI);
        titular1.setDocumento("12345678");
        titular1.setNombre("Juan");
        titular1.setApellido("Pérez");

        Titular titular2 = new Titular();
        titular2.setTipoDocumento(TipoDocumento.PASAPORTE);
        titular2.setDocumento("AB123456");
        titular2.setNombre("Ana");
        titular2.setApellido("García");

        when(titularRepository.findAll()).thenReturn(List.of(titular1, titular2));

        // Act
        List<Titular> resultado = titularService.buscarTitulares(null, null, null);

        // Assert
        assertEquals(2, resultado.size());
        assertEquals("Juan", resultado.get(0).getNombre());
        assertEquals("Ana", resultado.get(1).getNombre());
    }

    @Test
    void buscarTitulares_conFiltroApellido_retornaTitularesFiltrados() {
        // Arrange
        String apellido = "Pérez";
        
        Titular titular1 = new Titular();
        titular1.setTipoDocumento(TipoDocumento.DNI);
        titular1.setDocumento("12345678");
        titular1.setNombre("Juan");
        titular1.setApellido("Pérez");

        Titular titular2 = new Titular();
        titular2.setTipoDocumento(TipoDocumento.DNI);
        titular2.setDocumento("87654321");
        titular2.setNombre("María");
        titular2.setApellido("García");

        when(titularRepository.findAll()).thenReturn(List.of(titular1, titular2));

        // Act
        List<Titular> resultado = titularService.buscarTitulares(apellido, null, null);

        // Assert
        assertEquals(1, resultado.size());
        assertEquals("Pérez", resultado.get(0).getApellido());
        assertEquals("Juan", resultado.get(0).getNombre());
    }

    @Test
    void buscarTitulares_conFiltroTipoDocumento_retornaTitularesFiltrados() {
        // Arrange
        TipoDocumento tipoDocumento = TipoDocumento.PASAPORTE;
        
        Titular titular1 = new Titular();
        titular1.setTipoDocumento(TipoDocumento.DNI);
        titular1.setDocumento("12345678");
        titular1.setNombre("Juan");
        titular1.setApellido("Pérez");

        Titular titular2 = new Titular();
        titular2.setTipoDocumento(TipoDocumento.PASAPORTE);
        titular2.setDocumento("AB123456");
        titular2.setNombre("Ana");
        titular2.setApellido("García");

        when(titularRepository.findAll()).thenReturn(List.of(titular1, titular2));

        // Act
        List<Titular> resultado = titularService.buscarTitulares(null, tipoDocumento, null);

        // Assert
        assertEquals(1, resultado.size());
        assertEquals(TipoDocumento.PASAPORTE, resultado.get(0).getTipoDocumento());
        assertEquals("AB123456", resultado.get(0).getDocumento());
    }

    @Test
    void buscarTitulares_conFiltroDocumento_retornaTitularEspecifico() {
        // Arrange
        String documento = "12345678";
        
        Titular titular1 = new Titular();
        titular1.setTipoDocumento(TipoDocumento.DNI);
        titular1.setDocumento("12345678");
        titular1.setNombre("Juan");
        titular1.setApellido("Pérez");

        Titular titular2 = new Titular();
        titular2.setTipoDocumento(TipoDocumento.DNI);
        titular2.setDocumento("87654321");
        titular2.setNombre("Ana");
        titular2.setApellido("García");

        when(titularRepository.findAll()).thenReturn(List.of(titular1, titular2));

        // Act
        List<Titular> resultado = titularService.buscarTitulares(null, null, documento);

        // Assert
        assertEquals(1, resultado.size());
        assertEquals("12345678", resultado.get(0).getDocumento());
        assertEquals("Juan", resultado.get(0).getNombre());
    }

    @Test
    void buscarTitulares_conFiltrosCombinados_retornaTitularesFiltrados() {
        // Arrange
        String apellido = "Pérez";
        TipoDocumento tipoDocumento = TipoDocumento.DNI;
        String documento = "12345678";
        
        Titular titular1 = new Titular();
        titular1.setTipoDocumento(TipoDocumento.DNI);
        titular1.setDocumento("12345678");
        titular1.setNombre("Juan");
        titular1.setApellido("Pérez");

        Titular titular2 = new Titular();
        titular2.setTipoDocumento(TipoDocumento.DNI);
        titular2.setDocumento("87654321");
        titular2.setNombre("María");
        titular2.setApellido("Pérez");

        Titular titular3 = new Titular();
        titular3.setTipoDocumento(TipoDocumento.PASAPORTE);
        titular3.setDocumento("AB123456");
        titular3.setNombre("Ana");
        titular3.setApellido("García");

        when(titularRepository.findAll()).thenReturn(List.of(titular1, titular2, titular3));

        // Act
        List<Titular> resultado = titularService.buscarTitulares(apellido, tipoDocumento, documento);

        // Assert
        assertEquals(1, resultado.size());
        assertEquals("Pérez", resultado.get(0).getApellido());
        assertEquals(TipoDocumento.DNI, resultado.get(0).getTipoDocumento());
        assertEquals("12345678", resultado.get(0).getDocumento());
    }

    @Test
    void buscarTitulares_conFiltroApellidoCaseInsensitive_retornaTitularesFiltrados() {
        // Arrange
        String apellido = "pérez"; // minúsculas
        
        Titular titular1 = new Titular();
        titular1.setTipoDocumento(TipoDocumento.DNI);
        titular1.setDocumento("12345678");
        titular1.setNombre("Juan");
        titular1.setApellido("Pérez");

        Titular titular2 = new Titular();
        titular2.setTipoDocumento(TipoDocumento.DNI);
        titular2.setDocumento("87654321");
        titular2.setNombre("María");
        titular2.setApellido("García");

        when(titularRepository.findAll()).thenReturn(List.of(titular1, titular2));

        // Act
        List<Titular> resultado = titularService.buscarTitulares(apellido, null, null);

        // Assert
        assertEquals(1, resultado.size());
        assertEquals("Pérez", resultado.get(0).getApellido());
    }

    @Test
    void buscarTitulares_conFiltroApellidoParcial_retornaTitularesFiltrados() {
        // Arrange
        String apellido = "Pér"; // búsqueda parcial
        
        Titular titular1 = new Titular();
        titular1.setTipoDocumento(TipoDocumento.DNI);
        titular1.setDocumento("12345678");
        titular1.setNombre("Juan");
        titular1.setApellido("Pérez");

        Titular titular2 = new Titular();
        titular2.setTipoDocumento(TipoDocumento.DNI);
        titular2.setDocumento("87654321");
        titular2.setNombre("María");
        titular2.setApellido("García");

        when(titularRepository.findAll()).thenReturn(List.of(titular1, titular2));

        // Act
        List<Titular> resultado = titularService.buscarTitulares(apellido, null, null);

        // Assert
        assertEquals(1, resultado.size());
        assertEquals("Pérez", resultado.get(0).getApellido());
    }

    @Test
    void buscarTitulares_sinResultados_retornaListaVacia() {
        // Arrange
        String apellido = "Inexistente";
        
        Titular titular1 = new Titular();
        titular1.setTipoDocumento(TipoDocumento.DNI);
        titular1.setDocumento("12345678");
        titular1.setNombre("Juan");
        titular1.setApellido("Pérez");

        when(titularRepository.findAll()).thenReturn(List.of(titular1));

        // Act
        List<Titular> resultado = titularService.buscarTitulares(apellido, null, null);

        // Assert
        assertTrue(resultado.isEmpty());
    }
}
