package dev.msi_hackaton.backend_app.mapper;

import dev.msi_hackaton.backend_app.dao.entities.ConstructionRequest;
import dev.msi_hackaton.backend_app.dto.request.ConstructionRequestCreateDto;
import dev.msi_hackaton.backend_app.dto.response.ConstructionRequestResponseDto;
import dev.msi_hackaton.backend_app.dto.response.ConstructionRequestStatusResponseDto;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface ConstructionRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "anonymousFullName", source = "fullName")
    @Mapping(target = "anonymousEmail", source = "email")
    @Mapping(target = "anonymousPhone", source = "phone")
    ConstructionRequest toEntity(ConstructionRequestCreateDto requestCreateDto);

    @Mapping(target = "projectId", source = "project.id")
    ConstructionRequestResponseDto toDto(ConstructionRequest constructionRequest);

    @Mapping(target = "projectId", source = "project.id")
    ConstructionRequestStatusResponseDto toStatusDto(ConstructionRequest constructionRequest);
}