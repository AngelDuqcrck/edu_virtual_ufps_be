package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.controllers;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos.DocumentoDto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos.RetroalimentacionDto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.enums.TipoDocumento;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.services.DocumentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documentos")
public class DocumentoController {

    private final DocumentoService documentoService;

    public DocumentoController(DocumentoService documentoService) {
        this.documentoService = documentoService;
    }

    @PostMapping("/{idProyecto}")
    public ResponseEntity<DocumentoDto> subirDocumento(
            @PathVariable Integer idProyecto,
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("tipoDocumento") TipoDocumento tipoDocumento,
            @RequestParam("tag") String tag
    ) {
        return ResponseEntity.ok(documentoService.guardarDocumento(idProyecto, archivo, tipoDocumento, tag));
    }

    @GetMapping("/proyecto/{idProyecto}")
    public ResponseEntity<List<DocumentoDto>> listarPorProyecto(@PathVariable Integer idProyecto,
                                                                @RequestParam(value = "tipoDocumento", required = false) TipoDocumento tipoDocumento) {
        return ResponseEntity.ok(documentoService.listarPorProyecto(idProyecto, tipoDocumento));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDocumento(@PathVariable Integer id) {
        documentoService.eliminarDocumento(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/sustentacion")
    public ResponseEntity<DocumentoDto> agregarDocumentoSustentacion(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam TipoDocumento tipoDocumento,
            @RequestParam Integer idSustentacion,
            @RequestParam("tag") String tag) {
        return ResponseEntity.ok(documentoService.agregarDocumentoaSustentacion(archivo, tipoDocumento, idSustentacion, tag));
    }

    @GetMapping("/sustentacion/{idSustentacion}")
    public ResponseEntity<List<DocumentoDto>> listarDocumentosSustentacion(@PathVariable Integer idSustentacion) {
        return ResponseEntity.ok(documentoService.listarDocumentosPorSustentacion(idSustentacion));
    }

    @PostMapping("/coloquio")
    public ResponseEntity<DocumentoDto> agregarDocumentoColoquio(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam TipoDocumento tipoDocumento,
            @RequestParam Integer idColoquio,
            @RequestParam("tag") String tag) {
        return ResponseEntity.ok(documentoService.agregarDocumentoaColoquio(archivo, tipoDocumento, idColoquio, tag));
    }

    @GetMapping("/coloquio/{idColoquio}")
    public ResponseEntity<List<DocumentoDto>> listarDocumentosColoquioEstudiante(@PathVariable Integer idColoquio, @RequestParam(required = false) Integer idEstudiante) {
        return ResponseEntity.ok(documentoService.listarDocumentosPorColoquioEstudiante(idColoquio, idEstudiante));
    }

    @PostMapping("/retroalimentacion")
    public ResponseEntity<RetroalimentacionDto> agregarRetroalimentacionADocumentos(@RequestBody RetroalimentacionDto retroalimentacionDto) {
        return ResponseEntity.ok(documentoService.agregarRetroalimentacionADocumentos(retroalimentacionDto));
    }

    @PutMapping("/retroalimentacion")
    public ResponseEntity<RetroalimentacionDto> editarRetroalimentacion(@RequestBody RetroalimentacionDto retroalimentacionDto) {
        return ResponseEntity.ok(documentoService.editarRetroalimentacion(retroalimentacionDto));
    }

    @DeleteMapping("/retroalimentacion/{id}")
    public ResponseEntity<Void> eliminarRetroalimentacion(@PathVariable Integer id) {
        documentoService.eliminarRetroalimentacion(id);
        return ResponseEntity.noContent().build();
    }
}
