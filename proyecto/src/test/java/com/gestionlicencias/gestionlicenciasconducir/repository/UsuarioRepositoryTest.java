package com.gestionlicencias.gestionlicenciasconducir.repository;

import com.gestionlicencias.gestionlicenciasconducir.model.TipoDocumento;
import com.gestionlicencias.gestionlicenciasconducir.model.Usuario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void setUp() {
        Usuario usuario1 = new Usuario();
        usuario1.setNombreUsuario("marcos05");
        usuario1.setTipoDocumento(TipoDocumento.DNI);
        usuario1.setDocumento("12345678");
        usuario1.setApellido("Perez");
        usuario1.setNombre("Marcos");
        usuario1.setContrasena("pass1");
        usuario1.setRol("Administrativo");
        usuarioRepository.save(usuario1);

        Usuario usuario2 = new Usuario();
        usuario2.setNombreUsuario("ana01");
        usuario2.setTipoDocumento(TipoDocumento.DNI);
        usuario2.setDocumento("87654321");
        usuario2.setApellido("Gomez");
        usuario2.setNombre("Ana");
        usuario2.setContrasena("pass2");
        usuario2.setRol("Administrativo");
        usuarioRepository.save(usuario2);
    }

    @Test
    void testBuscarPorFiltros_TodosNulos() {
        List<Usuario> usuarios = usuarioRepository.buscarPorFiltros(null, null, null);
        Assertions.assertEquals(2, usuarios.size());
    }

    @Test
    void testBuscarPorFiltros_PorTipoDocumento() {
        List<Usuario> usuarios = usuarioRepository.buscarPorFiltros(TipoDocumento.DNI, null, null);
        Assertions.assertEquals(2, usuarios.size());
    }

    @Test
    void testBuscarPorFiltros_PorDocumento() {
        List<Usuario> usuarios = usuarioRepository.buscarPorFiltros(null, "12345678", null);
        Assertions.assertEquals(1, usuarios.size());
        Assertions.assertEquals("marcos05", usuarios.get(0).getNombreUsuario());
    }

    @Test
    void testBuscarPorFiltros_PorNombreUsuario() {
        List<Usuario> usuarios = usuarioRepository.buscarPorFiltros(null, null, "ana01");
        Assertions.assertEquals(1, usuarios.size());
        Assertions.assertEquals("ana01", usuarios.get(0).getNombreUsuario());
    }

    @Test
    void testBuscarPorFiltros_Combinado() {
        List<Usuario> usuarios = usuarioRepository.buscarPorFiltros(TipoDocumento.DNI, "12345678", "marcos05");
        Assertions.assertEquals(1, usuarios.size());
        Assertions.assertEquals("marcos05", usuarios.get(0).getNombreUsuario());
    }

    @Test
    void testFindByNombreUsuario_noExistente() {
        // Verificar que no se encuentra un usuario que no existe
        Usuario usuario = usuarioRepository.findByNombreUsuario("usuario_inexistente");
        
        assertNull(usuario, "No debería encontrar un usuario con nombre 'usuario_inexistente'");
    }

    @Test
    void testFindByNombreUsuario_caseSensitive() {
        // Verificar que la búsqueda es case sensitive
        Usuario usuario = usuarioRepository.findByNombreUsuario("MARCOS05");
        
        assertNull(usuario, "No debería encontrar un usuario con nombre en mayúsculas 'MARCOS05'");
    }

    @Test
    void testFindByNombreUsuario_conEspacios() {
        // Verificar comportamiento con espacios
        Usuario usuario = usuarioRepository.findByNombreUsuario(" marcos05 ");
        
        assertNull(usuario, "No debería encontrar un usuario con espacios en el nombre");
    }

    @Test
    void testFindByNombreUsuario_vacio() {
        // Verificar comportamiento con nombre vacío
        Usuario usuario = usuarioRepository.findByNombreUsuario("");
        
        assertNull(usuario, "No debería encontrar un usuario con nombre vacío");
    }

    @Test
    void testFindByNombreUsuario_nulo() {
        // Verificar comportamiento con nombre nulo
        Usuario usuario = usuarioRepository.findByNombreUsuario(null);
        
        assertNull(usuario, "No debería encontrar un usuario con nombre nulo");
    }

    @Test
    void testExistsByNombreUsuario_existente() {
        // Verificar que existe un usuario con el nombre especificado
        boolean existe = usuarioRepository.existsByNombreUsuario("marcos05");
        
        assertTrue(existe, "Debería existir un usuario con nombre 'marcos05'");
    }

    @Test
    void testExistsByNombreUsuario_noExistente() {
        // Verificar que no existe un usuario con el nombre especificado
        boolean existe = usuarioRepository.existsByNombreUsuario("usuario_inexistente");
        
        assertFalse(existe, "No debería existir un usuario con nombre 'usuario_inexistente'");
    }

    @Test
    void testExistsByNombreUsuario_caseSensitive() {
        // Verificar que la verificación de existencia es case sensitive
        boolean existe = usuarioRepository.existsByNombreUsuario("MARCOS05");
        
        assertFalse(existe, "No debería existir un usuario con nombre en mayúsculas 'MARCOS05'");
    }

    @Test
    void testFindByNombreUsuario_multiplesUsuarios() {
        // Verificar que se puede encontrar cada usuario específicamente
        Usuario usuario1 = usuarioRepository.findByNombreUsuario("marcos05");
        Usuario usuario2 = usuarioRepository.findByNombreUsuario("ana01");
        
        assertNotNull(usuario1, "Debería encontrar el primer usuario");
        assertNotNull(usuario2, "Debería encontrar el segundo usuario");
        assertEquals("marcos05", usuario1.getNombreUsuario(), "El primer usuario debería ser marcos05");
        assertEquals("ana01", usuario2.getNombreUsuario(), "El segundo usuario debería ser ana01");
        assertEquals("Marcos", usuario1.getNombre(), "El primer usuario debería ser Marcos");
        assertEquals("Ana", usuario2.getNombre(), "El segundo usuario debería ser Ana");
    }

    @Test
    void testFindByNombreUsuario_conCaracteresEspeciales() {
        // Crear un usuario con caracteres especiales en el nombre
        Usuario usuarioEspecial = new Usuario();
        usuarioEspecial.setNombreUsuario("user_123");
        usuarioEspecial.setTipoDocumento(TipoDocumento.DNI);
        usuarioEspecial.setDocumento("11111111");
        usuarioEspecial.setApellido("Test");
        usuarioEspecial.setNombre("Usuario");
        usuarioEspecial.setContrasena("pass123");
        usuarioEspecial.setRol("Administrativo");
        usuarioRepository.save(usuarioEspecial);

        // Verificar que se puede encontrar el usuario con caracteres especiales
        Usuario encontrado = usuarioRepository.findByNombreUsuario("user_123");
        
        assertNotNull(encontrado, "Debería encontrar el usuario con caracteres especiales");
        assertEquals("user_123", encontrado.getNombreUsuario(), "El nombre de usuario debería ser 'user_123'");
    }
}
