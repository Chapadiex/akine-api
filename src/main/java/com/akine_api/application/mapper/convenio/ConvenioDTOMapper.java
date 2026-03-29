package com.akine_api.application.mapper.convenio;

import com.akine_api.application.dto.convenio.ArancelDTO;
import com.akine_api.application.dto.convenio.ConvenioDTO;
import com.akine_api.application.dto.convenio.ConvenioVersionDTO;
import com.akine_api.application.dto.convenio.PrestacionDTO;
import com.akine_api.domain.model.convenio.Arancel;
import com.akine_api.domain.model.convenio.Convenio;
import com.akine_api.domain.model.convenio.ConvenioVersion;
import com.akine_api.domain.model.convenio.Prestacion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConvenioDTOMapper {

    PrestacionDTO toDto(Prestacion p);

    Prestacion toDomain(PrestacionDTO dto);

    @Mapping(target = "vigenciaDesde", expression = "java(a.getVigenciaDesde() != null ? a.getVigenciaDesde().toString() : null)")
    @Mapping(target = "vigenciaHasta", expression = "java(a.getVigenciaHasta() != null ? a.getVigenciaHasta().toString() : null)")
    @Mapping(target = "coseguroTipo", expression = "java(a.getCoseguroTipo() != null ? a.getCoseguroTipo().name() : null)")
    ArancelDTO toDto(Arancel a);

    @Mapping(target = "vigenciaDesde", expression = "java(v.getVigenciaDesde() != null ? v.getVigenciaDesde().toString() : null)")
    @Mapping(target = "vigenciaHasta", expression = "java(v.getVigenciaHasta() != null ? v.getVigenciaHasta().toString() : null)")
    @Mapping(target = "estado", expression = "java(v.getEstado() != null ? v.getEstado().name() : null)")
    @Mapping(target = "creadoAt", expression = "java(v.getCreadoAt() != null ? v.getCreadoAt().toString() : null)")
    ConvenioVersionDTO toDto(ConvenioVersion v);

    @Mapping(target = "modalidad", expression = "java(c.getModalidad() != null ? c.getModalidad().name() : null)")
    ConvenioDTO toDto(Convenio c);
}
