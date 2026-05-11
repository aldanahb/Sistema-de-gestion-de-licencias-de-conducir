package com.gestionlicencias.gestionlicenciasconducir.service;

import java.util.List;

import com.gestionlicencias.gestionlicenciasconducir.dto.UsuarioRecord;
import com.gestionlicencias.gestionlicenciasconducir.model.TipoDocumento;
import com.gestionlicencias.gestionlicenciasconducir.model.Usuario;

public interface UsuarioService {

    public Usuario buscarUsuarioPorId(Integer idUsuario);
    public void registrarUsuario(UsuarioRecord usuarioRecord);
    public List<UsuarioRecord> buscarUsuario(TipoDocumento tipoDocumento, String documento, String nombreUsuario);
    public void modificarUsuario(UsuarioRecord usuarioRecord);
    public String loginDeUsuario(String username, String password);
}
