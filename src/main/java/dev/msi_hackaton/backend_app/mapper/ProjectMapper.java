package dev.msi_hackaton.backend_app.mapper;

import dev.msi_hackaton.backend_app.dao.entities.*;
import dev.msi_hackaton.backend_app.dto.request.ProjectCreateDto;
import dev.msi_hackaton.backend_app.dto.request.ProjectPhotoCreateDto;
import dev.msi_hackaton.backend_app.dto.response.ProjectPhotoResponseDto;
import dev.msi_hackaton.backend_app.dto.response.ProjectResponseDto;
import dev.msi_hackaton.backend_app.dto.nested.PhotoNestedDto;
import dev.msi_hackaton.backend_app.dto.nested.ProjectPhotoNestedDto;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface ProjectMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "projectPhotos", ignore = true)
    Project toEntity(ProjectCreateDto projectCreateDto);

    @Mapping(target = "photos", source = "projectPhotos", qualifiedByName = "mapProjectPhotosToNestedDto")
    ProjectResponseDto toDto(Project project);

    @Mapping(target = "photo", source = "photo", qualifiedByName = "mapPhotoToNestedDto")
    ProjectPhotoResponseDto toProjectPhotoDto(ProjectPhoto projectPhoto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "photo", ignore = true)
    ProjectPhoto toEntity(ProjectPhotoCreateDto projectPhotoCreateDto);

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

    @Named("mapPhotoToNestedDto")
    default PhotoNestedDto mapPhotoToNestedDto(Photo photo) {
        if (photo == null) {
            return null;
        }
        PhotoNestedDto dto = new PhotoNestedDto();
        dto.setId(photo.getId());
        dto.setUrl(photo.getUrl());
        return dto;
    }
}