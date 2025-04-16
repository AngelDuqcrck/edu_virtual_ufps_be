package com.sistemas_mangager_be.edu_virtual_ufps.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Soporte;
import com.sistemas_mangager_be.edu_virtual_ufps.services.implementations.S3Service;

import java.io.IOException;
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

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Integer id) {
        s3Service.deleteFile(id);
        return ResponseEntity.noContent().build();
    }
}