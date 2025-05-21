package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.mappers;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos.CriterioEvaluacionDto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.CriterioEvaluacion;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CriterioEvaluacionMapper {

    @Mapping(source = "idSustentacion", target = "sustentacion.id")
    CriterioEvaluacion toEntity(CriterioEvaluacionDto criterioEvaluacionDto);

    @Mapping(source = "sustentacion.id", target = "idSustentacion")
    CriterioEvaluacionDto toDto(CriterioEvaluacion criterioEvaluacion);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    CriterioEvaluacion partialUpdate(CriterioEvaluacionDto criterioEvaluacionDto, @MappingTarget CriterioEvaluacion criterioEvaluacion);
}