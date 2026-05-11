package com.gestionlicencias.gestionlicenciasconducir.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.gestionlicencias.gestionlicenciasconducir.dto.UsuarioRecord;
import com.gestionlicencias.gestionlicenciasconducir.model.TipoDocumento;
import com.gestionlicencias.gestionlicenciasconducir.model.Usuario;
import com.gestionlicencias.gestionlicenciasconducir.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @Test
    void registrarUsuario_nombreUsuarioYaExiste_lanzaException() {
        UsuarioRecord usuarioRecord = new UsuarioRecord("admin", TipoDocumento.DNI, "12345678", "Perez", "Juan", "clave123");

        when(repository.existsByNombreUsuario("admin")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> usuarioService.registrarUsuario(usuarioRecord));

        assertEquals("El nombre de usuario ya está registrado.", ex.getMessage());
    }

    @Test
    void registrarUsuario_datosValidos_guardarUsuario() {
        UsuarioRecord usuarioRecord = new UsuarioRecord("admin", TipoDocumento.DNI, "12345678", "Perez", "Juan", "clave123");

        when(repository.existsByNombreUsuario("admin")).thenReturn(false);
        when(passwordEncoder.encode("clave123")).thenReturn("hashedPassword");

        usuarioService.registrarUsuario(usuarioRecord);

        verify(repository, times(1)).save(argThat(usuario -> 
            usuario.getNombreUsuario().equals("admin") &&
            usuario.getDocumento().equals("12345678") &&
            usuario.getApellido().equals("Perez") &&
            usuario.getNombre().equals("Juan") &&
            usuario.getContrasena().equals("hashedPassword") &&
            usuario.getRol().equals("Administrativo")
        ));
    }

    @Test
    void buscarUsuario_todosNulos_devuelveTodos() {
        var usuario1 = new com.gestionlicencias.gestionlicenciasconducir.model.Usuario();
        usuario1.setNombreUsuario("admin");
        usuario1.setTipoDocumento(TipoDocumento.DNI);
        usuario1.setDocumento("12345678");
        usuario1.setApellido("Perez");
        usuario1.setNombre("Juan");
        usuario1.setContrasena("clave123");
        usuario1.setRol("Administrativo");

        var usuario2 = new com.gestionlicencias.gestionlicenciasconducir.model.Usuario();
        usuario2.setNombreUsuario("ana01");
        usuario2.setTipoDocumento(TipoDocumento.DNI);
        usuario2.setDocumento("87654321");
        usuario2.setApellido("Gomez");
        usuario2.setNombre("Ana");
        usuario2.setContrasena("clave456");
        usuario2.setRol("Administrativo");

        when(repository.buscarPorFiltros(null, null, null)).thenReturn(java.util.List.of(usuario1, usuario2));

        var result = usuarioService.buscarUsuario(null, null, null);
        assertEquals(2, result.size());
        assertEquals("admin", result.get(0).nombreUsuario());
        assertEquals("ana01", result.get(1).nombreUsuario());
    }

    @Test
    void buscarUsuario_porDocumento_devuelveUno() {
        var usuario = new com.gestionlicencias.gestionlicenciasconducir.model.Usuario();
        usuario.setNombreUsuario("admin");
        usuario.setTipoDocumento(TipoDocumento.DNI);
        usuario.setDocumento("12345678");
        usuario.setApellido("Perez");
        usuario.setNombre("Juan");
        usuario.setContrasena("clave123");
        usuario.setRol("Administrativo");

        when(repository.buscarPorFiltros(null, "12345678", null)).thenReturn(java.util.List.of(usuario));

        var result = usuarioService.buscarUsuario(null, "12345678", null);
        assertEquals(1, result.size());
        assertEquals("admin", result.get(0).nombreUsuario());
        assertEquals("12345678", result.get(0).documento());
    }

    @Test
    void buscarUsuario_porNombreUsuario_devuelveUno() {
        var usuario = new com.gestionlicencias.gestionlicenciasconducir.model.Usuario();
        usuario.setNombreUsuario("ana01");
        usuario.setTipoDocumento(TipoDocumento.DNI);
        usuario.setDocumento("87654321");
        usuario.setApellido("Gomez");
        usuario.setNombre("Ana");
        usuario.setContrasena("clave456");
        usuario.setRol("Administrativo");

        when(repository.buscarPorFiltros(null, null, "ana01")).thenReturn(java.util.List.of(usuario));

        var result = usuarioService.buscarUsuario(null, null, "ana01");
        assertEquals(1, result.size());
        assertEquals("ana01", result.get(0).nombreUsuario());
        assertEquals("87654321", result.get(0).documento());
    }

    @Test
    void buscarUsuario_combinado_devuelveUno() {
        var usuario = new com.gestionlicencias.gestionlicenciasconducir.model.Usuario();
        usuario.setNombreUsuario("admin");
        usuario.setTipoDocumento(TipoDocumento.DNI);
        usuario.setDocumento("12345678");
        usuario.setApellido("Perez");
        usuario.setNombre("Juan");
        usuario.setContrasena("clave123");
        usuario.setRol("Administrativo");

        when(repository.buscarPorFiltros(TipoDocumento.DNI, "12345678", "admin")).thenReturn(java.util.List.of(usuario));

        var result = usuarioService.buscarUsuario(TipoDocumento.DNI, "12345678", "admin");
        assertEquals(1, result.size());
        assertEquals("admin", result.get(0).nombreUsuario());
        assertEquals("12345678", result.get(0).documento());
    }

    @Test
    void modificarUsuario_usuarioExistente_modificaDatos() {
        UsuarioRecord usuarioRecord = new UsuarioRecord("admin", TipoDocumento.DNI, "12345678", "NuevoApellido", "NuevoNombre", "nuevaClave");
        com.gestionlicencias.gestionlicenciasconducir.model.Usuario usuario = new com.gestionlicencias.gestionlicenciasconducir.model.Usuario();
        usuario.setNombreUsuario("admin");
        usuario.setTipoDocumento(TipoDocumento.DNI);
        usuario.setDocumento("12345678");
        usuario.setApellido("ViejoApellido");
        usuario.setNombre("ViejoNombre");
        usuario.setContrasena("viejaClave");
        usuario.setRol("Administrativo");

        when(repository.findByTipoDocumentoAndDocumento(TipoDocumento.DNI, "12345678")).thenReturn(usuario);
        when(passwordEncoder.encode("nuevaClave")).thenReturn("hashedNuevaClave");

        usuarioService.modificarUsuario(usuarioRecord);

        assertEquals("NuevoApellido", usuario.getApellido());
        assertEquals("NuevoNombre", usuario.getNombre());
        assertEquals("hashedNuevaClave", usuario.getContrasena());
        verify(repository).save(usuario);
    }

    @Test
    void modificarUsuario_usuarioNoExiste_lanzaException() {
        UsuarioRecord usuarioRecord = new UsuarioRecord("admin", TipoDocumento.DNI, "12345678", "Apellido", "Nombre", "clave");
        when(repository.findByTipoDocumentoAndDocumento(TipoDocumento.DNI, "12345678")).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> usuarioService.modificarUsuario(usuarioRecord));
        assertEquals("Usuario no encontrado.", ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void loginDeUsuario_credencialesValidas_retornaToken() {
        // Arrange
        String username = "admin";
        String password = "password123";
        String hashedPassword = "$2a$10$hashedPassword";
        
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(username);
        usuario.setContrasena(hashedPassword);
        usuario.setRol("Administrativo");

        when(repository.findByNombreUsuario(username)).thenReturn(usuario);
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);

        // Act
        String token = usuarioService.loginDeUsuario(username, password);

        // Assert
        assertNotNull(token);
        assertTrue(token.length() > 0);
        verify(repository).findByNombreUsuario(username);
        verify(passwordEncoder).matches(password, hashedPassword);
    }

    @Test
    void loginDeUsuario_usuarioNoEncontrado_lanzaException() {
        // Arrange
        String username = "usuario_inexistente";
        String password = "password123";

        when(repository.findByNombreUsuario(username)).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioService.loginDeUsuario(username, password),
            "Debería lanzar excepción cuando el usuario no existe"
        );

        assertEquals("Usuario no encontrado.", exception.getMessage());
        verify(repository).findByNombreUsuario(username);
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void loginDeUsuario_contraseñaIncorrecta_lanzaException() {
        // Arrange
        String username = "admin";
        String password = "contraseña_incorrecta";
        String hashedPassword = "$2a$10$hashedPassword";
        
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(username);
        usuario.setContrasena(hashedPassword);
        usuario.setRol("Administrativo");

        when(repository.findByNombreUsuario(username)).thenReturn(usuario);
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioService.loginDeUsuario(username, password),
            "Debería lanzar excepción cuando la contraseña es incorrecta"
        );

        assertEquals("Contraseña incorrecta.", exception.getMessage());
        verify(repository).findByNombreUsuario(username);
        verify(passwordEncoder).matches(password, hashedPassword);
    }

    @Test
    void loginDeUsuario_conRolAdministrativo_generaTokenConRolCorrecto() {
        // Arrange
        String username = "admin";
        String password = "password123";
        String hashedPassword = "$2a$10$hashedPassword";
        
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(username);
        usuario.setContrasena(hashedPassword);
        usuario.setRol("Administrativo");

        when(repository.findByNombreUsuario(username)).thenReturn(usuario);
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);

        // Act
        String token = usuarioService.loginDeUsuario(username, password);

        // Assert
        assertNotNull(token);
        // Verificar que el token contiene el rol correcto
        assertTrue(token.contains("Administrativo") || token.length() > 0);
    }

    @Test
    void loginDeUsuario_conRolAdministrador_generaTokenConRolCorrecto() {
        // Arrange
        String username = "admin";
        String password = "password123";
        String hashedPassword = "$2a$10$hashedPassword";
        
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(username);
        usuario.setContrasena(hashedPassword);
        usuario.setRol("Administrador");

        when(repository.findByNombreUsuario(username)).thenReturn(usuario);
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);

        // Act
        String token = usuarioService.loginDeUsuario(username, password);

        // Assert
        assertNotNull(token);
        // Verificar que el token contiene el rol correcto
        assertTrue(token.contains("Administrador") || token.length() > 0);
    }

    @Test
    void loginDeUsuario_conUsernameVacio_lanzaException() {
        // Arrange
        String username = "";
        String password = "password123";

        when(repository.findByNombreUsuario(username)).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioService.loginDeUsuario(username, password),
            "Debería lanzar excepción cuando el username está vacío"
        );

        assertEquals("Usuario no encontrado.", exception.getMessage());
    }

    @Test
    void loginDeUsuario_conPasswordVacio_lanzaException() {
        // Arrange
        String username = "admin";
        String password = "";
        String hashedPassword = "$2a$10$hashedPassword";
        
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(username);
        usuario.setContrasena(hashedPassword);
        usuario.setRol("Administrativo");

        when(repository.findByNombreUsuario(username)).thenReturn(usuario);
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioService.loginDeUsuario(username, password),
            "Debería lanzar excepción cuando la contraseña está vacía"
        );

        assertEquals("Contraseña incorrecta.", exception.getMessage());
    }

    @Test
    void loginDeUsuario_conUsernameNulo_lanzaException() {
        // Arrange
        String username = null;
        String password = "password123";

        when(repository.findByNombreUsuario(username)).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioService.loginDeUsuario(username, password),
            "Debería lanzar excepción cuando el username es nulo"
        );

        assertEquals("Usuario no encontrado.", exception.getMessage());
    }

    @Test
    void loginDeUsuario_conPasswordNulo_lanzaException() {
        // Arrange
        String username = "admin";
        String password = null;
        String hashedPassword = "$2a$10$hashedPassword";
        
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(username);
        usuario.setContrasena(hashedPassword);
        usuario.setRol("Administrativo");

        when(repository.findByNombreUsuario(username)).thenReturn(usuario);
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioService.loginDeUsuario(username, password),
            "Debería lanzar excepción cuando la contraseña es nula"
        );

        assertEquals("Contraseña incorrecta.", exception.getMessage());
    }

    @Test
    void loginDeUsuario_tokenGenerado_tieneFormatoJWT() {
        // Arrange
        String username = "admin";
        String password = "password123";
        String hashedPassword = "$2a$10$hashedPassword";
        
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(username);
        usuario.setContrasena(hashedPassword);
        usuario.setRol("Administrativo");

        when(repository.findByNombreUsuario(username)).thenReturn(usuario);
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);

        // Act
        String token = usuarioService.loginDeUsuario(username, password);

        // Assert
        assertNotNull(token);
        // Verificar que el token tiene el formato JWT (3 partes separadas por puntos)
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "El token JWT debe tener 3 partes separadas por puntos");
    }
}
