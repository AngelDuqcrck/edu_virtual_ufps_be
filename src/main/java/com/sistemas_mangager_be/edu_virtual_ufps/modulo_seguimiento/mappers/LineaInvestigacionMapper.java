package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.mappers;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos.LineaInvestigacionDto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.LineaInvestigacion;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface LineaInvestigacionMapper {
    LineaInvestigacion toEntity(LineaInvestigacionDto lineaInvestigacionDto);

    LineaInvestigacionDto toDto(LineaInvestigacion lineaInvestigacion);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    LineaInvestigacion partialUpdate(LineaInvestigacionDto lineaInvestigacionDto, @MappingTarget LineaInvestigacion lineaInvestigacion);
}