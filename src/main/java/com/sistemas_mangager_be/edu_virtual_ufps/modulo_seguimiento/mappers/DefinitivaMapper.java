package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.mappers;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos.DefinitivaDto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Definitiva;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface DefinitivaMapper {
    @Mapping(source = "idProyecto", target = "proyecto.id")
    Definitiva toEntity(DefinitivaDto definitivaDto);

    @Mapping(source = "proyecto.id", target = "idProyecto")
    DefinitivaDto toDto(Definitiva definitiva);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Definitiva partialUpdate(DefinitivaDto definitivaDto, @MappingTarget Definitiva definitiva);
}