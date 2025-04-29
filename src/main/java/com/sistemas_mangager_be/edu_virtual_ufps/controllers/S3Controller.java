package com.sistemas_mangager_be.edu_virtual_ufps.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Soporte;
import com.sistemas_mangager_be.edu_virtual_ufps.services.implementations.S3Service;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.FileDownloadInfo;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/s3")
public class S3Controller {

    @Autowired
    private S3Service s3Service;

    @PostMapping("/upload")
    public ResponseEntity<Soporte> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("tipo") String tipoDocumento) throws IOException {
        Soporte soporte = s3Service.uploadFile(file, tipoDocumento);
        return ResponseEntity.ok(soporte);
    }

    @GetMapping("/list/{tipo}")
    public ResponseEntity<List<Soporte>> listFilesByType(@PathVariable String tipo) {
        return ResponseEntity.ok(s3Service.listFilesByType(tipo));
    }

    @GetMapping("/metadata/{id}")
    public ResponseEntity<Soporte> getFileMetadata(@PathVariable Integer id) {
        return ResponseEntity.ok(s3Service.getFileMetadata(id));
    }

    @GetMapping("/presigned-url/{id}")
    public ResponseEntity<String> generatePresignedUrl(@PathVariable Integer id) {
        return ResponseEntity.ok(s3Service.generatePresignedUrl(id));
    }
    
    /**
     * Endpoint para descargar un archivo directamente desde S3
     * @param id ID del soporte
     * @return ResponseEntity con el archivo como stream
     */
    @GetMapping("/download/{id}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable Integer id) {
        // Obtener información del archivo
        FileDownloadInfo fileInfo = s3Service.getFileDownloadInfo(id);
        
        // Obtener el stream del archivo desde S3
        S3ObjectInputStream objectContent = s3Service.downloadFile(id);
        
        // Codificar el nombre del archivo para el header
        String encodedFileName;
        try {
            encodedFileName = URLEncoder.encode(fileInfo.getFileName(), StandardCharsets.UTF_8.toString())
                    .replace("+", "%20");
        } catch (Exception e) {
            encodedFileName = "download";
        }
        
        // Configurar headers para la descarga
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName);
        headers.add(HttpHeaders.CONTENT_TYPE, fileInfo.getContentType());
        headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileInfo.getContentLength()));
        
        // Devolver el archivo como stream con los headers adecuados
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType(fileInfo.getContentType()))
                .body(new InputStreamResource(objectContent));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Integer id) {
        s3Service.deleteFile(id);
        return ResponseEntity.noContent().build();
    }
}