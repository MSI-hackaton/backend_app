package dev.msi_hackaton.backend_app.mapper;

import dev.msi_hackaton.backend_app.dao.entities.Project;
import dev.msi_hackaton.backend_app.dao.entities.ProjectPhoto;
import dev.msi_hackaton.backend_app.dto.request.ProjectCreateDto;
import dev.msi_hackaton.backend_app.dto.response.ProjectResponseDto;
import dev.msi_hackaton.backend_app.dto.nested.ProjectPhotoNestedDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    Project toEntity(ProjectCreateDto projectCreateDto);

    @Mapping(target = "photos", source = "projectPhotos", qualifiedByName = "mapProjectPhotosToNestedDto")
    ProjectResponseDto toDto(Project project);

    @Named("mapProjectPhotosToNestedDto")
    default List<ProjectPhotoNestedDto> mapProjectPhotosToNestedDto(List<ProjectPhoto> projectPhotos) {
        if (projectPhotos == null) {
            return List.of();
        }
        return projectPhotos.stream()
                .map(photo -> {
                    ProjectPhotoNestedDto dto = new ProjectPhotoNestedDto();
                    dto.setId(photo.getId());
                    dto.setUrl(photo.getPhoto().getUrl());
                    dto.setDescription(photo.getDescription());
                    dto.setSortOrder(photo.getSortOrder());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
