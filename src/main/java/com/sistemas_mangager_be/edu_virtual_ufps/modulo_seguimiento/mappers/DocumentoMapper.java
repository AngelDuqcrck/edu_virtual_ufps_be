package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.mappers;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Documento;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.enums.TipoDocumento;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos.DocumentoDto;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface DocumentoMapper {

    @Mapping(source = "idProyecto", target = "proyecto.id")
    @Mapping(source = "tipoDocumento", target = "tipoDocumento", qualifiedByName = "stringToEnum")
    Documento toEntity(DocumentoDto documentoDto);

    @Mapping(source = "proyecto.id", target = "idProyecto")
    @Mapping(source = "tipoDocumento", target = "tipoDocumento", qualifiedByName = "enumToString")
    DocumentoDto toDto(Documento documento);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Documento partialUpdate(DocumentoDto documentoDto, @MappingTarget Documento documento);

    @Named("enumToString")
    static String mapEnumToString(TipoDocumento tipo) {
        return tipo != null ? tipo.name() : null;
    }

    @Named("stringToEnum")
    static TipoDocumento mapStringToEnum(String tipo) {
        return tipo != null ? TipoDocumento.valueOf(tipo) : null;
    }
}