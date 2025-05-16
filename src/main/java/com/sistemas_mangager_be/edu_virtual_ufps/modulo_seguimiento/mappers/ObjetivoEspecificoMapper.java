package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.mappers;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos.ObjetivoEspecificoDto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.ObjetivoEspecifico;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ObjetivoEspecificoMapper {
    ObjetivoEspecifico toEntity(ObjetivoEspecificoDto objetivoEspecificoDto);

    ObjetivoEspecificoDto toDto(ObjetivoEspecifico objetivoEspecifico);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(ObjetivoEspecificoDto objetivoEspecificoDto, @MappingTarget ObjetivoEspecifico objetivoEspecifico);
}