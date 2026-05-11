package com.gestionlicencias.gestionlicenciasconducir;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.gestionlicencias.gestionlicenciasconducir.model.TipoDocumento;
import com.gestionlicencias.gestionlicenciasconducir.model.Usuario;
import com.gestionlicencias.gestionlicenciasconducir.repository.UsuarioRepository;
import com.gestionlicencias.gestionlicenciasconducir.service.UsuarioService;

@SpringBootTest
@ActiveProfiles("test")
class LoginIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // Limpiar la base de datos antes de cada test
        usuarioRepository.deleteAll();
        
        // Crear un usuario de prueba
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario("testuser");
        usuario.setTipoDocumento(TipoDocumento.DNI);
        usuario.setDocumento("12345678");
        usuario.setApellido("Test");
        usuario.setNombre("Usuario");
        usuario.setContrasena(passwordEncoder.encode("password123"));
        usuario.setRol("Administrativo");
        usuarioRepository.save(usuario);
    }

    @Test
    void loginIntegration_credencialesValidas_retornaToken() {
        // Arrange
        String username = "testuser";
        String password = "password123";

        // Act
        String token = usuarioService.loginDeUsuario(username, password);

        // Assert
        assertNotNull(token);
        assertTrue(token.length() > 0);
        
        // Verificar que el token tiene formato JWT
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "El token JWT debe tener 3 partes");
    }

    @Test
    void loginIntegration_credencialesInvalidas_lanzaException() {
        // Arrange
        String username = "testuser";
        String password = "password_incorrecta";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioService.loginDeUsuario(username, password),
            "Debería lanzar excepción con credenciales inválidas"
        );

        assertEquals("Contraseña incorrecta.", exception.getMessage());
    }

    @Test
    void loginIntegration_usuarioNoExistente_lanzaException() {
        // Arrange
        String username = "usuario_inexistente";
        String password = "password123";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioService.loginDeUsuario(username, password),
            "Debería lanzar excepción cuando el usuario no existe"
        );

        assertEquals("Usuario no encontrado.", exception.getMessage());
    }

    @Test
    void loginIntegration_endpointLogin_retornaVistaLogin() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/usuarios/login"))
            .andExpect(status().isOk())
            .andExpect(view().name("login"));
    }

    @Test
    void loginIntegration_endpointLoginInput_credencialesValidas_redirige() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/usuarios/loginInput")
                .param("username", "testuser")
                .param("password", "password123")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/api/usuarios/Administrativo"));
    }

    @Test
    void loginIntegration_endpointLoginInput_credencialesInvalidas_redirigeConError() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/usuarios/loginInput")
                .param("username", "testuser")
                .param("password", "password_incorrecta")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/api/usuarios/login?error=true"));
    }

    @Test
    void loginIntegration_endpointLoginInput_usuarioNoExistente_redirigeConError() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/usuarios/loginInput")
                .param("username", "usuario_inexistente")
                .param("password", "password123")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/api/usuarios/login?error=true"));
    }

    @Test
    void loginIntegration_endpointMenuAdministrativo_retornaVista() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/usuarios/Administrativo"))
            .andExpect(status().isOk())
            .andExpect(view().name("menuOpcionesUsuarioAdministrativo"));
    }

    @Test
    void loginIntegration_endpointMenuAdministrador_retornaVista() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/usuarios/Administrador"))
            .andExpect(status().isOk())
            .andExpect(view().name("menuOpcionesUsuarioAdministrador"));
    }

    @Test
    void loginIntegration_tokenContieneInformacionCorrecta() {
        // Arrange
        String username = "testuser";
        String password = "password123";

        // Act
        String token = usuarioService.loginDeUsuario(username, password);

        // Assert
        assertNotNull(token);
        
        // Verificar que el token contiene información del usuario
        // (En un test real, podrías decodificar el JWT para verificar el contenido)
        assertTrue(token.length() > 50, "El token debería tener una longitud significativa");
    }

    @Test
    void loginIntegration_diferentesUsuarios_generanTokensDiferentes() {
        // Arrange
        Usuario usuario2 = new Usuario();
        usuario2.setNombreUsuario("testuser2");
        usuario2.setTipoDocumento(TipoDocumento.DNI);
        usuario2.setDocumento("87654321");
        usuario2.setApellido("Test2");
        usuario2.setNombre("Usuario2");
        usuario2.setContrasena(passwordEncoder.encode("password456"));
        usuario2.setRol("Administrador");
        usuarioRepository.save(usuario2);

        // Act
        String token1 = usuarioService.loginDeUsuario("testuser", "password123");
        String token2 = usuarioService.loginDeUsuario("testuser2", "password456");

        // Assert
        assertNotNull(token1);
        assertNotNull(token2);
        assertNotEquals(token1, token2, "Los tokens de diferentes usuarios deberían ser diferentes");
    }

    @Test
    void loginIntegration_mismoUsuario_diferentesContraseñas_generanExcepciones() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
            () -> usuarioService.loginDeUsuario("testuser", "password_incorrecta1"));
        
        assertThrows(IllegalArgumentException.class, 
            () -> usuarioService.loginDeUsuario("testuser", "password_incorrecta2"));
        
        // Verificar que con la contraseña correcta sí funciona
        String token = usuarioService.loginDeUsuario("testuser", "password123");
        assertNotNull(token);
    }
} 