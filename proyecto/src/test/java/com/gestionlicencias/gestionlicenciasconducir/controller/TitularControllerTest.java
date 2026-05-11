package com.gestionlicencias.gestionlicenciasconducir.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestionlicencias.gestionlicenciasconducir.dto.TitularRecord;
import com.gestionlicencias.gestionlicenciasconducir.mapper.TitularMapper;
import com.gestionlicencias.gestionlicenciasconducir.model.TipoDocumento;
import com.gestionlicencias.gestionlicenciasconducir.model.Titular;
import com.gestionlicencias.gestionlicenciasconducir.service.TitularService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.hasSize;

//@WebMvcTest(TitularController.class)
@ExtendWith(MockitoExtension.class)
class TitularControllerTest {

    //@Autowired
    private MockMvc mockMvc;

    //@Autowired
    private ObjectMapper objectMapper;

    @Mock
    private TitularService titularService;

    @Mock
    private TitularMapper titularMapper;

    @InjectMocks
    private TitularController titularController;

    private TitularRecord titularValido;
    private Titular titularEsperado;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(titularController).build();

        objectMapper = new ObjectMapper();

        titularValido = new TitularRecord(
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
        //titularEsperado = titularValido.toTitular();
        titularEsperado = new Titular();
        titularEsperado.setTipoDocumento(TipoDocumento.DNI);
        titularEsperado.setDocumento("12345678");
        titularEsperado.setNombre("Juan");
        titularEsperado.setApellido("Pérez");
        titularEsperado.setFechaNacimiento(titularValido.fechaNacimiento());
        titularEsperado.setDireccion("Calle Principal 123");
        titularEsperado.setGrupoSanguineo("A");
        titularEsperado.setFactorRH("Positivo");
        titularEsperado.setDonanteOrganos(true);

        // Mock the behavior of the mapper
        //when(titularMapper.toEntity(any(TitularRecord.class))).thenReturn(titularEsperado);
        //when(titularMapper.toRecord(any(Titular.class))).thenReturn(titularValido);
    }

    @Test
    void testBuscarTitular_Exitoso() throws Exception {
        // Arrange
        when(titularService.buscarTitular(TipoDocumento.DNI, "12345678"))
                .thenReturn(titularValido);

        // Act & Assert
        mockMvc.perform(get("/api/titulares/buscar/titular")
                        .param("tipoDocumento", TipoDocumento.DNI.name())
                        .param("documento", "12345678")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testBuscarTitular_NoEncontrado() throws Exception {
        // Arrange
        when(titularService.buscarTitular(TipoDocumento.DNI, "12345678"))
                .thenThrow(new IllegalArgumentException("Titular no encontrado"));

        // Act & Assert
        mockMvc.perform(get("/api/titulares/buscar/titular")
                        .param("tipoDocumento", TipoDocumento.DNI.name())
                        .param("documento", "12345678")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRegistrarTitular_Exitoso() throws Exception {
        // Arrange
        when(titularService.registrarTitular(any(TitularRecord.class)))
            .thenReturn(titularEsperado);

        // Act & Assert
        mockMvc.perform(post("/api/titulares/registrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(titularValido)))
                .andExpect(status().isCreated());
    }

    @Test
    void testRegistrarTitular_DocumentoExistente() throws Exception {
        // Arrange
        when(titularService.registrarTitular(any(TitularRecord.class)))
            .thenThrow(new IllegalArgumentException("Ya existe un titular con documento: " + titularValido.documento()));

        // Act & Assert
        mockMvc.perform(post("/api/titulares/registrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(titularValido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buscarTitular_existente_retornaTitularCorrecto() throws Exception {
        // Arrange
        TipoDocumento tipoDocumento = TipoDocumento.DNI;
        String documento = "12345678";
        
        TitularRecord titularRecord = new TitularRecord(
            tipoDocumento,
            documento,
            "Juan",
            "Pérez",
            java.sql.Date.valueOf("1990-01-01"),
            "Calle Principal 123",
            "A",
            "+",
            true
        );

        when(titularService.buscarTitular(tipoDocumento, documento)).thenReturn(titularRecord);

        // Act & Assert
        mockMvc.perform(get("/api/titulares/buscar/titular")
                .param("tipoDocumento", tipoDocumento.name())
                .param("documento", documento)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tipoDocumento").value(tipoDocumento.name()))
            .andExpect(jsonPath("$.documento").value(documento))
            .andExpect(jsonPath("$.nombre").value("Juan"))
            .andExpect(jsonPath("$.apellido").value("Pérez"))
            .andExpect(jsonPath("$.direccion").value("Calle Principal 123"))
            .andExpect(jsonPath("$.grupoSanguineo").value("A"))
            .andExpect(jsonPath("$.factorRH").value("+"))
            .andExpect(jsonPath("$.donanteOrganos").value(true));
    }

    @Test
    void buscarTitular_noExistente_retornaNotFound() throws Exception {
        // Arrange
        TipoDocumento tipoDocumento = TipoDocumento.DNI;
        String documento = "99999999";

        when(titularService.buscarTitular(tipoDocumento, documento))
            .thenThrow(new IllegalArgumentException("Titular no encontrado"));

        // Act & Assert
        mockMvc.perform(get("/api/titulares/buscar/titular")
                .param("tipoDocumento", tipoDocumento.name())
                .param("documento", documento)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    void buscarTitular_conPasaporte_existente_retornaTitularCorrecto() throws Exception {
        // Arrange
        TipoDocumento tipoDocumento = TipoDocumento.PASAPORTE;
        String documento = "AB123456";
        
        TitularRecord titularRecord = new TitularRecord(
            tipoDocumento,
            documento,
            "María",
            "González",
            java.sql.Date.valueOf("1985-05-15"),
            "Avenida Libertad 456",
            "O",
            "-",
            false
        );

        when(titularService.buscarTitular(tipoDocumento, documento)).thenReturn(titularRecord);

        // Act & Assert
        mockMvc.perform(get("/api/titulares/buscar/titular")
                .param("tipoDocumento", tipoDocumento.name())
                .param("documento", documento)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tipoDocumento").value(tipoDocumento.name()))
            .andExpect(jsonPath("$.documento").value(documento))
            .andExpect(jsonPath("$.nombre").value("María"))
            .andExpect(jsonPath("$.apellido").value("González"))
            .andExpect(jsonPath("$.donanteOrganos").value(false));
    }

    @Test
    void listarTitulares_sinFiltros_retornaTodosLosTitulares() throws Exception {
        // Arrange
        Titular titular1 = new Titular();
        titular1.setTipoDocumento(TipoDocumento.DNI);
        titular1.setDocumento("12345678");
        titular1.setNombre("Juan");
        titular1.setApellido("Pérez");
        titular1.setFechaNacimiento(java.sql.Date.valueOf("1990-01-01"));
        titular1.setDireccion("Calle 1");
        titular1.setGrupoSanguineo("A");
        titular1.setFactorRH("+");
        titular1.setDonanteOrganos(true);

        Titular titular2 = new Titular();
        titular2.setTipoDocumento(TipoDocumento.DNI);
        titular2.setDocumento("87654321");
        titular2.setNombre("Ana");
        titular2.setApellido("García");
        titular2.setFechaNacimiento(java.sql.Date.valueOf("1985-05-15"));
        titular2.setDireccion("Calle 2");
        titular2.setGrupoSanguineo("O");
        titular2.setFactorRH("-");
        titular2.setDonanteOrganos(false);

        TitularRecord titularRecord1 = new TitularRecord(
            TipoDocumento.DNI, "12345678", "Juan", "Pérez", 
            java.sql.Date.valueOf("1990-01-01"), "Calle 1", "A", "+", true
        );

        TitularRecord titularRecord2 = new TitularRecord(
            TipoDocumento.DNI, "87654321", "Ana", "García", 
            java.sql.Date.valueOf("1985-05-15"), "Calle 2", "O", "-", false
        );

        when(titularService.buscarTitulares(null, null, null)).thenReturn(List.of(titular1, titular2));
        when(titularMapper.toRecord(titular1)).thenReturn(titularRecord1);
        when(titularMapper.toRecord(titular2)).thenReturn(titularRecord2);

        // Act & Assert
        mockMvc.perform(get("/api/titulares/lista")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].nombre").value("Juan"))
            .andExpect(jsonPath("$[1].nombre").value("Ana"));
    }

    @Test
    void listarTitulares_conFiltroApellido_retornaTitularesFiltrados() throws Exception {
        // Arrange
        String apellido = "Pérez";
        Titular titular = new Titular();
        titular.setTipoDocumento(TipoDocumento.DNI);
        titular.setDocumento("12345678");
        titular.setNombre("Juan");
        titular.setApellido("Pérez");
        titular.setFechaNacimiento(java.sql.Date.valueOf("1990-01-01"));
        titular.setDireccion("Calle 1");
        titular.setGrupoSanguineo("A");
        titular.setFactorRH("+");
        titular.setDonanteOrganos(true);

        TitularRecord titularRecord = new TitularRecord(
            TipoDocumento.DNI, "12345678", "Juan", "Pérez", 
            java.sql.Date.valueOf("1990-01-01"), "Calle 1", "A", "+", true
        );

        when(titularService.buscarTitulares(apellido, null, null)).thenReturn(List.of(titular));
        when(titularMapper.toRecord(titular)).thenReturn(titularRecord);

        // Act & Assert
        mockMvc.perform(get("/api/titulares/lista")
                .param("apellido", apellido)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].apellido").value("Pérez"))
            .andExpect(jsonPath("$[0].nombre").value("Juan"));
    }

    @Test
    void listarTitulares_conFiltroTipoDocumento_retornaTitularesFiltrados() throws Exception {
        // Arrange
        TipoDocumento tipoDocumento = TipoDocumento.PASAPORTE;
        Titular titular = new Titular();
        titular.setTipoDocumento(TipoDocumento.PASAPORTE);
        titular.setDocumento("AB123456");
        titular.setNombre("María");
        titular.setApellido("González");

        TitularRecord titularRecord = new TitularRecord(
            TipoDocumento.PASAPORTE, "AB123456", "María", "González", 
            java.sql.Date.valueOf("1985-05-15"), "Avenida 1", "O", "-", false
        );

        when(titularService.buscarTitulares(null, tipoDocumento, null)).thenReturn(List.of(titular));
        when(titularMapper.toRecord(titular)).thenReturn(titularRecord);

        // Act & Assert
        mockMvc.perform(get("/api/titulares/lista")
                .param("tipoDocumento", tipoDocumento.name())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].tipoDocumento").value("PASAPORTE"))
            .andExpect(jsonPath("$[0].documento").value("AB123456"));
    }

    @Test
    void listarTitulares_conFiltroDocumento_retornaTitularEspecifico() throws Exception {
        // Arrange
        String documento = "12345678";
        Titular titular = new Titular();
        titular.setTipoDocumento(TipoDocumento.DNI);
        titular.setDocumento("12345678");
        titular.setNombre("Juan");
        titular.setApellido("Pérez");

        TitularRecord titularRecord = new TitularRecord(
            TipoDocumento.DNI, "12345678", "Juan", "Pérez", 
            java.sql.Date.valueOf("1990-01-01"), "Calle 1", "A", "+", true
        );

        when(titularService.buscarTitulares(null, null, documento)).thenReturn(List.of(titular));
        when(titularMapper.toRecord(titular)).thenReturn(titularRecord);

        // Act & Assert
        mockMvc.perform(get("/api/titulares/lista")
                .param("documento", documento)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].documento").value("12345678"))
            .andExpect(jsonPath("$[0].nombre").value("Juan"));
    }

    @Test
    void listarTitulares_conFiltrosCombinados_retornaTitularesFiltrados() throws Exception {
        // Arrange
        String apellido = "Pérez";
        TipoDocumento tipoDocumento = TipoDocumento.DNI;
        String documento = "12345678";
        
        Titular titular = new Titular();
        titular.setTipoDocumento(TipoDocumento.DNI);
        titular.setDocumento("12345678");
        titular.setNombre("Juan");
        titular.setApellido("Pérez");

        TitularRecord titularRecord = new TitularRecord(
            TipoDocumento.DNI, "12345678", "Juan", "Pérez", 
            java.sql.Date.valueOf("1990-01-01"), "Calle 1", "A", "+", true
        );

        when(titularService.buscarTitulares(apellido, tipoDocumento, documento)).thenReturn(List.of(titular));
        when(titularMapper.toRecord(titular)).thenReturn(titularRecord);

        // Act & Assert
        mockMvc.perform(get("/api/titulares/lista")
                .param("apellido", apellido)
                .param("tipoDocumento", tipoDocumento.name())
                .param("documento", documento)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].apellido").value("Pérez"))
            .andExpect(jsonPath("$[0].tipoDocumento").value("DNI"))
            .andExpect(jsonPath("$[0].documento").value("12345678"));
    }

    @Test
    void listarTitulares_sinResultados_retornaListaVacia() throws Exception {
        // Arrange
        String apellido = "Inexistente";
        
        when(titularService.buscarTitulares(apellido, null, null)).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/titulares/lista")
                .param("apellido", apellido)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

}
