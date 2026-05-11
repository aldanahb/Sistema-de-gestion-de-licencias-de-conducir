package com.gestionlicencias.gestionlicenciasconducir.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestionlicencias.gestionlicenciasconducir.dto.UsuarioRecord;
import com.gestionlicencias.gestionlicenciasconducir.model.TipoDocumento;
import com.gestionlicencias.gestionlicenciasconducir.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.List;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    private UsuarioRecord usuarioRecord;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController).build();
        objectMapper = new ObjectMapper();

        usuarioRecord = new UsuarioRecord(
                "admin",
                TipoDocumento.DNI,
                "12345678",
                "Perez",
                "Juan",
                "clave123"
        );
    }

    @Test
    void registrarUsuario_exitoso() throws Exception {
        Mockito.doNothing().when(usuarioService).registrarUsuario(any());

        mockMvc.perform(post("/api/usuarios/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioRecord)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Usuario registrado correctamente"));
    }

    @Test
    void registrarUsuario_nombreUsuarioRepetido() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("El nombre de usuario ya está registrado."))
                .when(usuarioService).registrarUsuario(any());

        mockMvc.perform(post("/api/usuarios/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioRecord)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El nombre de usuario ya está registrado."));
    }

    @Test
    void buscarUsuario_exitoso() throws Exception {
        UsuarioRecord usuario1 = new UsuarioRecord("admin", TipoDocumento.DNI, "12345678", "Perez", "Juan", "clave123");
        UsuarioRecord usuario2 = new UsuarioRecord("ana01", TipoDocumento.DNI, "87654321", "Gomez", "Ana", "clave456");
        when(usuarioService.buscarUsuario(null, null, null)).thenReturn(List.of(usuario1, usuario2));

        mockMvc.perform(get("/api/usuarios/buscar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreUsuario").value("admin"))
                .andExpect(jsonPath("$[1].nombreUsuario").value("ana01"));
    }

    @Test
    void buscarUsuario_sinResultados() throws Exception {
        when(usuarioService.buscarUsuario(TipoDocumento.DNI, "99999999", null)).thenReturn(List.of());

        mockMvc.perform(get("/api/usuarios/buscar")
                .param("tipoDocumento", "DNI")
                .param("documento", "99999999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void loginUsuario_credencialesValidas_redirigeSegunRol() throws Exception {
        // Arrange
        String username = "admin";
        String password = "password123";
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbCI6IkFkbWluaXN0cmF0aXZvIiwiaWF0IjoxNjM5NzI5NjAwLCJleHAiOjE2Mzk3MzMyMDB9.signature";
        
        when(usuarioService.loginDeUsuario(username, password)).thenReturn(token);

        // Act & Assert
        mockMvc.perform(post("/api/usuarios/loginInput")
                .param("username", username)
                .param("password", password)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/api/usuarios/Administrativo"));
    }

    @Test
    void loginUsuario_credencialesInvalidas_redirigeALoginConError() throws Exception {
        // Arrange
        String username = "usuario_inexistente";
        String password = "password123";
        
        when(usuarioService.loginDeUsuario(username, password))
            .thenThrow(new IllegalArgumentException("Usuario no encontrado."));

        // Act & Assert
        mockMvc.perform(post("/api/usuarios/loginInput")
                .param("username", username)
                .param("password", password)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/api/usuarios/login?error=true"));
    }

    @Test
    void loginUsuario_contraseñaIncorrecta_redirigeALoginConError() throws Exception {
        // Arrange
        String username = "admin";
        String password = "contraseña_incorrecta";
        
        when(usuarioService.loginDeUsuario(username, password))
            .thenThrow(new IllegalArgumentException("Contraseña incorrecta."));

        // Act & Assert
        mockMvc.perform(post("/api/usuarios/loginInput")
                .param("username", username)
                .param("password", password)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/api/usuarios/login?error=true"));
    }

    @Test
    void mostrarLogin_retornaVistaLogin() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/usuarios/login"))
            .andExpect(status().isOk())
            .andExpect(view().name("login"));
    }

    @Test
    void mostrarMenuAdministrador_retornaVistaAdministrador() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/usuarios/Administrador"))
            .andExpect(status().isOk())
            .andExpect(view().name("menuOpcionesUsuarioAdministrador"));
    }

    @Test
    void mostrarMenuAdministrativo_retornaVistaAdministrativo() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/usuarios/Administrativo"))
            .andExpect(status().isOk())
            .andExpect(view().name("menuOpcionesUsuarioAdministrativo"));
    }

    @Test
    void loginUsuario_conRolRoot_redirigeAAdministrador() throws Exception {
        // Arrange
        String username = "root";
        String password = "root";
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyb290Iiwicm9sIjoicm9vdCIsImlhdCI6MTYzOTcyOTYwMCwiZXhwIjoxNjM5NzMzMjAwfQ.signature";
        
        when(usuarioService.loginDeUsuario(username, password)).thenReturn(token);

        // Act & Assert
        mockMvc.perform(post("/api/usuarios/loginInput")
                .param("username", username)
                .param("password", password)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/api/usuarios/Administrador"));
    }

    @Test
    void loginUsuario_conRolAdministrativo_redirigeAAdministrativo() throws Exception {
        // Arrange
        String username = "usuario_admin";
        String password = "password123";
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c3VhcmlvX2FkbWluIiwicm9sIjoiQWRtaW5pc3RyYXRpdm8iLCJpYXQiOjE2Mzk3Mjk2MDAsImV4cCI6MTYzOTczMzIwMH0.signature";
        
        when(usuarioService.loginDeUsuario(username, password)).thenReturn(token);

        // Act & Assert
        mockMvc.perform(post("/api/usuarios/loginInput")
                .param("username", username)
                .param("password", password)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/api/usuarios/Administrativo"));
    }

    @Test
    void loginUsuario_conParametrosVacios_redirigeALoginConError() throws Exception {
        // Arrange
        String username = "";
        String password = "";
        
        when(usuarioService.loginDeUsuario(username, password))
            .thenThrow(new IllegalArgumentException("Usuario no encontrado."));

        // Act & Assert
        mockMvc.perform(post("/api/usuarios/loginInput")
                .param("username", username)
                .param("password", password)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/api/usuarios/login?error=true"));
    }

    @Test
    void loginUsuario_conParametrosNulos_redirigeALoginConError() throws Exception {
        // Arrange
        when(usuarioService.loginDeUsuario(null, null))
            .thenThrow(new IllegalArgumentException("Usuario no encontrado."));

        // Act & Assert
        mockMvc.perform(post("/api/usuarios/loginInput")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/api/usuarios/login?error=true"));
    }
}
