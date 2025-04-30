package com.sistemas_mangager_be.edu_virtual_ufps.shared.responses;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDownloadInfo {
    private String fileName;
    private String contentType;
    private Long contentLength;
    private Integer soporteId;
}