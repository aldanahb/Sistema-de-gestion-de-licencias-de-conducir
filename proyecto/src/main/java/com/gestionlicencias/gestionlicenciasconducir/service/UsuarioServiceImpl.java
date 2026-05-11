package com.gestionlicencias.gestionlicenciasconducir.service;

import java.util.Date;
import java.util.List;

import com.gestionlicencias.gestionlicenciasconducir.config.SeguridadConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.gestionlicencias.gestionlicenciasconducir.dto.UsuarioRecord;
import com.gestionlicencias.gestionlicenciasconducir.model.TipoDocumento;
import com.gestionlicencias.gestionlicenciasconducir.model.Usuario;
import com.gestionlicencias.gestionlicenciasconducir.repository.UsuarioRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import javax.crypto.SecretKey;

import javax.crypto.SecretKey;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final SecretKey jwtSecretKey;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, SecretKey jwtSecretKey) {
        this.repository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtSecretKey = jwtSecretKey;
    }

    @Override
    public Usuario buscarUsuarioPorId(Integer idUsuario) {
        return repository.findByIdUsuario(idUsuario);
    }

    @Override
    public void registrarUsuario(UsuarioRecord usuarioRecord) {
        if (repository.existsByNombreUsuario(usuarioRecord.nombreUsuario())) {
            throw new IllegalArgumentException("El nombre de usuario ya está registrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(usuarioRecord.nombreUsuario());
        usuario.setTipoDocumento(usuarioRecord.tipoDocumento());
        usuario.setDocumento(usuarioRecord.documento());
        usuario.setApellido(usuarioRecord.apellido());
        usuario.setNombre(usuarioRecord.nombre());
        usuario.setContrasena(passwordEncoder.encode(usuarioRecord.contrasena()));
        usuario.setRol("Administrativo"); 

        repository.save(usuario);
    }

    @Override
    public List<UsuarioRecord> buscarUsuario(TipoDocumento tipoDocumento, String documento, String nombreUsuario) {
        List<Usuario> usuarios = repository.buscarPorFiltros(tipoDocumento, 
            (documento != null && !documento.isEmpty()) ? documento : null, 
            (nombreUsuario != null && !nombreUsuario.isEmpty()) ? nombreUsuario : null);
        return usuarios.stream()
                .map(usuario -> new UsuarioRecord(
                        usuario.getNombreUsuario(),
                        usuario.getTipoDocumento(),
                        usuario.getDocumento(),
                        usuario.getApellido(),
                        usuario.getNombre(),
                        usuario.getContrasena()
                ))
                .toList();
    }

    @Override
    public void modificarUsuario(UsuarioRecord usuarioRecord) {
        Usuario usuario = repository.findByTipoDocumentoAndDocumento(usuarioRecord.tipoDocumento(), usuarioRecord.documento());
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado.");
        }

        usuario.setTipoDocumento(usuarioRecord.tipoDocumento());
        usuario.setDocumento(usuarioRecord.documento());
        usuario.setApellido(usuarioRecord.apellido());
        usuario.setNombre(usuarioRecord.nombre());
        if (usuarioRecord.contrasena() != null && !usuarioRecord.contrasena().isBlank()) {
            usuario.setContrasena(passwordEncoder.encode(usuarioRecord.contrasena()));
        }

        repository.save(usuario);
    }

    //Login
    @Override
    public String loginDeUsuario(String username, String password) {

        /*if ("root".equals(username) && "root".equals(password)) {
            String token = Jwts.builder()
                    .setSubject("root")
                    .claim("rol", "root")
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour expiration
                    .signWith(SignatureAlgorithm.HS256, jwtSecretKey) // Replace "secret_key" with a secure key
                    .compact();

            return token;
        }*/

        // Retrieve the user by username
        Usuario usuario = repository.findByNombreUsuario(username);

        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado.");
        }

        // Verify the password
        if (!passwordEncoder.matches(password, usuario.getContrasena())) {
            throw new IllegalArgumentException("Contraseña incorrecta.");
        }

        // Generate a token (for example, a JWT or session token)
        // Generate a JWT token
        String token = Jwts.builder()
                .setSubject(username)
                .claim("rol", usuario.getRol())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour expiration
                .signWith(SignatureAlgorithm.HS256, jwtSecretKey) // Replace "secret_key" with a secure key
                .compact();

        return token; // Replace with actual token generation logic
    }
}


