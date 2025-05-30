package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.mappers;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos.ColoquioDto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Coloquio;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ColoquioMapper {


    @Mapping(source = "grupoCohorteId", target = "grupoCohorte.id")
    Coloquio toEntity(ColoquioDto coloquioDto);

    @Mapping(source = "grupoCohorte.id", target = "grupoCohorteId")
    @Mapping(source = "grupoCohorte.grupoId.nombre", target = "grupo")
    @Mapping(source = "grupoCohorte.grupoId.materiaId.nombre", target = "materia")
    ColoquioDto toDto(Coloquio coloquio);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Coloquio partialUpdate(ColoquioDto coloquioDto, @MappingTarget Coloquio coloquio);
}