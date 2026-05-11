package com.gestionlicencias.gestionlicenciasconducir.controller;

import com.gestionlicencias.gestionlicenciasconducir.mapper.TitularMapper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.gestionlicencias.gestionlicenciasconducir.service.TitularService;
import com.gestionlicencias.gestionlicenciasconducir.dto.TitularRecord;
import com.gestionlicencias.gestionlicenciasconducir.model.TipoDocumento;
import com.gestionlicencias.gestionlicenciasconducir.model.Titular;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

/*Estas son anotaciones para la documentación de la API
 * Ingresar a http://localhost:8080/swagger-ui/index.html para ver la documentación de la API
*/
@Tag(name = "Titular Controller", description = "Operaciones para la gestión de titulares")
@Controller
@RequestMapping("/api/titulares")
public class TitularController {

    private final TitularService service;
    private final TitularMapper titularMapper;

    @Autowired
    public TitularController(TitularService service, TitularMapper titularMapper) {
        this.service = service;
        this.titularMapper = titularMapper;
    }

    @Operation(summary = "Registrar un titular", 
                description = "Registra un nuevo titular en la base de datos", 
                responses = {
                    @ApiResponse(responseCode = "201", description = "Titular registrado correctamente"),
                    @ApiResponse(responseCode = "400", description = "Error al registrar el titular"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
                }) 
    
    @PostMapping("/registrar")
    public ResponseEntity<Void> registrarTitular(@RequestBody @Valid TitularRecord titularRecord) {
        service.registrarTitular(titularRecord);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(
            summary = "Buscar un Titular",
            description = "Busca un titular por dtipo y numero de documento",
            responses = {
                    @ApiResponse(responseCode = "202", description = "Titular no encontrado"),
                    @ApiResponse(responseCode = "404", description = "Titular no encontrado"),
                    @ApiResponse(responseCode = "400", description = "Parámetros Inválidos")
            }
    )
    @GetMapping("/buscar/titular")
    public ResponseEntity<TitularRecord> buscarTitular(
            @RequestParam TipoDocumento tipoDocumento,
            @RequestParam String documento
    ){
        try{
            TitularRecord titularRecord = service.buscarTitular(tipoDocumento, documento);
            return ResponseEntity.ok(titularRecord);
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            //Alternativa
            //return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/modificar/{tipoDocumento}/{documento}")
    public String mostrarFormularioModificar(
            @PathVariable TipoDocumento tipoDocumento,
            @PathVariable String documento,
            Model model) {

        try {
            Titular titular = service.buscarTitularDocumento(tipoDocumento, documento);
            if (titular == null) {
                throw new EntityNotFoundException("Titular no encontrado");
            }
            model.addAttribute("titular", titular);
            return "modificacionTitular"; 

        } catch (EntityNotFoundException e) {
            return "error/404"; 
        } catch (Exception e) {
            return "error/500";
        }
    }

   @PutMapping("/modificar/{tipoDocumento}/{documento}")
    public ResponseEntity<Void> modificarTitular(
            @PathVariable TipoDocumento tipoDocumento,
            @PathVariable String documento,
            @RequestBody @Valid TitularRecord titularModificado) {

        try {
            service.actualizarTitular(tipoDocumento, documento, titularModificado);
            return ResponseEntity.noContent().build();

        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/lista", produces = "application/json")
    @ResponseBody
    @Operation(summary   = "Listar / Buscar Titulares",
            description = "Lista todos los titulares o filtra por apellido, tipoDocumento y/o documento",
            responses = {
                @ApiResponse(responseCode = "200", description = "Lista obtenida"),
                @ApiResponse(responseCode = "500", description = "Error interno")
            })
    public ResponseEntity<List<TitularRecord>> listarOBuscarTitulares(
            @RequestParam(required = false) String apellido,
            @RequestParam(required = false) TipoDocumento tipoDocumento,
            @RequestParam(required = false) String documento) {

        List<TitularRecord> resultado = service.buscarTitulares(apellido, tipoDocumento, documento)
                                            .stream()
                                            .map(titularMapper::toRecord)
                                            .toList();

        return ResponseEntity.ok(resultado);
    }

    //Endpoints para el front
    @GetMapping
    public String mostrarMenu() {  return "menuTitular";    }

    @GetMapping("/registroTitular")
    public String mostrarFormulario() {    return "registroTitular";    }

    @GetMapping("/modificar")
    public String mostrarFormularioModificar() {   return "modificarTitular";    }

    //eliminar
    @GetMapping("/eliminar")
    public String mostrarFormularioEliminar() {   return "eliminarTitular";    }

    //listar
    @GetMapping("/buscarTitulares")
    public String mostrarFormularioListar() {   return "listaTitulares";    }

}
