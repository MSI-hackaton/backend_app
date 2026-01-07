package dev.msi_hackaton.backend_app.mapper;

import dev.msi_hackaton.backend_app.dao.entities.ConstructionStage;
import dev.msi_hackaton.backend_app.dto.response.ConstructionStageResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ConstructionStageMapper {

    @Mapping(source = "request.id", target = "requestId")
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "specialist.id", target = "specialistId")
    ConstructionStageResponseDto toResponseDto(ConstructionStage stage);
}
