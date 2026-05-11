package com.gestionlicencias.gestionlicenciasconducir.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gestionlicencias.gestionlicenciasconducir.dto.UsuarioRecord;
import com.gestionlicencias.gestionlicenciasconducir.model.TipoDocumento;
import com.gestionlicencias.gestionlicenciasconducir.model.Usuario;
import com.gestionlicencias.gestionlicenciasconducir.service.UsuarioService;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.crypto.SecretKey;

@Tag(name = "Usuario Controller", description = "Operaciones para la gestión de usuarios")
@Controller
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final SecretKey jwtSecretKey;

    public UsuarioController(UsuarioService usuarioService, SecretKey jwtSecretKey) {
        this.usuarioService = usuarioService;
        this.jwtSecretKey = jwtSecretKey;
    }
    
    @GetMapping("/menuOpcionesAdministrativo") 
    String mostrarMenuOpcionesAdministrativo(){
        return "menuOpcionesUsuarioAdministrativo";
    }

    @GetMapping("/menuOpcionesAdministrador") 
    String mostrarMenuOpcionesAdministrador(){
        return "menuOpcionesUsuarioAdministrador";
    }

    @GetMapping("/menuUsuario") 
    String mostrarMenuUsuario() {
        return "menuUsuarioAdmin";
    }

    @GetMapping("/registroUsuarioAdministrativo")
    String mostrarRegistroUsuarioAdministrativo() {
        return "registroUsuario";
    }

    //Login
    @GetMapping("/login") 
    String mostrarLogin() {
        return "login";
    }

    @Operation(
        summary = "Login de Usuario",
        description = "Permite a un usuario iniciar sesión con su nombre de usuario y contraseña.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login exitoso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
        }
    )
    @PostMapping("/loginInput")
    public String loginUsuario(
            @RequestParam String username,
            @RequestParam String password,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            String token = usuarioService.loginDeUsuario(username, password);
            String rol = Jwts.parser()
                    .setSigningKey(jwtSecretKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .get("rol", String.class);

            return "root".equals(rol)
                    ? "redirect:/api/usuarios/Administrador"
                    : "redirect:/api/usuarios/Administrativo";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addAttribute("error", "true");
            return "redirect:/api/usuarios/login";
        }
    }
    @GetMapping("/Administrador")
    public String mostrarMenuOpcionesUsuarioAdministrador() {
        return "menuOpcionesUsuarioAdministrador"; // Return the name of the Thymeleaf template
    }

    @GetMapping("/Administrativo")
    public String mostrarMenuOpcionesUsuarioAdministrativo() {
        return "menuOpcionesUsuarioAdministrativo"; // Return the name of the Thymeleaf template
    }

    //------------------Fin de login------------------//
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarUsuario(@RequestBody UsuarioRecord usuarioRecord) {
        try {
            usuarioService.registrarUsuario(usuarioRecord);
            return ResponseEntity.ok(Map.of("message", "Usuario registrado correctamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Ocurrió un error al registrar el usuario"));
        }
    }

    @Operation(
        summary = "Buscar un Usuario",
        description = "Busca un usuario por tipo de documento, número de documento y nombre de usuario. Todos los parámetros son opcionales.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuarios encontrados"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Parámetros inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
        }
    )
    @GetMapping("/buscar")
    public ResponseEntity<List<UsuarioRecord>> buscarUsuario(
        @RequestParam(required = false) TipoDocumento tipoDocumento, 
        @RequestParam(required = false) String documento, 
        @RequestParam(required = false) String nombreUsuario) {
        try{
            List<UsuarioRecord> usuarios = usuarioService.buscarUsuario(tipoDocumento, documento, nombreUsuario);
            return ResponseEntity.ok(usuarios);
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(
        summary = "Modificar un Usuario",
        description = "Modifica los datos de un usuario existente.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario modificado correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
        }
    )
    @PutMapping("/modificar")
    public ResponseEntity<?> modificarUsuario(@RequestBody UsuarioRecord usuarioRecord) {
        try {
            usuarioService.modificarUsuario(usuarioRecord);
            return ResponseEntity.ok(Map.of("message", "Usuario modificado correctamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Ocurrió un error al modificar el usuario"));
        }
    }

    @GetMapping("/buscarUsuarios")
    public String mostrarBusquedaUsuarios() {
        return "listaUsuarios"; 
    }

    @Operation(
    summary = "Obtener usuario por tipo y documento",
    description = "Devuelve un usuario según tipo de documento y número de documento",
    responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Parámetros inválidos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    }
)
    @GetMapping("/modificar/{tipoDocumento}/{documento}/{nombreUsuario}")
    public String mostrarFormularioModificacion(
            @PathVariable TipoDocumento tipoDocumento,
            @PathVariable String documento,
            @PathVariable String nombreUsuario,
            Model model) {

        List<UsuarioRecord> usuarios = usuarioService.buscarUsuario(tipoDocumento, documento, nombreUsuario);
        model.addAttribute("usuario", usuarios.get(0));
        
        model.addAttribute("tiposDocumento", TipoDocumento.values());

        return "modificacionUsuario"; 
    }

}


