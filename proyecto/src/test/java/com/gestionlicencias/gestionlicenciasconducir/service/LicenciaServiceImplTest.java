package com.gestionlicencias.gestionlicenciasconducir.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.time.LocalDate;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.MediaType;

import com.gestionlicencias.gestionlicenciasconducir.dto.LicenciaRecord;
import com.gestionlicencias.gestionlicenciasconducir.dto.LicenciaListadoRecord;
import com.gestionlicencias.gestionlicenciasconducir.model.Licencia;
import com.gestionlicencias.gestionlicenciasconducir.model.TipoDocumento;
import com.gestionlicencias.gestionlicenciasconducir.model.Titular;
import com.gestionlicencias.gestionlicenciasconducir.model.Tramite;
import com.gestionlicencias.gestionlicenciasconducir.repository.LicenciaRepository;

class LicenciaServiceImplTest {

    private LicenciaService licenciaService;
    private LicenciaRepository licenciaRepository;
    private TramiteService tramiteService;
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        licenciaRepository = mock(LicenciaRepository.class);
        tramiteService = mock(TramiteService.class);
        usuarioService = mock(UsuarioService.class);
        licenciaService = new LicenciaServiceImpl(licenciaRepository, tramiteService, usuarioService);
    }

    @ParameterizedTest
    @CsvSource({
        "A, 5, 48.0",  // 8 + 40
        "A, 4, 38.0",  // 8 + 30
        "A, 3, 33.0",  // 8 + 25
        "A, 1, 28.0",  // 8 + 20
        "B, 5, 48.0",  // 8 + 40
        "B, 4, 38.0",  // 8 + 30
        "B, 3, 33.0",  // 8 + 25
        "B, 1, 28.0",  // 8 + 20
        "C, 5, 55.0",  // 8 + 47
        "C, 4, 43.0",  // 8 + 35
        "C, 3, 38.0",  // 8 + 30
        "C, 1, 31.0",  // 8 + 23
        "E, 5, 67.0",  // 8 + 59
        "E, 4, 52.0",  // 8 + 44
        "E, 3, 47.0",  // 8 + 39
        "E, 1, 37.0",  // 8 + 29
        "G, 5, 48.0",  // 8 + 40
        "G, 4, 38.0",  // 8 + 30
        "G, 3, 33.0",  // 8 + 25
        "G, 1, 28.0"   // 8 + 20
    })
    void calcularCostoLicencia_casoValido_retornaCostoEsperado(String clase, int vigencia, float costoEsperado) {
        float costoCalculado = licenciaService.calcularCostoLicencia(clase, vigencia);
        assertEquals(costoEsperado, costoCalculado, 0.01, 
            "El costo calculado para clase " + clase + " y vigencia " + vigencia + " años debe ser " + costoEsperado);
    }

    @Test
    void calcularCostoLicencia_claseInvalida_lanzaExcepcion() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> licenciaService.calcularCostoLicencia("X", 5),
            "Debería lanzar excepción para clase inválida"
        );

        assertTrue(exception.getMessage().contains("Clase de licencia no válida"));
    }

    @Test
    void calcularCostoLicencia_vigenciaInvalida_lanzaExcepcion() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> licenciaService.calcularCostoLicencia("A", 2),
            "Debería lanzar excepción para vigencia inválida"
        );

        assertTrue(exception.getMessage().contains("Vigencia no válida"));
    }

    @Test
    void calcularCostoLicencia_claseMinuscula_funcionaCorrectamente() {
        float costoCalculado = licenciaService.calcularCostoLicencia("a", 5);
        assertEquals(48.0, costoCalculado, 0.01, 
            "El costo debe ser calculado correctamente independientemente de mayúsculas/minúsculas");
    }

    @Test
    void calcularCostoLicencia_claseNula_lanzaExcepcion() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> licenciaService.calcularCostoLicencia(null, 5),
            "Debería lanzar excepción para clase nula"
        );

        assertTrue(exception.getMessage().contains("Clase de licencia no válida"));
    }

    @Test
    void calcularCostoLicencia_vigenciaNula_lanzaExcepcion() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> licenciaService.calcularCostoLicencia("A", null),
            "Debería lanzar excepción para vigencia nula"
        );

        assertTrue(exception.getMessage().contains("Vigencia no válida"));
    }

    @Test
    void buscarLicenciasVigentes_filtraPorNombreYDonante() {
        Titular t = new Titular();
        t.setTipoDocumento(TipoDocumento.DNI);
        t.setDocumento("12345678");
        t.setNombre("Ana");
        t.setApellido("Martínez");
        t.setFechaNacimiento(Date.valueOf(LocalDate.of(1990, 1, 1)));
        t.setDireccion("Calle A");
        t.setGrupoSanguineo("A");
        t.setFactorRH("+");
        t.setDonanteOrganos(true);

        Licencia l = new Licencia();
        l.setClase("B");
        l.setObservaciones("Obs");
        l.setFechaVencimiento(Date.valueOf(LocalDate.now().plusDays(90)));
        l.setTitular(t);

        when(licenciaRepository.findByFechaVencimientoAfter(any(LocalDate.class)))
            .thenReturn(List.of(l));

        List<LicenciaRecord> resultado = licenciaService
            .buscarLicenciasVigentes("ana", "A", "+", true);

        assertEquals(1, resultado.size());
        assertEquals("Ana", resultado.get(0).titular().nombre());
    }

    @Test
    void buscarLicenciasVigentes_sinCoincidencias_retornaListaVacia() {
        when(licenciaRepository.findByFechaVencimientoAfter(any(LocalDate.class)))
            .thenReturn(List.of());

        List<LicenciaRecord> resultado = licenciaService
            .buscarLicenciasVigentes("Pedro", "0", "0", false);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarLicenciasVigentes_filtraSoloGrupoSanguineo() {
        Titular a = new Titular();
        a.setNombre("Mario"); a.setGrupoSanguineo("O"); a.setFactorRH("+");
        a.setFechaNacimiento(Date.valueOf(LocalDate.of(1980, 5, 1))); a.setDonanteOrganos(false);
        Licencia la = new Licencia(); la.setClase("A");
        la.setFechaVencimiento(Date.valueOf(LocalDate.now().plusMonths(6))); la.setTitular(a);

        Titular b = new Titular();
        b.setNombre("Silvia"); b.setGrupoSanguineo("A"); b.setFactorRH("+");
        b.setFechaNacimiento(Date.valueOf(LocalDate.of(1990, 7, 1))); b.setDonanteOrganos(false);
        Licencia lb = new Licencia(); lb.setClase("B");
        lb.setFechaVencimiento(Date.valueOf(LocalDate.now().plusMonths(6))); lb.setTitular(b);

        when(licenciaRepository.findByFechaVencimientoAfter(any(LocalDate.class)))
            .thenReturn(List.of(la, lb));

        List<LicenciaRecord> r = licenciaService.buscarLicenciasVigentes(
            "",        // nombreApellido    
            "O",       // grupoSanguineo    
            "",        // factorRH          
            false);    // donanteOrganos    

        assertEquals(1, r.size());
        assertEquals("Mario", r.get(0).titular().nombre());
    }

    @Test
    void emitirCopiaLicencia_registraTramiteCorrectamente() {
        Titular titular = new Titular();
        titular.setNombre("Juan");
        titular.setApellido("Pérez");

        Licencia licencia = new Licencia();
        licencia.setClase("B");
        licencia.setTitular(titular);

        Tramite tramiteMock = new Tramite();
        tramiteMock.setIdTramite(1);
        tramiteMock.setDescripcion("Emisión de copia de licencia clase B: duplicado");

        when(tramiteService.contarCopiasPorTitularYClase(titular, "B")).thenReturn(0);
        when(usuarioService.buscarUsuarioPorId(1)).thenReturn(null); // Simulamos que devuelve null
        when(tramiteService.registrarTramite(any(Tramite.class))).thenReturn(tramiteMock);

        Tramite resultado = licenciaService.emitirCopiaLicencia(licencia, titular);

        assertNotNull(resultado);
        assertEquals("Emisión de copia de licencia clase B: duplicado", resultado.getDescripcion());
        assertEquals(1, resultado.getIdTramite());
    }

    @Test
    void renovarLicencia_actualizaVigenciaYRegistraTramite() {
        // Setup de titular y licencia vencida
        Titular titular = new Titular();
        titular.setNombre("Laura");
        titular.setApellido("Sosa");
        titular.setFechaNacimiento(Date.valueOf(LocalDate.of(1990, 6, 15)));

        Licencia licencia = new Licencia();
        licencia.setClase("B");
        licencia.setTitular(titular);
        licencia.setFechaVencimiento(Date.valueOf(LocalDate.of(2020, 6, 15)));

        // Trámite esperado
        Tramite tramiteMock = new Tramite();
        tramiteMock.setIdTramite(123);
        tramiteMock.setDescripcion("Renovación de licencia de conducir clase B");

        // Mock comportamiento de usuario y trámite
        when(usuarioService.buscarUsuarioPorId(1)).thenReturn(null); // no importa usuario por ahora
        when(tramiteService.registrarTramite(any(Tramite.class))).thenReturn(tramiteMock);

        // Ejecutar renovación
        Tramite resultado = licenciaService.renovarLicencia(licencia);

        // Verificaciones
        assertNotNull(resultado);
        assertEquals("Renovación de licencia de conducir clase B", resultado.getDescripcion());

        // Validar que la licencia se haya actualizado con una nueva fecha
        LocalDate vencimientoEsperado = LocalDate.of(
            LocalDate.now().getYear() + 5,
            6, 15 
        );
        assertEquals(Date.valueOf(vencimientoEsperado), licencia.getFechaVencimiento());
    }

    @Test
    void sePuedeRenovar_licenciaVigente_titularNoModificado_retornaFalse() {
        // Arrange
        Titular titular = new Titular();
        titular.setModificado(false); // No hubo cambios en los datos del titular

        Licencia licencia = new Licencia();
        licencia.setEstaVigente(true); // La licencia sigue vigente
        licencia.setTitular(titular);

        // Act
        boolean resultado = licenciaService.sePuedeRenovar(licencia, titular);

        // Assert
        assertFalse(resultado, "No debe poder renovarse si la licencia está vigente y el titular no fue modificado");
    }

    @Test
    void buscarLicenciasNoVigentes_filtraPorFechasYClase() {
        // Arrange
        Titular titular = new Titular();
        titular.setNombre("Juan");
        titular.setApellido("Pérez");
        Licencia l1 = new Licencia();
        l1.setTitular(titular);
        l1.setIdLicencia(1);
        l1.setClase("A");
        l1.setFechaVencimiento(java.sql.Date.valueOf("2024-01-10"));
        Licencia l2 = new Licencia();
        l2.setTitular(titular);
        l2.setIdLicencia(2);
        l2.setClase("B");
        l2.setFechaVencimiento(java.sql.Date.valueOf("2024-03-15"));
        Licencia l3 = new Licencia();
        l3.setTitular(titular);
        l3.setIdLicencia(3);
        l3.setClase("A");
        l3.setFechaVencimiento(java.sql.Date.valueOf("2025-01-10")); // fuera de rango

        // Solo l1 y l2 deben ser devueltas por el mock
        when(licenciaRepository.findLicenciasNoVigentes(any(Date.class), any())).thenReturn(List.of(l1, l2, l3));

        Date desde = java.sql.Date.valueOf("2024-01-01");
        Date hasta = java.sql.Date.valueOf("2024-12-31");

        // Act
        List<LicenciaListadoRecord> resultado = licenciaService.buscarLicenciasNoVigentes(desde, hasta, null);

        // Assert
        assertEquals(2, resultado.size());
        assertEquals("No vigente", resultado.get(0).estadoActual());
        assertEquals("Juan Pérez", resultado.get(0).nombreCompletoTitular());

        // Filtrar por clase
        List<LicenciaListadoRecord> soloClaseA = licenciaService.buscarLicenciasNoVigentes(desde, hasta, "A");
        assertEquals(2, soloClaseA.size()); // El mock devuelve l1 y l3, pero solo l1 está en rango
        assertTrue(resultado.stream().anyMatch(r -> r.nombreCompletoTitular().equals("Juan Pérez")));
    }
}