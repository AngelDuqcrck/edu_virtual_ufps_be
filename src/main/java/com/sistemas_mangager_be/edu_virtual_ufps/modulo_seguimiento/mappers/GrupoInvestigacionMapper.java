package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.mappers;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos.GrupoInvestigacionDto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.GrupoInvestigacion;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface GrupoInvestigacionMapper {
    GrupoInvestigacion toEntity(GrupoInvestigacionDto grupoInvestigacionDto);

    GrupoInvestigacionDto toDto(GrupoInvestigacion grupoInvestigacion);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    GrupoInvestigacion partialUpdate(GrupoInvestigacionDto grupoInvestigacionDto, @MappingTarget GrupoInvestigacion grupoInvestigacion);
}