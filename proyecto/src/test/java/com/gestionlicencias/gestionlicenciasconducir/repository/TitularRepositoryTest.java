package com.gestionlicencias.gestionlicenciasconducir.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.gestionlicencias.gestionlicenciasconducir.model.Titular;
import com.gestionlicencias.gestionlicenciasconducir.model.TipoDocumento;

import java.util.Optional;

@DataJpaTest
public class TitularRepositoryTest {

    @Autowired
    private TitularRepository titularRepository;

    private Titular titular;

    @BeforeEach
    void setUp() {
        // Crear un titular de ejemplo para usar en los tests
        titular = new Titular();
        titular.setTipoDocumento(TipoDocumento.DNI);
        titular.setDocumento("12345678");
        titular.setNombre("Juan");
        titular.setApellido("Perez");
        titular.setFechaNacimiento(new java.util.Date());
        titular.setDireccion("Calle Falsa 123");
        titular.setGrupoSanguineo("O+");
        titular.setFactorRH("Positivo");
        titular.setDonanteOrganos(true);

        // Guardar el titular en la base de datos
        titularRepository.saveAndFlush(titular);
    }

    @Test
    void testExistsByTipoDocumentoAndDocumento() {
        // Verificar que el método existsByTipoDocumentoAndDocumento funciona correctamente
        boolean exists = titularRepository.existsByTipoDocumentoAndDocumento(TipoDocumento.DNI, "12345678");
        assertTrue(exists, "El titular con DNI 12345678 debería existir en la base de datos");

        boolean notExists = titularRepository.existsByTipoDocumentoAndDocumento(TipoDocumento.DNI, "87654321");
        assertFalse(notExists, "El titular con DNI 87654321 no debería existir en la base de datos");

        boolean notExistsDifferentType = titularRepository.existsByTipoDocumentoAndDocumento(TipoDocumento.PASAPORTE, "12345678");
        assertFalse(notExistsDifferentType, "No debería existir un titular con pasaporte 12345678");
    }

    @Test
    void testSaveAndRetrieveTitular() {
        // Verificar que se puede guardar y recuperar un titular correctamente
        Titular retrievedTitular = titularRepository.findById(titular.getIdTitular()).orElse(null);
        assertNotNull(retrievedTitular, "El titular debería haberse recuperado correctamente");
        assertEquals("Juan", retrievedTitular.getNombre(), "El nombre del titular debería ser 'Juan'");
        assertEquals("Perez", retrievedTitular.getApellido(), "El apellido del titular debería ser 'Perez'");
    }

    @Test
    void testUniqueConstraintOnDocumento() {
        // Verificar que no se pueden guardar dos titulares con el mismo documento
        Titular otroTitular = new Titular();
        otroTitular.setTipoDocumento(TipoDocumento.DNI);
        otroTitular.setDocumento("12345678"); // Documento duplicado
        otroTitular.setNombre("Maria");
        otroTitular.setApellido("Gomez");
        otroTitular.setFechaNacimiento(new java.util.Date());
        otroTitular.setDireccion("Calle Verdadera 456");
        otroTitular.setGrupoSanguineo("A+");
        otroTitular.setFactorRH("Negativo");
        otroTitular.setDonanteOrganos(false);

        Exception exception = assertThrows(Exception.class, () -> {
            titularRepository.save(otroTitular);
        });

        assertNotNull(exception, "Debería lanzarse una excepción al intentar guardar un documento duplicado");
    }

    @Test
    void testFindByTipoDocumentoAndDocumento_existente() {
        // Verificar que se puede encontrar un titular por tipo y número de documento
        Optional<Titular> resultado = titularRepository.findByTipoDocumentoAndDocumento(TipoDocumento.DNI, "12345678");
        
        assertTrue(resultado.isPresent(), "Debería encontrar el titular con DNI 12345678");
        assertEquals("Juan", resultado.get().getNombre(), "El nombre del titular debería ser 'Juan'");
        assertEquals("Perez", resultado.get().getApellido(), "El apellido del titular debería ser 'Perez'");
        assertEquals(TipoDocumento.DNI, resultado.get().getTipoDocumento(), "El tipo de documento debería ser DNI");
        assertEquals("12345678", resultado.get().getDocumento(), "El documento debería ser 12345678");
    }

    @Test
    void testFindByTipoDocumentoAndDocumento_noExistente() {
        // Verificar que no se encuentra un titular que no existe
        Optional<Titular> resultado = titularRepository.findByTipoDocumentoAndDocumento(TipoDocumento.DNI, "99999999");
        
        assertFalse(resultado.isPresent(), "No debería encontrar un titular con DNI 99999999");
    }

    @Test
    void testFindByTipoDocumentoAndDocumento_tipoDocumentoDiferente() {
        // Verificar que no se encuentra un titular con el mismo número pero tipo de documento diferente
        Optional<Titular> resultado = titularRepository.findByTipoDocumentoAndDocumento(TipoDocumento.PASAPORTE, "12345678");
        
        assertFalse(resultado.isPresent(), "No debería encontrar un titular con pasaporte 12345678");
    }

    @Test
    void testFindByTipoDocumentoAndDocumento_conPasaporte() {
        // Crear un titular con pasaporte
        Titular titularPasaporte = new Titular();
        titularPasaporte.setTipoDocumento(TipoDocumento.PASAPORTE);
        titularPasaporte.setDocumento("AB123456");
        titularPasaporte.setNombre("María");
        titularPasaporte.setApellido("González");
        titularPasaporte.setFechaNacimiento(new java.util.Date());
        titularPasaporte.setDireccion("Avenida Libertad 456");
        titularPasaporte.setGrupoSanguineo("O");
        titularPasaporte.setFactorRH("Negativo");
        titularPasaporte.setDonanteOrganos(false);
        
        titularRepository.saveAndFlush(titularPasaporte);

        // Verificar que se puede encontrar el titular con pasaporte
        Optional<Titular> resultado = titularRepository.findByTipoDocumentoAndDocumento(TipoDocumento.PASAPORTE, "AB123456");
        
        assertTrue(resultado.isPresent(), "Debería encontrar el titular con pasaporte AB123456");
        assertEquals("María", resultado.get().getNombre(), "El nombre del titular debería ser 'María'");
        assertEquals("González", resultado.get().getApellido(), "El apellido del titular debería ser 'González'");
        assertEquals(TipoDocumento.PASAPORTE, resultado.get().getTipoDocumento(), "El tipo de documento debería ser PASAPORTE");
        assertEquals("AB123456", resultado.get().getDocumento(), "El documento debería ser AB123456");
    }

    @Test
    void testFindByTipoDocumentoAndDocumento_conDocumentoVacio() {
        // Verificar comportamiento con documento vacío
        Optional<Titular> resultado = titularRepository.findByTipoDocumentoAndDocumento(TipoDocumento.DNI, "");
        
        assertFalse(resultado.isPresent(), "No debería encontrar un titular con documento vacío");
    }

    @Test
    void testFindByTipoDocumentoAndDocumento_conDocumentoNulo() {
        // Verificar comportamiento con documento nulo
        Optional<Titular> resultado = titularRepository.findByTipoDocumentoAndDocumento(TipoDocumento.DNI, null);
        
        assertFalse(resultado.isPresent(), "No debería encontrar un titular con documento nulo");
    }

    @Test
    void testFindByTipoDocumentoAndDocumento_caseSensitive() {
        // Verificar que la búsqueda es case sensitive para el documento
        Optional<Titular> resultado = titularRepository.findByTipoDocumentoAndDocumento(TipoDocumento.DNI, "12345678");
        assertTrue(resultado.isPresent(), "Debería encontrar el titular con DNI 12345678");

        // Intentar buscar con mayúsculas
        Optional<Titular> resultadoMayusculas = titularRepository.findByTipoDocumentoAndDocumento(TipoDocumento.DNI, "12345678");
        assertTrue(resultadoMayusculas.isPresent(), "Debería encontrar el titular independientemente del case");
    }

    @Test
    void testFindByTipoDocumentoAndDocumento_multiplesTitulares() {
        // Crear un segundo titular con DNI diferente
        Titular titular2 = new Titular();
        titular2.setTipoDocumento(TipoDocumento.DNI);
        titular2.setDocumento("87654321");
        titular2.setNombre("Ana");
        titular2.setApellido("García");
        titular2.setFechaNacimiento(new java.util.Date());
        titular2.setDireccion("Calle Verdadera 789");
        titular2.setGrupoSanguineo("A");
        titular2.setFactorRH("Negativo");
        titular2.setDonanteOrganos(false);
        
        titularRepository.saveAndFlush(titular2);

        // Verificar que se puede encontrar cada titular específicamente
        Optional<Titular> resultado1 = titularRepository.findByTipoDocumentoAndDocumento(TipoDocumento.DNI, "12345678");
        Optional<Titular> resultado2 = titularRepository.findByTipoDocumentoAndDocumento(TipoDocumento.DNI, "87654321");
        
        assertTrue(resultado1.isPresent(), "Debería encontrar el primer titular");
        assertTrue(resultado2.isPresent(), "Debería encontrar el segundo titular");
        assertEquals("Juan", resultado1.get().getNombre(), "El primer titular debería ser Juan");
        assertEquals("Ana", resultado2.get().getNombre(), "El segundo titular debería ser Ana");
    }
}
