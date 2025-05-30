package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.mappers;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Sustentacion;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos.SustentacionDto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.enums.TipoSustentacion;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface SustentacionMapper {
    @Mapping(source = "idProyecto", target = "proyecto.id")
    @Mapping(source = "tipoSustentacion", target = "tipoSustentacion", qualifiedByName = "stringToEnum")
    Sustentacion toEntity(SustentacionDto sustentacionDto);

    @Mapping(source = "proyecto.id", target = "idProyecto")
    @Mapping(source = "tipoSustentacion", target = "tipoSustentacion", qualifiedByName = "enumToString")
    SustentacionDto toDto(Sustentacion sustentacion);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Sustentacion partialUpdate(SustentacionDto sustentacionDto, @MappingTarget Sustentacion sustentacion);

    @Named("enumToString")
    static String mapEnumToString(TipoSustentacion tipo) {
        return tipo != null ? tipo.name() : null;
    }

    @Named("stringToEnum")
    static TipoSustentacion mapStringToEnum(String tipo) {
        return tipo != null ? TipoSustentacion.valueOf(tipo) : null;
    }
}