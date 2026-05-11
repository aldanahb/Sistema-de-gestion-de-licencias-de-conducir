package com.gestionlicencias.gestionlicenciasconducir.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gestionlicencias.gestionlicenciasconducir.model.TipoDocumento;
import com.gestionlicencias.gestionlicenciasconducir.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Usuario findByIdUsuario(Integer idUsuario);
    Usuario findByNombreUsuario(String nombreUsuario);
    boolean existsByNombreUsuario(String nombreUsuario);
    Usuario findByTipoDocumentoAndDocumento(TipoDocumento tipoDocumento, String documento);

    @Query("SELECT u FROM Usuario u WHERE " +
           "(:tipoDocumento IS NULL OR u.tipoDocumento = :tipoDocumento) AND " +
           "(:documento IS NULL OR u.documento = :documento) AND " +
           "(:nombreUsuario IS NULL OR u.nombreUsuario = :nombreUsuario)")
    List<Usuario> buscarPorFiltros(@Param("tipoDocumento") TipoDocumento tipoDocumento,
                                   @Param("documento") String documento,
                                   @Param("nombreUsuario") String nombreUsuario);
}
