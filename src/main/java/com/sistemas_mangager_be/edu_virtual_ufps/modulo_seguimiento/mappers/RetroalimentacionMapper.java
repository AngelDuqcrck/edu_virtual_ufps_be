package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.mappers;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos.RetroalimentacionDto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Retroalimentacion;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface RetroalimentacionMapper {

    @Mapping(target = "usuario.id", source = "usuarioId")
    @Mapping(target = "documento.id", source = "documentoId")
    Retroalimentacion toEntity(RetroalimentacionDto retroalimentacionDto);

    @Mapping(target = "usuarioId", source = "usuario.id")
    @Mapping(target = "documentoId", source = "documento.id")
    @Mapping(target = "nombreUsuario", source = "usuario.nombreCompleto")
    @Mapping(target = "fotoUsuario", source = "usuario.fotoUrl")
    RetroalimentacionDto toDto(Retroalimentacion retroalimentacion);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Retroalimentacion partialUpdate(RetroalimentacionDto retroalimentacionDto, @MappingTarget Retroalimentacion retroalimentacion);
}