package com.sistemas_mangager_be.edu_virtual_ufps.services.implementations;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Soporte;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.SoporteRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.FileDownloadInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class S3Service {

    @Autowired
    private AmazonS3 amazonS3;
    
    @Autowired
    private SoporteRepository soporteRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    // Subir archivo y guardar metadata en BD
    public Soporte uploadFile(MultipartFile file, String tipoDocumento) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        String newFilename = UUID.randomUUID() + "." + extension;
        String folder =  tipoDocumento + "/";
        String fullPath = folder + newFilename;

        // Subir a S3
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());
        
        amazonS3.putObject(new PutObjectRequest(bucketName, fullPath, file.getInputStream(), metadata));
        
        // Construir URL (puedes usar CloudFront si lo tienes configurado)
        String fileUrl = amazonS3.getUrl(bucketName, fullPath).toString();

        // Crear y guardar entidad Soporte
        Soporte soporte = Soporte.builder()
                .url_s3(fileUrl)
                .fecha_subida(new Date())
                .ruta(fullPath)
                .nombre_archivo(originalFilename)
                .peso(formatFileSize(file.getSize()))
                .tamano_bytes(file.getSize())
                .tipo(tipoDocumento)
                .mime_type(file.getContentType())
                .extension(extension)
                .build();

        return soporteRepository.save(soporte);
    }

    // Eliminar archivo de S3 y BD
    public void deleteFile(Integer soporteId) {
        Soporte soporte = soporteRepository.findById(soporteId)
                .orElseThrow(() -> new RuntimeException("Soporte no encontrado"));
        
        amazonS3.deleteObject(bucketName, soporte.getRuta());
        soporteRepository.delete(soporte);
    }

    // Obtener metadata desde BD
    public Soporte getFileMetadata(Integer soporteId) {
        return soporteRepository.findById(soporteId)
                .orElseThrow(() -> new RuntimeException("Soporte no encontrado"));
    }

    // Listar archivos desde BD (más eficiente que listar desde S3)
    public List<Soporte> listFilesByType(String tipo) {
        return soporteRepository.findByTipo(tipo);
    }

    // Generar URL firmada
    public String generatePresignedUrl(Integer soporteId) {
        Soporte soporte = getFileMetadata(soporteId);
        
        java.util.Date expiration = new java.util.Date();
        long expTimeMillis = expiration.getTime() + 1000 * 60 * 60; // 1 hora
        
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, soporte.getRuta())
                        .withMethod(HttpMethod.GET)
                        .withExpiration(new Date(expTimeMillis));
        
        return amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString();
    }
    
    /**
     * Descarga un archivo directamente desde S3
     * @param soporteId ID del soporte del archivo a descargar
     * @return ResponseInputStream con el contenido del archivo
     */
    public S3ObjectInputStream downloadFile(Integer soporteId) {
        Soporte soporte = getFileMetadata(soporteId);
        
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, soporte.getRuta());
        S3Object s3Object = amazonS3.getObject(getObjectRequest);
        
        return s3Object.getObjectContent();
    }
    
    /**
     * Obtiene la información necesaria para descargar un archivo
     * @param soporteId ID del soporte
     * @return Objeto con información para la descarga
     */
    public FileDownloadInfo getFileDownloadInfo(Integer soporteId) {
        Soporte soporte = getFileMetadata(soporteId);
        
        return FileDownloadInfo.builder()
                .fileName(soporte.getNombre_archivo())
                .contentType(soporte.getMime_type())
                .contentLength(soporte.getTamano_bytes())
                .soporteId(soporteId)
                .build();
    }

    // Método auxiliar para formatear tamaño
    private String formatFileSize(long size) {
        if (size <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return String.format("%.1f %s", size / Math.pow(1024, digitGroups), units[digitGroups]);
    }
}