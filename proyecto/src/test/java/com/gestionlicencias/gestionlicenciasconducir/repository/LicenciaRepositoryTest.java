package com.gestionlicencias.gestionlicenciasconducir.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.gestionlicencias.gestionlicenciasconducir.model.Licencia;
import com.gestionlicencias.gestionlicenciasconducir.model.Titular;
import com.gestionlicencias.gestionlicenciasconducir.model.TipoDocumento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.*;

@DataJpaTest
public class LicenciaRepositoryTest {
    @Autowired
    private LicenciaRepository licenciaRepository;
    @Autowired
    private TitularRepository titularRepository;

    private Titular titular;

    @BeforeEach
    void setUp() {
        titular = new Titular();
        titular.setTipoDocumento(TipoDocumento.DNI);
        titular.setDocumento("12345678");
        titular.setNombre("Juan");
        titular.setApellido("Perez");
        titular.setFechaNacimiento(new Date());
        titular.setDireccion("Calle Falsa 123");
        titular.setGrupoSanguineo("O+");
        titular.setFactorRH("Positivo");
        titular.setDonanteOrganos(true);
        titularRepository.saveAndFlush(titular);
    }

    @Test
    void testFindLicenciasNoVigentes() {
        Date hoy = new Date();
        Calendar cal = Calendar.getInstance();

        // Licencia vencida (no vigente)
        Licencia licenciaVencida = new Licencia();
        licenciaVencida.setTitular(titular);
        licenciaVencida.setClase("A");
        cal.setTime(hoy);
        cal.add(Calendar.YEAR, -2);
        licenciaVencida.setFechaInicio(cal.getTime());
        cal.add(Calendar.YEAR, 1);
        licenciaVencida.setFechaVencimiento(cal.getTime()); // vencida hace 1 año
        licenciaVencida.setEstaVigente(false);
        licenciaVencida.setObservaciones("Vencida");
        licenciaRepository.save(licenciaVencida);

        // Licencia vencida de otra clase
        Licencia licenciaVencidaB = new Licencia();
        licenciaVencidaB.setTitular(titular);
        licenciaVencidaB.setClase("B");
        cal.setTime(hoy);
        cal.add(Calendar.YEAR, -3);
        licenciaVencidaB.setFechaInicio(cal.getTime());
        cal.add(Calendar.YEAR, 1);
        licenciaVencidaB.setFechaVencimiento(cal.getTime()); // vencida hace 2 años
        licenciaVencidaB.setEstaVigente(false);
        licenciaVencidaB.setObservaciones("Vencida B");
        licenciaRepository.save(licenciaVencidaB);

        licenciaRepository.flush();

        // Test: debe devolver ambas licencias vencidas
        List<Licencia> noVigentes = licenciaRepository.findLicenciasNoVigentes(hoy, null);
        assertTrue(noVigentes.stream().anyMatch(l -> l.getClase().equals("A")));
        assertTrue(noVigentes.stream().anyMatch(l -> l.getClase().equals("B")));

        // Test: filtrar por clase B
        List<Licencia> noVigentesB = licenciaRepository.findLicenciasNoVigentes(hoy, "B");
        assertEquals(1, noVigentesB.size());
        assertEquals("B", noVigentesB.get(0).getClase());
    }
}
